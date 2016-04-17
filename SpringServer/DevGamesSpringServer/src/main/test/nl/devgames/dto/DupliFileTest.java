package nl.devgames.dto;

import nl.devgames.connection.database.dto.DuplicationFileDTO;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;

/**
 * Created by Marcel on 14-4-2016.
 */
public class DupliFileTest {


    @Test
    public void testName() throws Exception {
        DuplicationFileDTO d = new DuplicationFileDTO();

        assertFalse(d.isValid());

        d.size = 5;
        d.endLine = 9;

        assertFalse(d.isValid());

        d.file = "filename";
        d.beginLine = 4;

        assertTrue(d.isValid());
    }
}
