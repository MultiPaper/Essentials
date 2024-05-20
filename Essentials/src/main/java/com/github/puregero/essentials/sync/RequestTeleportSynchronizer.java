package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import java.io.Serializable;
import java.util.UUID;

public class RequestTeleportSynchronizer extends MultiServerSynchronizer<RequestTeleportSynchronizer.Notification> {

    private final Essentials essentials;
    private final RecursiveLock recursiveLock = new RecursiveLock();

    public RequestTeleportSynchronizer(Essentials essentials) {
        super(essentials, "essentials:request_teleport");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user1 = essentials.getUser(notification.getUuid1());
        User user2 = essentials.getUser(notification.getUuid2());
        boolean here = notification.isHere();

        try (RecursiveLock.AutoUnlock ignored = recursiveLock.lock()) {
            user1.requestTeleport(user2, here);
        }
    }

    public void notify(User user1, User user2, boolean here) {
        if (!recursiveLock.isLocked()) {
            super.notify(new Notification(user1, user2, here));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid1;
        private final UUID uuid2;
        private final boolean here;

        public Notification(User user1, User user2, boolean here) {
            this.uuid1 = user1.getUUID();
            this.uuid2 = user2.getUUID();
            this.here = here;
        }

        public UUID getUuid1() {
            return uuid1;
        }

        public UUID getUuid2() {
            return uuid2;
        }

        public boolean isHere() {
            return here;
        }
    }

}
