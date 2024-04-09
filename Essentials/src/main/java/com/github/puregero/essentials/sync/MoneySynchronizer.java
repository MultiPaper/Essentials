package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class MoneySynchronizer extends MultiServerSynchronizer<MoneySynchronizer.Notification> {

    private final Essentials essentials;
    private boolean handling;

    public MoneySynchronizer(Essentials essentials) {
        super(essentials, "essentials:money");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            handling = true;
            user._addMoneyRaw(notification.difference());
            handling = false;
        }
    }

    public void notify(User user, BigDecimal difference) {
        if (!handling) {
            super.notify(new Notification(user, difference));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final BigDecimal difference;

        public Notification(User user, BigDecimal difference) {
            this.uuid = user.getUUID();
            this.difference = difference;
        }

        public UUID getUuid() {
            return uuid;
        }

        public BigDecimal difference() {
            return difference;
        }
    }

}
