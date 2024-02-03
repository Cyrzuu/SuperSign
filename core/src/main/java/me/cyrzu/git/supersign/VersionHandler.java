package me.cyrzu.git.supersign;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface VersionHandler {

    void run(Player player, Consumer<String[]> lines);

}
