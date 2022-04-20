package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.config.entities.LazyLocation;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.UUID;

public class HomeLocationSynchronizer extends MultiServerSynchronizer<HomeLocationSynchronizer.Notification> {

    private final Essentials essentials;
    private boolean handling;

    public HomeLocationSynchronizer(Essentials essentials) {
        super(essentials, "essentials:home_location");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            handling = true;
            if (notification.getLocation() == null) {
                try {
                    user.delHome(notification.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                user.setHome(notification.getName(), notification.getLocation().location());
            }
            handling = false;
        }
    }

    public void notify(User user, String name, @Nullable LazyLocation location) {
        if (!handling) {
            super.notify(new Notification(user, name, location));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final String name;
        private final @Nullable LazyLocation location;

        public Notification(User user, String name, @Nullable LazyLocation location) {
            this.uuid = user.getUUID();
            this.name = name;
            this.location = location;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public @Nullable LazyLocation getLocation() {
            return location;
        }
    }

}
