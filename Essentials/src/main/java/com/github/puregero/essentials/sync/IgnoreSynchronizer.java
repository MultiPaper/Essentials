package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import java.io.Serializable;
import java.util.UUID;

public class IgnoreSynchronizer extends MultiServerSynchronizer<IgnoreSynchronizer.Notification> {

    private final Essentials essentials;
    private final RecursiveLock recursiveLock = new RecursiveLock();

    public IgnoreSynchronizer(Essentials essentials) {
        super(essentials, "essentials:ignore");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        User ignoredUser = essentials.getUser(notification.getIgnored());
        if (user != null && ignoredUser != null) {
            try (RecursiveLock.AutoUnlock ignored = recursiveLock.lock()) {
                user.setIgnoredPlayer(ignoredUser, notification.isIgnoring());
            }
        }
    }

    public void notify(User user, User ignored, boolean ignoring) {
        if (!recursiveLock.isLocked()) {
            super.notify(new Notification(user, ignored, ignoring));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final UUID ignored;
        private final boolean ignoring;

        public Notification(User user, User replyRecipient, boolean ignoring) {
            this.uuid = user.getUUID();
            this.ignored = replyRecipient.getUUID();
            this.ignoring = ignoring;
        }

        public UUID getUuid() {
            return uuid;
        }

        public UUID getIgnored() {
            return ignored;
        }

        public boolean isIgnoring() {
            return ignoring;
        }
    }

}
