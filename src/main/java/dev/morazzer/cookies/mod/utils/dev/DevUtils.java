package dev.morazzer.cookies.mod.utils.dev;

import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.Getter;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.slf4j.LoggerFactory;

/**
 * Utils related to debugging and development.
 */
public class DevUtils {

	private static final Identifier ONLY_IN_DEV_ENV = Identifier.of("cookiesmod", "disabled_in_prod");
	/**
	 * All tools that are available.
	 */
	public static final Set<Identifier> availableTools = new HashSet<>();
	private static final Set<Identifier> enabledTools = new CopyOnWriteArraySet<>();
	@Getter
	private static final Set<Identifier> disabledTools = new CopyOnWriteArraySet<>(availableTools);

	private static final Identifier EXTRA_LOGGING = createIdentifier("extra_logging");
	@Getter
	private static final List<String> enabledExtraLogging = new LinkedList<>();

	/**
	 * Whether the mod is currently logging debug messages or not.
	 *
	 * @return Whether it is logging.
	 */
	public static boolean isExtraLoggingEnabled() {
		return isEnabled(EXTRA_LOGGING);
	}

	/**
	 * Whether a dev tool is enabled.
	 *
	 * @param identifier The tool to check.
	 * @return Whether it is enabled.
	 */
	public static boolean isEnabled(final Identifier identifier) {
		return enabledTools.contains(identifier);
	}

	public static boolean isDisabled(final Identifier identifier) {
		return !isEnabled(identifier);
	}

	/**
	 * Logs a specific value to the console.
	 *
	 * @param key          The key of the logger.
	 * @param message      The message to log.
	 * @param replacements The replacements.
	 */
	public static void log(final String key, final Object message, final Object... replacements) {
		if (!isExtraLoggingEnabled()) {
			return;
		}
		if (!enabledExtraLogging.isEmpty() && enabledExtraLogging.contains(key)) {
			CookiesUtils.sendWhiteMessage("%s".formatted(message).formatted(replacements));
			return;
		}
		LoggerFactory.getLogger(key).info("%s".formatted(message).formatted(replacements));
	}

	/**
	 * Enables a specific dev tool.
	 *
	 * @param identifier The tool to enable.
	 * @return If the tool was enabled.
	 */
	public static boolean enable(final Identifier identifier) {
		if (!availableTools.contains(identifier)) {
			return false;
		}

		enabledTools.add(identifier);
		disabledTools.remove(identifier);
		return true;
	}

	/**
	 * Gets a list of all enabled tools.
	 *
	 * @return A list of tools.
	 */
	public static Set<Identifier> getEnabledTools() {
		return Collections.unmodifiableSet(enabledTools);
	}

	/**
	 * Creates a dev tool that only works in a development environment, this may be usefull for debugs that should
	 * never be used by an end user.
	 *
	 * @param name The name of the tool.
	 * @return The identifier.
	 */
	public static Identifier createDevelopmentEnvIdentifier(final String name) {
		if (!isDevEnvironment()) {
			return ONLY_IN_DEV_ENV;
		}
		return createIdentifier(name);
	}

	/**
	 * Creates a dev tool.
	 *
	 * @param name The name of the tool.
	 * @return The identifier.
	 */
	public static Identifier createIdentifier(final String name) {
		return createIdentifier(name, false);
	}

	/**
	 * Creates a dev tool.
	 *
	 * @param name The name of the tool.
	 * @param defaultEnabled Whether the tool should be enabled by default.
	 * @return The identifier.
	 */
	public static Identifier createIdentifier(final String name, boolean defaultEnabled) {
		final Identifier identifier = Identifier.of("cookiesmod", "dev/" + name);
		availableTools.add(identifier);
		disable(identifier);
		if (defaultEnabled) {
			enable(identifier);
		}
		return identifier;
	}

	/**
	 * Disables a specific dev tool.
	 *
	 * @param identifier The tool to disable.
	 * @return If the tool was disabled.
	 */
	public static boolean disable(final Identifier identifier) {
		if (!availableTools.contains(identifier)) {
			return false;
		}

		enabledTools.remove(identifier);
		disabledTools.add(identifier);
		return true;
	}

	/**
	 * Whether the mod is running in a development environment.
	 *
	 * @return Whether it is a development environment.
	 */
	public static boolean isDevEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	/**
	 * Runs the {@link Runnable} when the {@link Identifier} is currently active.
	 *
	 * @param identifier The identifier to check.
	 * @param function   The runnable to run.
	 */
	public static void runIf(final Identifier identifier, final Runnable function) {
		if (DevUtils.isEnabled(identifier)) {
			function.run();
		}
	}

	/**
	 * Sends a message to the player if the {@link Identifier} is active.
	 *
	 * @param identifier The identifier to check.
	 * @param text       The text to send.
	 */
	public static void sendIf(final Identifier identifier, final String text) {
		if (DevUtils.isEnabled(identifier)) {
			CookiesUtils.sendMessage(StackWalker.getInstance().getCallerClass().getSimpleName() + " > " + text);
		}
	}

	/**
	 * Sends a message to the player if the {@link Identifier} is active.
	 *
	 * @param identifier The identifier to check.
	 * @param text       The text to send.
	 */
	public static void sendIf(final Identifier identifier, final Text text) {
		if (DevUtils.isEnabled(identifier)) {
			CookiesUtils.sendMessage(text);
		}
	}

}
