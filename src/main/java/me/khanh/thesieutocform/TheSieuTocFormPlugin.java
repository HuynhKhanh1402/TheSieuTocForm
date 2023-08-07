package me.khanh.thesieutocform;

import lombok.Getter;
import me.khanh.thesieutocform.command.ReloadCommand;
import me.khanh.thesieutocform.file.ConfigFile;
import me.khanh.thesieutocform.form.NapTheForm;
import me.khanh.thesieutocform.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

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
}
