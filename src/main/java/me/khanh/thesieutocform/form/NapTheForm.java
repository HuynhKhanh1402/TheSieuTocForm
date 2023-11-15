package me.khanh.thesieutocform.form;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.khanh.thesieutocform.TheSieuTocFormPlugin;
import me.khanh.thesieutocform.file.ConfigFile;
import net.thesieutoc.Thesieutoc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for handling the custom form for card top-up in TheSieuTocForm plugin.
 * This class provides methods to open and handle the custom form for players.
 */
public class NapTheForm {
    @Getter
    private final TheSieuTocFormPlugin plugin;
    @Getter
    private final ConfigurationSection section;
    @Getter
    private final String title;
    @Getter
    private final String header;
    @Getter
    private final String cardType;
    @Getter
    private final String price;
    @Getter
    private final String seri;
    @Getter
    private final String pin;
    @Getter
    private final boolean confirmToggleEnabled;
    @Getter
    private final String confirmToggleText;
    @Getter
    private final String footer;

    private final List<Integer> prices =  Arrays.asList(10000, 20000, 30000, 50000, 100000, 200000, 300000, 500000, 1000000);

    /**
     * Constructor for the NapTheForm.
     *
     * @param plugin The main plugin instance.
     * @param section The configuration section for the form.
     */
    public NapTheForm(TheSieuTocFormPlugin plugin, ConfigurationSection section){
        this.plugin = plugin;
        this.section = section;

        this.title = section.getString("title", "");
        header = formatText(section.getStringList("header"));
        cardType = formatText(section.getStringList("loai-the"));
        price = formatText(section.getStringList("menh-gia"));
        seri = formatText(section.getStringList("seri"));
        pin = formatText(section.getStringList("ma-the"));
        confirmToggleEnabled = section.getBoolean("xac-nhan.enable");
        confirmToggleText = formatText(section.getStringList("xac-nhan.text"));
        footer = formatText(section.getStringList("footer"));
    }

    /**
     * Method to open the custom form for a player to top up their card.
     *
     * @param player The player who will receive the custom form.
     * @throws IllegalArgumentException if the player is a Java player (not Floodgate player).
     */
    public void open(Player player){

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());

        if (floodgatePlayer == null){
            throw new IllegalArgumentException("Player " + player.getName() + " is a Java player.");
        }

        Thesieutoc thesieutoc = JavaPlugin.getPlugin(Thesieutoc.class);
        List<String> cardTypes = thesieutoc.getConfig().getStringList("card.enable");

        CustomForm form = CustomForm
                .builder()
                .title(title)
                .label(header)
                .dropdown(cardType, cardTypes)
                .dropdown(price, prices.stream()
                        .map(integer -> {
                            ConfigFile configFile = plugin.getConfigFile();
                            String formattedNumber = configFile.getPriceNumberFormat().format(integer);
                            return colorize(configFile.getPriceStringFormat().replace("{price}", formattedNumber));
                        }).collect(Collectors.toList()))
                .input(seri)
                .input(pin)
                .optionalToggle(confirmToggleText, false, confirmToggleEnabled)
                .label(footer)
                .validResultHandler((customForm, response) -> {
                    String cardType = cardTypes.get(response.asDropdown());
                    int priceIndex = response.asDropdown();

                    String seri = response.asInput();

                    if (seri == null || seri.isEmpty()){
                        player.sendMessage(colorize(plugin.getMessage("seri-trong")));
                        return;
                    }

                    String pin = response.asInput();

                    if (pin == null || pin.isEmpty()){
                        player.sendMessage(colorize(plugin.getMessage("ma-the-trong")));
                        return;
                    }

                    if (confirmToggleEnabled && response.hasNext() && !response.asToggle()) {
                        player.sendMessage(colorize(plugin.getMessage("chua-xac-nhan")));
                        return;
                    }

                    JsonObject json = thesieutoc.REQUESTS.getOrDefault(player.getName(), new JsonObject());
                    json.addProperty("cardtype", cardType);
                    json.addProperty("cardprice", prices.get(priceIndex));
                    json.addProperty("seri", seri);
                    json.addProperty("pin", pin);


                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        thesieutoc.REQUESTS.put(player.getName(), json);
                        thesieutoc.WEB_REQUEST.send(player);
                    });
                })
                .build();

        floodgatePlayer.sendForm(form);
    }

    /**
     * Format a list of text lines by translating color codes.
     *
     * @param text The list of text lines to be formatted.
     * @return The formatted text with color codes translated.
     */
    private String formatText(List<String> text){
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int size = text.size();
        for (int i = 0; i < size; i++) {
            sb.append(text.get(i));
            if (i < size - 1) {
                sb.append("\n");
            }
        }
        return colorize(sb.toString());
    }

    /**
     * Colorize a text string by translating color codes.
     *
     * @param text The text string to be colorized.
     * @return The colorized text with color codes translated.
     */
    private String colorize(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
