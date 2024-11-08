package codes.cookies.mod.features.misc.render;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.utils.accessors.PlayerEntityRenderStateAccessor;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;

/**
 * Helper class to prevent duplicate code in the
 * {@linkplain codes.cookies.mod.mixins.render.ArmorFeatureRenderMixin}
 * and {@linkplain codes.cookies.mod.mixins.render.HeadFeatureRenderMixin}.
 */
public interface ArmorRenderHelper {

	/**
	 * Whether the armor should be rendered or not.
	 *
	 * @return Whether it should be rendered.
	 */
	static boolean shouldNotRender(LivingEntityRenderState state, ItemStack stack) {
		if (state instanceof PlayerEntityRenderState playerEntityRenderState) {
			if (PlayerEntityRenderStateAccessor.isSelf(playerEntityRenderState)) {
				return shouldHideOwnArmor() && !showDyedArmor(stack);
			} else {
				return shouldHideOtherArmor() && !showDyedArmor(stack);
			}
		}
		return false;
	}

	static boolean shouldHideOtherArmor() {
		return ConfigManager.getConfig().miscConfig.hideOtherArmor.getValue();
	}

	static boolean shouldHideOwnArmor() {
		return ConfigManager.getConfig().miscConfig.hideOwnArmor.getValue();
	}

	static boolean showDyedArmor(final ItemStack itemStack) {
		return ConfigManager.getConfig().miscConfig.showDyeArmor.getValue() &&
				itemStack.contains(CookiesDataComponentTypes.DYE);
	}

}
