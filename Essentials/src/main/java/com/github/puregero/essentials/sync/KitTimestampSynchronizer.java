package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import java.io.Serializable;
import java.util.UUID;

public class KitTimestampSynchronizer extends MultiServerSynchronizer<KitTimestampSynchronizer.Notification> {

    private final Essentials essentials;
    private final RecursiveLock recursiveLock = new RecursiveLock();

    public KitTimestampSynchronizer(Essentials essentials) {
        super(essentials, "essentials:kit_timestamp");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            try (RecursiveLock.AutoUnlock ignored = recursiveLock.lock()) {
                user.setKitTimestamp(notification.getName(), notification.getTime());
            }
        }
    }

    public void notify(User user, String name, long time) {
        if (!recursiveLock.isLocked()) {
            super.notify(new Notification(user, name, time));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final String name;
        private final long time;

        public Notification(User user, String name, long time) {
            this.uuid = user.getUUID();
            this.name = name;
            this.time = time;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public long getTime() {
            return time;
        }
    }

}
