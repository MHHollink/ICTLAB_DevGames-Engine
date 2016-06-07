package nl.devgames.rules;

import nl.devgames.connection.gcm.GCMMessageComposer;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.model.User;
import nl.devgames.utils.L;

/**
 * Created by Jorikito on 02-Jun-16.
 */
public class AchievementManager {
    private User user;

    public AchievementManager(User user) {
        this.user = user;
    }

    private void sendAchievementMessage(Integer achievementId) {
        L.i("Sending GCM message to %s", user.getUsername());
        GCMMessageComposer.sendMessage(
                GCMMessageType.ACHIEVEMENT_UNLOCKED,
                "",
                achievementId.toString(),
                user.getId()
        );
    }

    public void checkAchievementsOfUser() {

    }
}
