package baecon.devgames.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import baecon.devgames.model.Commit;
import baecon.devgames.model.Project;
import baecon.devgames.model.Setting;
import baecon.devgames.model.User;
import baecon.devgames.model.update.UserUpdate;

/**
 * A utility class to create ORMLite config files.
 *
 * For reference, the following types can be used in the model classes:
 *
 * ORMLite             | Java
 * --------------------+---------------
 * STRING              | String
 * LONG_STRING         | String
 * BOOLEAN             | boolean
 * DATE                | java.util.Date
 * CHAR                | char
 * BYTE                | byte
 * BYTE_ARRAY          | byte[]
 * SHORT               | short
 * INTEGER             | int
 * LONG                | long
 * FLOAT               | float
 * DOUBLE              | double
 * SERIALIZABLE        | Serializable
 * BIG_DECIMAL_NUMERIC | BigDecimal
 *
 * http://ormlite.com/data_types.shtml
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Logger logger = Logger.getLogger(DatabaseConfigUtil.class.getName());

    /**
     * The name of the generated ORMLite config file.
     */
    public static final String CONFIG_FILE_NAME = "ormlite_config.txt";

    /**
     * An array of Class-es which will be stored in the local DB.
     * <p/>
     * When internally changing one of these model classes, the main method from this DatabaseConfigUtil class will need
     * to be re-run!
     */
    public static final Class<?>[] CLASSES = new Class[]{

            User.class,
            UserUpdate.class,

            Project.class,


            Commit.class,


            Setting.class
    };

    public static void main(String[] args) throws IOException, SQLException {

        File rawFolder = new File("app/src/main/res/raw");

        logger.info("Absolute path for rawFolder: " +  rawFolder.getAbsolutePath());

        // Check is `res/raw` exists ...
        if (!rawFolder.exists()) {

            // ... if not create it.
            boolean rawCreated = rawFolder.mkdirs();

            if (!rawCreated) {
                logger.warning("could not create a 'raw' folder inside 'res/'" +
                        " from DatabaseConfigUtil: no DB-config file created!");
                System.exit(1);
            }
            else {
                logger.info("created folder `res/raw/`");
            }
        }

        File dbConfigFile = new File(rawFolder, CONFIG_FILE_NAME);

        writeConfigFile(dbConfigFile, CLASSES);
    }
}