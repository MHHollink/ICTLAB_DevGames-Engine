package nl.devgames.rules;

import nl.devgames.connection.database.dao.PushDao;
import nl.devgames.connection.gcm.GCMMessageComposer;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorikito on 02-Jun-16.
 */
public class AchievementManager {
    private User user;
    PushDao pushDao;

    public AchievementManager(User user) {
        this.user = user;
        pushDao = new PushDao();
    }

    private void sendAchievementMessage(String achievementId) {
        L.i("Sending GCM message to %s", user.getUsername());
        GCMMessageComposer.sendMessage(
                GCMMessageType.ACHIEVEMENT,
                "",
                achievementId,
                user.getId()
        );
    }

    public void checkAchievementsOfUser() throws ConnectException {
        checkTwoPushesIn10MinutesAchievements();
    }

    private void checkTwoPushesIn10MinutesAchievements() throws ConnectException {
        //cehck if already achieved


        Long currentTime = System.currentTimeMillis();
        List<Push> pushesInLast10Minutes = new ArrayList<>();

        List<Push> userPushes = pushDao.queryByUser(user.getId());
        for(Push push : userPushes) {
            if(push.getTimestamp() > currentTime - (1000 * 60 * 10)) {
                pushesInLast10Minutes.add(push);
            }
        }
        if(pushesInLast10Minutes.size() > 2 ) {
            //achievement unlocked
            sendAchievementMessage("CgkI6sj_ys4REAIQAA");
        }

    }
}
