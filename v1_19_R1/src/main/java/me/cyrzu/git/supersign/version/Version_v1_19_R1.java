package me.cyrzu.git.supersign.version;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.cyrzu.git.supersign.*;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Version_v1_19_R1 implements VersionHandler {

    @NotNull
    private final SuperSign superSign;

    private final Map<UUID, Channel> channels = new HashMap<>();

    private final Class<?> PLAYER_CONNECTION = Reflex.getClass("net.minecraft.server.network", "PlayerConnection");
    private final Class<?> NETWORK_MANAGER = Reflex.getClass("net.minecraft.network", "NetworkManager");

    private final Field CONNECTION = Reflex.getField(PLAYER_CONNECTION, "b", true);
    private final Field CHANNEL = Reflex.getField(NETWORK_MANAGER, "m", true);

    public Version_v1_19_R1(@NotNull SuperSign superSign) {
        this.superSign = superSign;
    }

    @Override
    public void onJoin(@NotNull Player player) {
        Object handle = ((CraftPlayer) player).getHandle().b;
        Object fieldValue = Reflex.getFieldValue(handle, CONNECTION, Object.class);
        Channel channel = Reflex.getFieldValue(fieldValue, CHANNEL, Channel.class);

        if(channel.pipeline().get(SuperSign.ID) != null) {
            channels.put(player.getUniqueId(), channel);
            return;
        }

        SignPacket signPacket = new SignPacket(player, superSign);
        channel.pipeline().addAfter("decoder", SuperSign.ID, signPacket);
        channels.put(player.getUniqueId(), channel);
    }

    @Override
    public void onQuit(@NotNull Player player) {
        superSign.removePlayer(player);
        uninject(player.getUniqueId());
    }

    @Override
    public void uninject() {
        for (UUID uuid : List.copyOf(channels.keySet())) {
            uninject(uuid);
        }
    }

    @Override
    public void uninject(@NotNull UUID uuid) {
        Channel channel = channels.get(uuid);
        if(channel != null) {
            try {
                channel.pipeline().remove(SuperSign.ID);
            } catch (Exception ignore) {}
            channels.remove(uuid);
        }
    }

    @Override
    public void read(@NotNull Player player, @NotNull String[] lines) {
        superSign.read(player, lines);
    }

    @Override
    public void sendSign(@NotNull Player player, @NotNull SuperSignBuilder signBuilder) {
        Block block = player.getLocation().getBlock();
        Location location = block.getLocation();
        final int locY = location.getBlockY();
        location.setY(locY >= -59 ? locY - 4 : locY + 4);

        player.sendBlockChange(location, signBuilder.getColorSign().getBlockData());
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        PlayerConnection connection = (((CraftPlayer)player).getHandle()).b;

        player.sendSignChange(location, signBuilder.getLines(), signBuilder.getDyeColor());
        PacketPlayOutOpenSignEditor openSign = new PacketPlayOutOpenSignEditor(blockPosition);
        connection.a(openSign);

        Consumer<@NotNull String[]> function = signBuilder.getAcceptConsumer();
        superSign.putPlayer(player, new SignHolder(location, function == null ? l -> {} : function));
    }

    private static class SignPacket extends MessageToMessageDecoder<PacketPlayInUpdateSign> {

        @NotNull
        private final SuperSign superSign;

        @NotNull
        private final Player player;

        public SignPacket(@NotNull Player player, @NotNull SuperSign superSign) {
            this.player = player;
            this.superSign = superSign;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, PacketPlayInUpdateSign msg, List<Object> out) {
            Bukkit.getScheduler().runTask(superSign.getInstance(), () -> {
                out.add(msg);
                superSign.read(player, msg.c());
            });
        }
    }

}
