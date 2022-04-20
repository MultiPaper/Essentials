package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.config.entities.LazyLocation;

import java.io.Serializable;
import java.util.UUID;

public class LastLocationSynchronizer extends MultiServerSynchronizer<LastLocationSynchronizer.Notification> {

    private final Essentials essentials;
    private boolean handling;

    public LastLocationSynchronizer(Essentials essentials) {
        super(essentials, "essentials:last_location");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            handling = true;
            user.setLastLocation(notification.getLocation().location());
            handling = false;
        }
    }

    public void notify(User user, LazyLocation location) {
        if (!handling) {
            super.notify(new Notification(user, location));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final LazyLocation location;

        public Notification(User user, LazyLocation location) {
            this.uuid = user.getUUID();
            this.location = location;
        }

        public UUID getUuid() {
            return uuid;
        }

        public LazyLocation getLocation() {
            return location;
        }
    }

}
