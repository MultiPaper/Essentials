package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.github.puregero.multilib.MultiLib;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MultiServerSynchronizer<T> {

    private final Plugin plugin;
    private final String channel;

    private static byte[] serialize(Object object) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)) {
            out.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static Object deserialize(byte[] bytes) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init(Essentials plugin) {
        // Initialise the synchronisers here to ensure they're loaded
        User.requestTeleportSynchronizer = new RequestTeleportSynchronizer(plugin);
        User.removeTpaRequestSynchronizer = new RemoveTpaRequestSynchronizer(plugin);
        User.lastLocationSynchronizer = new LastLocationSynchronizer(plugin);
        User.logoutLocationSynchronizer = new LogoutLocationSynchronizer(plugin);
        User.logoutTimeSynchronizer = new LogoutTimeSynchronizer(plugin);
        User.loginTimeSynchronizer = new LoginTimeSynchronizer(plugin);
        User.homeLocationSynchronizer = new HomeLocationSynchronizer(plugin);
        User.replyRecipientSynchronizer = new ReplyRecipientSynchronizer(plugin);
        User.sendComponentSynchronizer = new SendComponentSynchronizer(plugin);
        User.moneySynchronizer = new MoneySynchronizer(plugin);
        User.nicknameSynchronizer = new NicknameSynchronizer(plugin);
        User.vanishSynchronizer = new VanishSynchronizer(plugin);
        User.ignoreSynchronizer = new IgnoreSynchronizer(plugin);
    }

    public MultiServerSynchronizer(Plugin plugin, String channel) {
        this.plugin = plugin;
        this.channel = channel;
    }

    public void addHandler(Consumer<T> callback) {
        MultiLib.on(plugin, channel, data -> {
            callback.accept((T) deserialize(data));
        });
    }

    public void addHandler(BiConsumer<T, BiConsumer<String, Object>> callback) {
        MultiLib.on(plugin, channel, (data, reply) -> {
            callback.accept((T) deserialize(data), (replyChannel, object) -> reply.accept(replyChannel, serialize(object)));
        });
    }

    public void notify(T object) {
        MultiLib.notify(channel, serialize(object));
    }

    public void notify(Chunk chunk, T object) {
        MultiLib.notify(chunk, channel, serialize(object));
    }

    public void notifyOwningServer(Player player, T object) {
        MultiLib.notifyOwningServer(player, channel, serialize(object));
    }

}

