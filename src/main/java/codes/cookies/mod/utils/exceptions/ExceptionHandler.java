package codes.cookies.mod.utils.exceptions;

import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of methods to handle exceptions in different ways.
 */
public class ExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("cookies-exception-handler");

    /**
     * Handles the exception that might occur while running a function.
     *
     * @param throwableFunction The function.
     * @return If the function failed.
     */
    public static boolean tryCatch(ThrowableFunction<?> throwableFunction) {
        try {
            throwableFunction.run();
        } catch (Throwable exception) {
            handleException(exception);
            return false;
        }

        return true;
    }

    /**
     * Handles an exception and print it to std.err and to the chat.
     *
     * @param exception The exception to handle.
     */
    public static void handleException(Throwable exception) {
        LOGGER.error("An exception occurred", exception);
        CookiesUtils.getPlayer().ifPresent(player -> {
            String stackTrace = getStacktrace(exception);
            String copy = """
                ```
                Version: %s
                VM: %s
                Mod: %s
                Exception type: %s
                
                --------------------------
                
                Stacktrace:
                %s
                ```""".formatted(
                MinecraftClient.getInstance().getGameVersion(),
                ManagementFactory.getRuntimeMXBean().getVmVendor() + " " + ManagementFactory
                    .getRuntimeMXBean()
                    .getVmName() + " " + ManagementFactory.getRuntimeMXBean().getVmVersion(),
                "Cookies mod",
                exception.getClass().getSimpleName(),
                stackTrace
            );
            player.sendMessage(CookiesUtils.createPrefix(Constants.FAIL_COLOR)
                .append(Text
                    .translatable(TranslationKeys.INTERNAL_ERROR)
                    .styled(style -> style
                        .withColor(Constants.FAIL_COLOR)
                        .withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Text.literal("%s: %s".formatted(
                                exception.getClass().getName(),
                                exception.getMessage()
                            ))
                        ))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy)))), false);
        });
    }

    /**
     * Gets the stacktrace as string.
     *
     * @param throwable The throwable.
     * @return The stack trace.
     */
    public static String getStacktrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    /**
     * Handles a runnable that might or might not throw an Exception.
     *
     * @param runnable The runnable.
     * @return If the runnable failed.
     */
    public static boolean tryCatch(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception exception) {
            handleException(exception);
            return false;
        }

        return true;
    }

    /**
     * Handles the exception that might occur while running a function.
     *
     * @param throwableFunction The function.
     * @param <T>               The return type of the function.
     * @return The return value of the function.
     */
    public static <T> T removeThrows(ThrowableFunction<T> throwableFunction) {
        return removeThrows(throwableFunction, null);
    }

    /**
     * Handles the exception that might occur while running a function.
     *
     * @param throwableFunction The function.
     * @param defaultObject     The object to return if there was an exception.
     * @param <T>               The return type of the function.
     * @return The return value of the function.
     */
    public static <T> T removeThrows(ThrowableFunction<T> throwableFunction, T defaultObject) {
        try {
            return throwableFunction.run();
        } catch (Throwable e) {
            handleException(e);
            return defaultObject;
        }
    }

    /**
     * Removes the exception and return the default object if it fails.
     *
     * @param throwableFunction The function.
     * @param defaultObject     The default object.
     * @param <T>               The return type of the function.
     * @return The return value of the function.
     */
    public static <T> T removeThrowsSilent(ThrowableFunction<T> throwableFunction, T defaultObject) {
        try {
            return throwableFunction.run();
        } catch (Throwable e) {
            return defaultObject;
        }
    }

    /**
     * Creates a proxy instance for a function call to handle exceptions and disable if it fails.
     *
     * @param function The function to wrap.
     * @param <T>      The type of the function.
     * @return The function wrapped with error handling.
     */
    public static <T> T wrap(T function) {
        AtomicBoolean failed = new AtomicBoolean();

        //create a proxy class and call the initial function inside the method block.
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(
            function.getClass().getClassLoader(),
            function.getClass().getInterfaces(),
            (proxy, method, args) -> {
                if (failed.get()) {
                    return null;
                }
                try {
                    return method.invoke(function, args);
                } catch (Exception e) {
                    CookiesUtils.sendMessage(CookiesUtils
                        .createPrefix(Constants.FAIL_COLOR)
                        .append("One of the features you used crashed, it will be disabled for now!"));
                    handleException(e);
                    failed.set(true);
                    return null;
                }
            }
        );
    }

    /**
     * Function that might throw an exception.
     *
     * @param <T> The return type.
     */
    @SuppressWarnings("MissingJavadoc")
    @FunctionalInterface
    public interface ThrowableFunction<T> {

        T run() throws Throwable;

    }


}
