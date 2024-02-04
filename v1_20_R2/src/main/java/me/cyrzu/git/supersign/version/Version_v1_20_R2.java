package me.cyrzu.git.supersign.version;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.cyrzu.git.supersign.*;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Version_v1_20_R2 implements VersionHandler {

    @NotNull
    private final SuperSign superSign;

    private final Map<UUID, NioSocketChannel> channels = new HashMap<>();

    private final Class<?> SERVER_COMMON_IMPL = Reflex.getClass("net.minecraft.server.network", "ServerCommonPacketListenerImpl");
    private final Class<?> NETWORK_MANAGER = Reflex.getClass("net.minecraft.network", "NetworkManager");

    private final Field CONNECTION = Reflex.getField(SERVER_COMMON_IMPL, "c", true);
    private final Field CHANNEL = Reflex.getField(NETWORK_MANAGER, "n", true);

    public Version_v1_20_R2(@NotNull SuperSign superSign) {
        this.superSign = superSign;
    }

    @Override
    public void onJoin(@NotNull Player player) {
        Object handle = ((CraftPlayer) player).getHandle().c;
        Object fieldValue = Reflex.getFieldValue(handle, CONNECTION, Object.class);
        NioSocketChannel channel = Reflex.getFieldValue(fieldValue, CHANNEL, NioSocketChannel.class);

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
        NioSocketChannel channel = channels.get(uuid);
        if(channel != null && channel.pipeline().get(SuperSign.ID) != null) {
            try {
                try {
                channel.pipeline().remove(SuperSign.ID);
            } catch (Exception ignore) {}
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
        PlayerConnection connection = (((CraftPlayer)player).getHandle()).c;

        player.sendSignChange(location, signBuilder.getLines(), signBuilder.getDyeColor());
        PacketPlayOutOpenSignEditor openSign = new PacketPlayOutOpenSignEditor(blockPosition, true);
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
            out.add(msg);
            superSign.read(player, msg.e());
        }
    }

}
