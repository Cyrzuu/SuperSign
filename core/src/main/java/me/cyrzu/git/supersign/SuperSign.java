package me.cyrzu.git.supersign;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

public class SuperSign implements Listener {

    @NotNull
    private final VersionHandler handler;

    public SuperSign(@NotNull Main plugin) {
        Class<?> aClass = Reflex.getClass("me.cyrzu.git.supersign.version", "Version_" + Version.getCurrent());
        Constructor<?> constructor = Reflex.getConstructor(aClass);
        handler = (VersionHandler) Reflex.invokeConstructor(constructor);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void uninject() {
        handler.uninject();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        handler.onJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        System.out.println("L+WEWQJEQWQWEJO");
        handler.sendSign(event.getPlayer(), lines -> {
            event.getPlayer().sendMessage("o kurwa XD");
            for (String line : lines) {
                event.getPlayer().sendMessage(line);
            }
        });
    }

}
