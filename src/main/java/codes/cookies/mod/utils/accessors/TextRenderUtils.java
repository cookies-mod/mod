package codes.cookies.mod.utils.accessors;

/**
 * Allows for modification of a text field widget instance.
 */
public class TextRenderUtils {

	private static boolean shadowsDisabled = false;

	/**
	 * Disables all shadows for text.
	 */
	public static void disableShadows() {
		shadowsDisabled = true;
	}

	/**
	 * Uses the default show behaviour.
	 */
	public static void enableShadows() {
		shadowsDisabled = false;
	}

	/**
	 * @return Whether shadows are disabled.
	 */
	public static boolean hasShadowsDisabled() {
		return shadowsDisabled;
	}
}
