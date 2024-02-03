package me.cyrzu.git.supersign;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SuperSign {

    @NotNull
    public static String ID = "super-sign";

    @Nullable
    private static SuperSign superSign;

    public static void registerSuperSign(@NotNull JavaPlugin instance) {
        if(superSign != null) {
            return;
        }

        SuperSign.ID = instance.getName() + "_" + "sign-gui";
        SuperSign.superSign = new SuperSign(instance);
    }

    public static SuperSignBuilder build(@NotNull Player player) {
        if(superSign == null) {
            throw new RuntimeException("SuperSign must be registered!");
        }

        if(!player.isOnline()) {
            throw new RuntimeException("Player mus be online!");
        }

        return new SuperSignBuilder(superSign, player);
    }

    @NotNull
    private final VersionHandler handler;

    @Getter
    @NotNull
    private final JavaPlugin instance;

    private final Map<UUID, SignHolder> holders = new HashMap<>();

    private SuperSign(@NotNull JavaPlugin plugin) {
        if(Version.getCurrent() == Version.UNKNOWN) {
            throw new RuntimeException("Server version doent's support this library");
        }

        this.instance = plugin;

        Class<?> aClass = Reflex.getClass("me.cyrzu.git.supersign.version", "Version_" + Version.getCurrent());
        Constructor<?> constructor = Reflex.getConstructor(aClass, SuperSign.class);
        handler = (VersionHandler) Reflex.invokeConstructor(constructor, this);

        Bukkit.getPluginManager().registerEvents(new SuperSignListeners(this), plugin);

        Bukkit.getOnlinePlayers().forEach(handler::onJoin);
    }

    public void putPlayer(@NotNull Player player, @NotNull SignHolder signHolder) {
        holders.put(player.getUniqueId(), signHolder);
    }

    public void removePlayer(@NotNull Player player) {
        holders.remove(player.getUniqueId());
    }

    @Nullable
    public SignHolder getPlayer(@NotNull Player player) {
        return holders.get(player.getUniqueId());
    }

    public void read(@NotNull Player player, @NotNull String[] lines) {
        SignHolder signHolder = getPlayer(player);
        if(signHolder != null) {
            signHolder.applyPlayer(player, lines);
            removePlayer(player);
        }
    }

    void uninject() {
        handler.uninject();
    }

    void onJoin(@NotNull Player player) {
        handler.onJoin(player);
    }

    void onQuit(@NotNull Player player) {
        handler.onQuit(player);
    }

    void open(@NotNull SuperSignBuilder builder) {
        if(!builder.getPlayer().isOnline()) {
            return;
        }

        handler.sendSign(builder.getPlayer(), builder);
    }

}
