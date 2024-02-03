package me.cyrzu.git.supersign;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public interface VersionHandler {

    void onJoin(Player player);

    void onQuit(Player player);

    void read(Player player, String[] lines);

    void sendSign(Player player, @NotNull Consumer<String[]> function);

    void uninject(@NotNull UUID uuid);

    void uninject();

}
