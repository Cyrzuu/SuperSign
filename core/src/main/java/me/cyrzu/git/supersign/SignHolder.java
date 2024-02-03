package me.cyrzu.git.supersign;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SignHolder {

    @NotNull
    private final Location location;

    @NotNull
    private final Consumer<String[]> consumer;

    public SignHolder(@NotNull Location location, @NotNull Consumer<String[]> consumer) {
        this.location = location;
        this.consumer = consumer;
    }

    public void applyPlayer(@NotNull Player player, @NotNull String[] lines) {
        consumer.accept(lines);
        changeBlock(player);
    }

    private void changeBlock(Player player) {
        Block block = location.getBlock();
        player.sendBlockChange(location, block.getType().createBlockData());
    }

}
