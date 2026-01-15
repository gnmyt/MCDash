package de.gnm.mcdash;


import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.controller.AccountController;
import de.gnm.mcdash.api.controller.ActionRegistry;
import de.gnm.mcdash.api.controller.ApiKeyController;
import de.gnm.mcdash.api.controller.ControllerManager;
import de.gnm.mcdash.api.controller.PermissionController;
import de.gnm.mcdash.api.controller.ScheduleController;
import de.gnm.mcdash.api.controller.SSHController;
import de.gnm.mcdash.api.controller.SessionController;
import de.gnm.mcdash.api.controller.WidgetRegistry;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.event.EventDispatcher;
import de.gnm.mcdash.api.handlers.BaseHandler;
import de.gnm.mcdash.api.handlers.StaticHandler;
import de.gnm.mcdash.api.handlers.WebSocketHandler;
import de.gnm.mcdash.api.helper.ScheduleExecutor;
import de.gnm.mcdash.api.http.HTTPMethod;
import de.gnm.mcdash.api.http.RouteMeta;
import de.gnm.mcdash.api.pipes.BasePipe;
import de.gnm.mcdash.api.routes.BaseRoute;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class MCDashLoader {
    private final Map<Class<?>, BasePipe> pipes = new HashMap<>();
    private final List<Feature> availableFeatures = new ArrayList<>();
    private final ControllerManager controllerManager = new ControllerManager();
    private final BaseHandler routeHandler = new BaseHandler(this);
    private final WebSocketHandler webSocketHandler = new WebSocketHandler(this);
    private final EventDispatcher eventDispatcher = new EventDispatcher();
    private final ActionRegistry actionRegistry = new ActionRegistry();
    private final WidgetRegistry widgetRegistry = new WidgetRegistry();
    private String databaseFile = "mcdash.db";
    private File serverRoot = new File(System.getProperty("user.dir"));
    private Undertow httpServer;
    private ScheduleExecutor scheduleExecutor;

    /**
     * Registers a pipe with the given type
     *
     * @param pipeType the type of the pipe
     * @param pipe     the pipe to register
     */
    public void registerPipe(Class<? extends BasePipe> pipeType, BasePipe pipe) {
        pipes.put(pipeType, pipe);
    }

    /**
     * Initializes the server
     */
    private void initialize() {
        registerRoutes();

        controllerManager.setConnection(String.format("jdbc:sqlite:%s", databaseFile));

        PathHandler handler = new PathHandler()
                .addPrefixPath("/api", routeHandler)
                .addPrefixPath("/api/ws", new WebSocketProtocolHandshakeHandler(webSocketHandler))
                .addPrefixPath("/", new StaticHandler());

        httpServer = Undertow.builder().addHttpListener(7867, "0.0.0.0").setHandler(handler).build();

        controllerManager.registerController(AccountController.class);

        controllerManager.registerController(SessionController.class);

        controllerManager.registerController(PermissionController.class);

        controllerManager.registerController(SSHController.class);
        getController(SSHController.class).initialize(getController(AccountController.class), serverRoot);

        controllerManager.registerController(ScheduleController.class);

        controllerManager.registerController(ApiKeyController.class);

        registerFeatures(Feature.UserManagement);

        scheduleExecutor = new ScheduleExecutor(this);
        scheduleExecutor.start();
    }

    /**
     * Registers all routes in the {@link de.gnm.mcdash.api.routes} package
     */
    public void registerRoutes() {
        Reflections reflections = new Reflections(getRoutePackageName());
        reflections.getSubTypesOf(BaseRoute.class).forEach(clazz -> {
            try {
                BaseRoute baseRoute = clazz.getDeclaredConstructor().newInstance();

                baseRoute.setLoader(this);
                baseRoute.setServerRoot(serverRoot);

                for (Method method : clazz.getDeclaredMethods()) {
                    Path routePath = method.getAnnotation(Path.class);
                    if (routePath == null) {
                        continue;
                    }

                    de.gnm.mcdash.api.annotations.Method routeMethod = method.getAnnotation(de.gnm.mcdash.api.annotations.Method.class);

                    RouteMeta routeMeta = new RouteMeta(baseRoute, method, routeMethod != null ? routeMethod.value() : HTTPMethod.GET, routePath.value());
                    routeHandler.registerRoute(routeMeta);
                }
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * Gets the name of the route package
     *
     * @return the name of the route package
     */
    public static String getRoutePackageName() {
        return MCDashLoader.class.getPackage().getName() + ".api.routes";
    }

    /**
     * Starts the server
     */
    public void startup() {
        initialize();

        if (httpServer != null) {
            httpServer.start();
        }
    }

    /**
     * Shuts down the server
     */
    public void shutdown() {
        if (scheduleExecutor != null) {
            scheduleExecutor.stop();
        }

        pipes.clear();

        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
        }
    }

    /**
     * Gets a pipe of the given type
     *
     * @param pipeType the type of the pipe
     * @param <T>      the type of the pipe
     * @return the pipe
     */
    public <T> T getPipe(Class<T> pipeType) {
        BasePipe pipe = pipes.get(pipeType);
        if (pipe == null) {
            throw new IllegalStateException("No handler registered for type: " + pipeType.getName());
        }

        if (pipeType.isInstance(pipe)) {
            return (T) pipe;
        } else {
            throw new IllegalStateException("Registered handler is not of type: " + pipeType.getName());
        }
    }

    /**
     * Gets the event dispatcher
     *
     * @return the event dispatcher
     */
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * Gets the action registry for schedule actions
     *
     * @return the action registry
     */
    public ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    /**
     * Gets the widget registry for dashboard widgets
     *
     * @return the widget registry
     */
    public WidgetRegistry getWidgetRegistry() {
        return widgetRegistry;
    }

    /**
     * Gets a controller of the given type
     *
     * @param controllerType the type of the controller
     * @param <T>            the type of the controller
     * @return the controller
     */
    public <T> T getController(Class<T> controllerType) {
        return controllerManager.getController(controllerType);
    }

    /**
     * Registers a feature
     *
     * @param feature the feature to register
     */
    public void registerFeatures(Feature... feature) {
        availableFeatures.addAll(Arrays.asList(feature));
    }

    /**
     * Gets the available features
     *
     * @return the available features
     */
    public List<Feature> getAvailableFeatures() {
        return availableFeatures;
    }

    /**
     * Sets the database file
     *
     * @param databaseFile the database file
     */
    public void setDatabaseFile(String databaseFile) {
        this.databaseFile = databaseFile;
    }

    /**
     * Sets and creates the server root
     *
     * @param serverRoot the server root
     */
    public void setServerRoot(File serverRoot) {
        if (!serverRoot.exists()) {
            serverRoot.mkdirs();
        }

        if (!serverRoot.isDirectory()) {
            throw new IllegalArgumentException("The server root must be a directory.");
        }

        this.serverRoot = serverRoot;
    }
}
