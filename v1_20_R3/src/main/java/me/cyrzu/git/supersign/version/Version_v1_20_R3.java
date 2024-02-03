package me.cyrzu.git.supersign.version;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.cyrzu.git.supersign.Reflex;
import me.cyrzu.git.supersign.SignHolder;
import me.cyrzu.git.supersign.VersionHandler;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

public class Version_v1_20_R3 implements VersionHandler {

    private final static String ID = "sign-gui";

    private final Map<UUID, SignHolder> holders = new HashMap<>();

    private final Map<UUID, NioSocketChannel> channels = new HashMap<>();

    private final Class<?> SERVER_COMMON_IMPL = Reflex.getClass("net.minecraft.server.network", "ServerCommonPacketListenerImpl");
    private final Class<?> NETWORK_MANAGER = Reflex.getClass("net.minecraft.network", "NetworkManager");

    private final Field CONNECTION = Reflex.getField(SERVER_COMMON_IMPL, "c", true);
    private final Field CHANNEL = Reflex.getField(NETWORK_MANAGER, "n", true);

    @Override
    public void onJoin(final Player player) {
        Object handle = ((CraftPlayer) player).getHandle().c;
        Object fieldValue = Reflex.getFieldValue(handle, CONNECTION, Object.class);
        NioSocketChannel channel = Reflex.getFieldValue(fieldValue, CHANNEL, NioSocketChannel.class);

        if(channel.pipeline().get(ID) != null) {
            return;
        }

        SignPacket signPacket = new SignPacket(player, this);
        channel.pipeline().addAfter("decoder", ID, signPacket);
        channels.put(player.getUniqueId(), channel);
    }

    @Override
    public void onQuit(final Player player) {
        holders.remove(player.getUniqueId());
        uninject(player.getUniqueId());
    }

    @Override
    public void uninject() {
        for (UUID uuid : channels.keySet()) {
            uninject(uuid);
        }
    }

    @Override
    public void uninject(@NotNull UUID uuid) {
        NioSocketChannel channel = channels.get(uuid);
        if(channel != null && channel.pipeline().get(ID) != null) {
            channel.pipeline().remove(ID);
            channels.remove(uuid);
        }
    }

    @Override
    public void read(Player player, String[] lines) {
        SignHolder signHolder = holders.get(player.getUniqueId());
        if(signHolder != null) {
            signHolder.applyPlayer(player, lines);
            holders.remove(player.getUniqueId());
        }
    }

    @Override
    public void sendSign(Player player, @NotNull Consumer<String[]> function) {
        Block block = player.getLocation().getBlock();
        Location location = block.getLocation();
        final int locY = location.getBlockY();
        location.setY(locY > 5 ? locY - 4 : locY + 4);

        player.sendBlockChange(location, Material.OAK_SIGN.createBlockData());
        PlayerConnection connection = (((CraftPlayer)player).getHandle()).c;
        PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), false);
        connection.b(packet);

        holders.put(player.getUniqueId(), new SignHolder(location, function));
    }

    private static class SignPacket extends MessageToMessageDecoder<PacketPlayInUpdateSign> {

        private final VersionHandler handler;

        private final Player player;

        public SignPacket(Player player, VersionHandler handler) {
            this.player = player;
            this.handler = handler;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, PacketPlayInUpdateSign msg, List<Object> out) {
            out.add(msg);
            handler.read(player, msg.e());
        }
    }

}
