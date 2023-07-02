package de.gnmyt.mcdash.panel.routes.plugin;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class PluginListRoute extends DefaultHandler {

    @Override
    public String path() {
        return "list";
    }

    /**
     * Gets all plugins of the server
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {

        ArrayBuilder builder = new ArrayBuilder();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getProtectionDomain().getCodeSource() == null) continue;
            String pluginJar = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            String pluginJarNoPath = pluginJar.substring(pluginJar.lastIndexOf(File.separator) + 1);
            
            if (!new File("./plugins/" + pluginJarNoPath).exists()) continue;

            builder.addNode()
                    .add("name", plugin.getName())
                    .add("author", plugin.getDescription().getAuthors().size() == 0 ? null : plugin.getDescription().getAuthors().get(0))
                    .add("description", plugin.getDescription().getDescription())
                    .add("path", pluginJarNoPath)
                    .add("enabled", plugin.isEnabled())
                    .add("version", plugin.getDescription().getVersion())
                    .register();
        }

        response.type(ContentType.JSON).text(builder.toJSON());
    }

}
