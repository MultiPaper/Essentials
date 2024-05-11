package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import java.io.Serializable;
import java.util.UUID;

public class VanishSynchronizer extends MultiServerSynchronizer<VanishSynchronizer.Notification> {

    private final Essentials essentials;
    private boolean handling;

    public VanishSynchronizer(Essentials essentials) {
        super(essentials, "essentials:vanish");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            handling = true;
            if (user.isVanished() != notification.getVanished()) {
                user.setVanished(notification.getVanished());
            }
            handling = false;
        }
    }

    public void notify(User user, boolean vanished) {
        if (!handling) {
            super.notify(new Notification(user, vanished));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final boolean vanished;

        public Notification(User user, boolean vanished) {
            this.uuid = user.getUUID();
            this.vanished = vanished;
        }

        public UUID getUuid() {
            return uuid;
        }

        public boolean getVanished() {
            return vanished;
        }
    }

}
