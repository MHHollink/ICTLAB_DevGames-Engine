package nl.devgames.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nl.devgames.utils.L.LogLevel.DEBUG;
import static nl.devgames.utils.L.LogLevel.ERROR;
import static nl.devgames.utils.L.LogLevel.INFO;
import static nl.devgames.utils.L.LogLevel.TRACE;
import static nl.devgames.utils.L.LogLevel.WARN;

public class L {

    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL,
        OFF
    }
    public static LogLevel level = DEBUG;

    // The format of the automatically created tag in each log line.
    private static final String TAG_FORMAT = "%s/ DEVGAMES: line=%d: %s#%s: ";

    private static final Pattern ANONYMOUS_CLASS_PATTERN = Pattern.compile("\\$\\d+$");

    // Empty Instantiation of a args array in case of null
    private static final String[] NO_ARGS = {};

    public static void t(String message, Object... args) {
        if(level.ordinal() <= TRACE.ordinal())
            printLog(message, args);
    }

    public static void t(Throwable throwable, String message, Object... args) {
        if(level.ordinal() <= TRACE.ordinal())
            printLog(throwable, message, args);
    }

    public static void d(String message, Object... args) {
        if(level.ordinal() <= DEBUG.ordinal())
            printLog(message, args);
    }

    public static void d(Throwable throwable, String message, Object... args) {
        if(level.ordinal() <= DEBUG.ordinal())
            printLog(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        if(level.ordinal() <= INFO.ordinal())
            printLog(message, args);
    }

    public static void i(Throwable throwable, String message, Object... args) {
        if(level.ordinal() <= INFO.ordinal())
            printLog(throwable, message, args);
    }

    public static void w(String message, Object... args) {
        if(level.ordinal() <= WARN.ordinal()) {
            System.out.print("\u003B[31m");
            printLog(message, args);
        }
    }

    public static void w(Throwable throwable, String message, Object... args) {
        if(level.ordinal() <= WARN.ordinal()) {
            System.out.print("\u003B[31m");
            printLog(throwable, message, args);
        }
    }

    public static void e(String message, Object... args) {
        if(level.ordinal() <= ERROR.ordinal()) {
            System.out.print("\u001B[31m");
            printLog(message, args);
        }
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if(level.ordinal() <= ERROR.ordinal()) {
            System.out.print("\u001B[31m");
            printLog(throwable, message, args);
        }
    }

    /**
     * Creates a tag from the trace of the class from which
     * the Log-call was called.
     * <p/>
     * Tag name retrieval "borrowed" from:
     * https://github.com/JakeWharton/timber/blob/master/timber/src/main/java/timber/log/Timber.java
     *
     * @return a tag from the trace of the class from which
     * the Log-call was called.
     */
    private static String createTag() {

        try {
            StackTraceElement[] traces = Thread.currentThread().getStackTrace();
            StackTraceElement trace = traces[4];
            String tag = trace.getClassName();
            String lvl = traces[3].getMethodName().toUpperCase();
            Matcher m = ANONYMOUS_CLASS_PATTERN.matcher(tag);

            if (m.find()) {
                tag = m.replaceAll("");
            }

            String className = tag.substring(tag.lastIndexOf('.') + 1);

            return String.format(TAG_FORMAT,
                    lvl,
                    trace.getLineNumber(),
                    className,
                    trace.getMethodName()
            );
        }
        catch (Exception e) {
            // Should not happen.
            return "UNKNOWN-TAG";
        }
    }

    private static void printLog(Throwable throwable, String message, Object... args) {
        System.out.println(
                createTag()+
                        String.format(message, args == null ? NO_ARGS : args)
        );
        throwable.printStackTrace();
    }

    private static void printLog(String message, Object... args) {
        System.out.println(
                createTag()+
                        String.format(message, args == null ? NO_ARGS : args)+
                        "\u001B[0m" // RESET COLOR IF CHANGED
        );
    }
}