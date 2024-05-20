package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import java.io.Serializable;
import java.util.UUID;

public class LoginTimeSynchronizer extends MultiServerSynchronizer<LoginTimeSynchronizer.Notification> {

    private final Essentials essentials;
    private final RecursiveLock recursiveLock = new RecursiveLock();

    public LoginTimeSynchronizer(Essentials essentials) {
        super(essentials, "essentials:login_time");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            try (RecursiveLock.AutoUnlock ignored = recursiveLock.lock()) {
                user.setLastLogin(notification.getTime());
            }
        }
    }

    public void notify(User user, long time) {
        if (!recursiveLock.isLocked()) {
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
