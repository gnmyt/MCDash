package de.gnmyt.mcdash.panel.routes.plugin;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginRoute extends DefaultHandler {

    /**
     * Gets a plugin by name
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @return the plugin or <code>null</code> if the plugin does not exist
     */
    private Plugin getPlugin(Request request, ResponseController response) {
        if (!isStringInBody(request, response, "name")) return null;

        String pluginName = getStringFromBody(request, "name");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);

        if (plugin == null) {
            response.code(404).message("Plugin not found");
            return null;
        }

        return plugin;
    }

    /**
     * Gets a plugin by name
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        Plugin plugin = getPlugin(request, response);
        if (plugin == null) return;

        String filePath = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String author = plugin.getDescription().getAuthors().size() == 0 ? null : plugin.getDescription().getAuthors().get(0);

        response.json("name=\""+plugin.getName()+"\"", "author=\""+author+"\"",
                "description=\""+plugin.getDescription().getDescription()+"\"", "path=\""+filePath+"\"", "enabled="+plugin.isEnabled());
    }

    /**
     * Enables a plugin
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void post(Request request, ResponseController response) throws Exception {
        Plugin plugin = getPlugin(request, response);
        if (plugin == null) return;

        runSync(() -> {
            try {
                Bukkit.getPluginManager().enablePlugin(plugin);
                response.message("Plugin successfully enabled");
            } catch (Exception e) {
                response.code(500).message("Could not enable the plugin");
            }
        });
    }

    /**
     * Disables a plugin
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        Plugin plugin = getPlugin(request, response);
        if (plugin == null) return;

        runSync(() -> {
            try {
                Bukkit.getPluginManager().disablePlugin(plugin);
                response.message("Plugin successfully disabled");
            } catch (Exception e) {
                response.code(500).message("Could not disable the plugin");
            }
        });
    }

}
