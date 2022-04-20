package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.config.entities.LazyLocation;

import java.io.Serializable;
import java.util.UUID;

public class LogoutTimeSynchronizer extends MultiServerSynchronizer<LogoutTimeSynchronizer.Notification> {

    private final Essentials essentials;
    private boolean handling;

    public LogoutTimeSynchronizer(Essentials essentials) {
        super(essentials, "essentials:logout_time");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            handling = true;
            user.setLastLogout(notification.getTime());
            handling = false;
        }
    }

    public void notify(User user, long time) {
        if (!handling) {
            super.notify(new Notification(user, time));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final long time;

        public Notification(User user, long time) {
            this.uuid = user.getUUID();
            this.time = time;
        }

        public UUID getUuid() {
            return uuid;
        }

        public long getTime() {
            return time;
        }
    }

}
