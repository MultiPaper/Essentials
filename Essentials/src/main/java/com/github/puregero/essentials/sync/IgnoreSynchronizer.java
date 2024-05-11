package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import java.io.Serializable;
import java.util.UUID;

public class IgnoreSynchronizer extends MultiServerSynchronizer<IgnoreSynchronizer.Notification> {

    private final Essentials essentials;
    private boolean handling;

    public IgnoreSynchronizer(Essentials essentials) {
        super(essentials, "essentials:ignore");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        User ignored = essentials.getUser(notification.getIgnored());
        if (user != null && ignored != null) {
            handling = true;
            user.setIgnoredPlayer(ignored, notification.isIgnoring());
            handling = false;
        }
    }

    public void notify(User user, User ignored, boolean ignoring) {
        if (!handling) {
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
