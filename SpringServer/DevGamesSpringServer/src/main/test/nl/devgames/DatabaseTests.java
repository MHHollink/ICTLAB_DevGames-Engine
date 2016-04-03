package nl.devgames;

import nl.devgames.model.User;
import nl.devgames.model.UserWithPassword;
import nl.devgames.rest.controller.AuthController;
import nl.devgames.rest.controller.UserController;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Marcel on 03-4-2016.
 */
public class DatabaseTests extends DevGamesTests{
    String userSession;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
            }

    @Test
    public void createUser() throws Exception {
        UserController controller = new UserController();

        UserWithPassword user = new UserWithPassword();

        user.setUsername("Mjollnir");
        user.setPassword("password");

        controller.createNewUser(
                user
        );

        String session = new AuthController().login(user.getUsername(), user.getPassword()).get(Application.SESSION_HEADER_KEY);

        User newUser = controller.getOwnUser(session);

        assert user.equals(newUser);
    }
}
