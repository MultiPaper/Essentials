package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import java.io.Serializable;
import java.util.UUID;

public class RemoveTpaRequestSynchronizer extends MultiServerSynchronizer<RemoveTpaRequestSynchronizer.Notification> {

    private final Essentials essentials;
    private final RecursiveLock recursiveLock = new RecursiveLock();

    public RemoveTpaRequestSynchronizer(Essentials essentials) {
        super(essentials, "essentials:remove_tpa_request");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        String username = notification.getUsername();

        try (RecursiveLock.AutoUnlock ignored = recursiveLock.lock()) {
            user.removeTpaRequest(username);
        }
    }

    public void notify(User user, String username) {
        if (!recursiveLock.isLocked()) {
            super.notify(new Notification(user, username));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final String username;

        public Notification(User user, String username) {
            this.uuid = user.getUUID();
            this.username = username;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getUsername() {
            return username;
        }
    }

}
