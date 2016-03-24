package nl.devgames.utils;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("PointlessBooleanExpression")
public class L {

    // The format of the automatically created tag in each log line.
    private static final String TAG_FORMAT = "DEVGAMES: line=%d: %s#%s: ";

    private static final Pattern ANONYMOUS_CLASS_PATTERN = Pattern.compile("\\$\\d+$");

    // Empty Instantiation of a args array in case of null
    private static final String[] NO_ARGS = {};

    public static void og(String message, Object... args) {
        String formattedMessage = String.format(message, args);
        System.out.println(
                        createTag() +
                        formattedMessage
        );
    }

    public static void og(Throwable throwable, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        System.out.println(
                createTag()+
                        form.format(args == null ? NO_ARGS : args)
        );
        throwable.printStackTrace();
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
            Matcher m = ANONYMOUS_CLASS_PATTERN.matcher(tag);

            if (m != null && m.find()) {
                tag = m.replaceAll("");
            }

            String className = tag.substring(tag.lastIndexOf('.') + 1);

            return String.format(TAG_FORMAT,
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
}