package dev.morazzer.cookies.mod.features.misc.render;

import dev.morazzer.cookies.mod.config.ConfigManager;

/**
 * Helper class to prevent duplicate code in the
 * {@linkplain cm.mixins.render.ArmorFeatureRenderMixin}
 * and {@linkplain cm.mixins.render.HeadFeatureRenderMixin}.
 */
public interface ArmorRenderHelper {

	/**
	 * Whether the armor should be rendered or not.
	 *
	 * @return Whether it should be rendered.
	 */
	static boolean shouldNotRender() {
		return ConfigManager.getConfig().miscConfig.hideOtherArmor.getValue();
	}
}
