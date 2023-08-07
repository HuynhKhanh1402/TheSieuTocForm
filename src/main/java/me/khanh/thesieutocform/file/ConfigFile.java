package me.khanh.thesieutocform.file;

import lombok.Getter;
import me.khanh.thesieutocform.TheSieuTocFormPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Configuration file manager for TheSieuTocForm plugin.
 * This class handles the loading, saving, and updating of the plugin's configuration file.
 */
public class ConfigFile {
    @Getter
    private final TheSieuTocFormPlugin plugin;
    @Getter
    private final File file;
    @Getter
    private final FileConfiguration defConfiguration;
    @Getter
    private final FileConfiguration configuration;
    @Getter
    private final String priceStringFormat;
    @Getter
    private final DecimalFormat priceNumberFormat;
    @Getter
    private final ConfigurationSection formSection;

    /**
     * Constructor for the ConfigFile.
     *
     * @param plugin The main plugin instance.
     */
    public ConfigFile(TheSieuTocFormPlugin plugin){
        this.plugin = plugin;

        try {
            Reader reader = new InputStreamReader(Objects.requireNonNull(plugin.getResource("config.yml")));
            defConfiguration = YamlConfiguration.loadConfiguration(reader);

            file = new File(plugin.getDataFolder(), "config.yml");

            if (!file.exists()) {
                plugin.saveDefaultConfig();
            }

            configuration = YamlConfiguration.loadConfiguration(file);

            int defVersion = defConfiguration.getInt("version");
            int version = configuration.getInt("version", 0);

            if (defVersion > version) {
                update(version);
                configuration.set("version", defVersion);
                save();
            }

            priceStringFormat = configuration.getString("menh-gia.string-format", "&b{price}");
            priceNumberFormat = new DecimalFormat(configuration.getString("menh-gia.number-format", ""));
            formSection = configuration.getConfigurationSection("form");

            if (formSection == null){
                throw new RuntimeException("form section is null");
            }

        } catch (Exception e) {
            throw new RuntimeException(String.format("An error occurred while loading the %s file", "config.yml"), e);
        }
    }

    /**
     * Save the configuration to the file.
     *
     * @throws IOException if an error occurs while saving the configuration.
     */
    public void save() throws IOException {
        configuration.save(file);
    }

    /**
     * Method to handle configuration updates.
     *
     * @param version The current version of the configuration file.
     */
    public void update(int version) {
    }
}
