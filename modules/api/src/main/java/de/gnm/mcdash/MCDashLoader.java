package de.gnm.mcdash;


import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.controller.AccountController;
import de.gnm.mcdash.api.controller.ControllerManager;
import de.gnm.mcdash.api.controller.SessionController;
import de.gnm.mcdash.api.handlers.BaseHandler;
import de.gnm.mcdash.api.handlers.StaticHandler;
import de.gnm.mcdash.api.http.HTTPMethod;
import de.gnm.mcdash.api.http.RouteMeta;
import de.gnm.mcdash.api.pipes.BasePipe;
import de.gnm.mcdash.api.routes.BaseRoute;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MCDashLoader {
    private final Map<Class<?>, BasePipe> pipes = new HashMap<>();
    private final ControllerManager controllerManager = new ControllerManager();
    private final BaseHandler routeHandler = new BaseHandler(controllerManager);
    private String databaseFile = "mcdash.db";
    private File serverRoot = new File(System.getProperty("user.dir"));
    private Undertow httpServer;

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
                .addPrefixPath("/", new StaticHandler());

        httpServer = Undertow.builder().addHttpListener(7867, "0.0.0.0").setHandler(handler).build();

        controllerManager.registerController(AccountController.class);
        controllerManager.registerController(SessionController.class);
    }

    /**
     * Registers all routes in the {@link de.gnm.mcdash.api.routes} package
     */
    public void registerRoutes() {
        Reflections reflections = new Reflections(getRoutePackageName());
        reflections.getSubTypesOf(BaseRoute.class).forEach(clazz -> {
            try {
                BaseRoute baseRoute = clazz.getDeclaredConstructor().newInstance();

                baseRoute.setControllerManager(controllerManager);
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
     * Gets a controller of the given type
     *
     * @param controllerType the type of the controller
     * @param <T>            the type of the controller
     * @return the controller
     */
    public <T> T getController(Class<T> controllerType) {
        return controllerManager.getController(controllerType);
    }

    public void setDatabaseFile(String databaseFile) {
        this.databaseFile = databaseFile;
    }

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
