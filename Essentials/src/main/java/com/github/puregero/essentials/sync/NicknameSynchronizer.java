package com.github.puregero.essentials.sync;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import java.io.Serializable;
import java.util.UUID;

public class NicknameSynchronizer extends MultiServerSynchronizer<NicknameSynchronizer.Notification> {

    private final Essentials essentials;
    private boolean handling;

    public NicknameSynchronizer(Essentials essentials) {
        super(essentials, "essentials:nickname");
        this.essentials = essentials;
        this.addHandler(this::handle);
    }

    private void handle(Notification notification) {
        User user = essentials.getUser(notification.getUuid());
        if (user != null) {
            handling = true;
            user.setNickname(notification.getNickname());
            user.setDisplayNick();
            handling = false;
        }
    }

    public void notify(User user, String nickname) {
        if (!handling) {
            super.notify(new Notification(user, nickname));
        }
    }

    public static class Notification implements Serializable {
        private final UUID uuid;
        private final String nickname;

        public Notification(User user, String nickname) {
            this.uuid = user.getUUID();
            this.nickname = nickname;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getNickname() {
            return nickname;
        }
    }

}
