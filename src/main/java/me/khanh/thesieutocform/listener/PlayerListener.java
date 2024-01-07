package me.khanh.thesieutocform.listener;

import lombok.Getter;
import me.khanh.thesieutocform.TheSieuTocFormPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.geysermc.floodgate.api.FloodgateApi;

/**
 * Listener class for handling player events related to TheSieuTocForm plugin.
 * This class listens for player command events and triggers the custom card top-up form when the "napthe" command is used by a Floodgate player.
 */
public class PlayerListener implements Listener {
    @Getter
    private final TheSieuTocFormPlugin plugin;

    /**
     * Constructor for the PlayerListener.
     *
     * @param plugin The main plugin instance.
     */
    public PlayerListener(TheSieuTocFormPlugin plugin){
        this.plugin = plugin;
    }

    /**
     * Event handler method for player command events.
     * It checks if the player is using the "napthe" command and is a Floodgate player, then opens the custom card top-up form for the player.
     *
     * @param event The PlayerCommandPreprocessEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        String command = event.getMessage().substring(1);

        if (command.equalsIgnoreCase("napthe")){
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())){
                event.setCancelled(true);
                plugin.getNapTheForm().open(player);
            }
        }
    }
}
