package me.khanh.thesieutocform;

import lombok.Getter;
import me.khanh.thesieutocform.command.ReloadCommand;
import me.khanh.thesieutocform.file.ConfigFile;
import me.khanh.thesieutocform.form.NapTheForm;
import me.khanh.thesieutocform.listener.PlayerListener;
import net.thesieutoc.api.Card;
import net.thesieutoc.api.ThesieutocAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Main class for TheSieuTocForm plugin.
 */
public final class TheSieuTocFormPlugin extends JavaPlugin {
    @Getter
    private ConfigFile configFile;
    @Getter
    private NapTheForm napTheForm;

    @Override
    public void onEnable() {
        // Setup config file
        configFile = new ConfigFile(this);

        // Register reload command
        registerReloadCommand();

        // Validate TheSieuToc compatible
        validateDependencies();

        // Register nap the form
        napTheForm = new NapTheForm(this, configFile.getFormSection());

        // Register player listener
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Helper method to register the reload command.
     */
    private void registerReloadCommand(){
        Objects.requireNonNull(getCommand("thesieutocformreload")).setExecutor(new ReloadCommand(this));
    }

    /**
     * Method to reload the plugin.
     * It reloads the configuration file and nap the form.
     */
    public void reloadPlugin(){
        configFile = new ConfigFile(this);
        napTheForm = new NapTheForm(this, configFile.getFormSection());
    }

    /**
     * Method to get a message from the plugin's configuration.
     *
     * @param key The key of the message to retrieve.
     * @return The message corresponding to the given key.
     */
    public String getMessage(String key){
        return getConfig().getString("messages." + key);
    }

    /**
     * Validates necessary dependencies for plugin functionality.
     * Throws a runtime exception if dependencies are missing.
     */
    @SuppressWarnings("unused")
    public void validateDependencies() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Thesieutoc");
        if (plugin == null || Integer.parseInt(plugin.getDescription().getVersion().substring(1)) < 35) {
            throw new RuntimeException("This plugin requires Thesieutoc plugin version #35 or later to work.");
        }
        try {
            Class<?> theSieuTocAPIClass = Class.forName("net.thesieutoc.api.ThesieutocAPI");
            Method method = ThesieutocAPI.class.getMethod("processCard", Player.class, Card.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Your TheSieuToc plugin version is incompatible. Please contact the author.", e);
        }
    }
}
