package codes.cookies.mod.utils.accessors;

/**
 * Allows for modification of a text field widget instance.
 */
public class TextRenderUtils {

	private static boolean shadowsDisabled = false;

	public static void disableShadows() {
		shadowsDisabled = true;
	}

	public static void enableShadows() {
		shadowsDisabled = false;
	}

	public static boolean hasShadowsDisabled() {
		return shadowsDisabled;
	}
}
