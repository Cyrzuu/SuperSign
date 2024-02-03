package me.cyrzu.git.supersign;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;

public class SuperSignListeners implements Listener {

    @NotNull
    private final SuperSign superSign;

    SuperSignListeners(@NotNull SuperSign superSign) {
        this.superSign = superSign;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        superSign.onJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        superSign.onQuit(event.getPlayer());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if(!event.getPlugin().equals(superSign.getInstance())) {
            return;
        }

        superSign.uninject();
    }

}
