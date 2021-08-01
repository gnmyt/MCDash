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
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        if (!isStringInQuery("name")) return;

        String pluginName = getStringFromQuery("name");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);

        if (plugin == null) {
            response.code(404).message("Plugin not found");
            return;
        }

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
        if (!isStringInBody("name")) return;

        String pluginName = getStringFromBody("name");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);

        if (plugin == null) {
            response.code(404).message("Plugin not found");
            return;
        }

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
        if (!isStringInBody("name")) return;

        String pluginName = getStringFromBody("name");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);

        if (plugin == null) {
            response.code(404).message("Plugin not found");
            return;
        }

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
