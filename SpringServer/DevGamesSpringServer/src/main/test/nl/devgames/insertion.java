package nl.devgames;

import nl.devgames.rest.controller.UserController;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Marcel on 23-5-2016.
 */
public class insertion {

    @Test
    public void testSomething() throws Exception {
        new UserController().setUpDb();
        assertTrue(true);
    }
}
