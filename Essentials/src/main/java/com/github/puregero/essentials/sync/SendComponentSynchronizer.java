package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.UUID;

public class SendComponentSynchronizer extends MultiServerSynchronizer<SendComponentSynchronizer.Notification> {

    private final Essentials essentials;
    private boolean handling;

    public SendComponentSynchronizer(Essentials essentials) {
        super(essentials, "essentials:send_component");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            handling = true;
            user.sendComponent(notification.getComponent());
            handling = false;
        }
    }

    public void notify(User user, ComponentLike componentLike) {
        if (!handling) {
            Player player = Bukkit.getPlayer(user.getUUID());
            if (player != null) {
                super.notifyOwningServer(player, new Notification(user, componentLike));
            } else {
                super.notify(new Notification(user, componentLike));
            }
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final String componentJson;

        public Notification(User user, ComponentLike componentLike) {
            this.uuid = user.getUUID();
            this.componentJson = JSONComponentSerializer.json().serialize(componentLike.asComponent());
        }

        public UUID getUuid() {
            return uuid;
        }

        public Component getComponent() {
            return JSONComponentSerializer.json().deserialize(componentJson);
        }
    }

}
