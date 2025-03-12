package codes.cookies.mod.features.misc.render;

import codes.cookies.mod.config.categories.MiscCategory;
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
		return MiscCategory.hideOtherArmour;
	}

	static boolean shouldHideOwnArmor() {
		return MiscCategory.hideOwnArmour;
	}

	static boolean showDyedArmor(final ItemStack itemStack) {
		return MiscCategory.showDyedArmor && itemStack.contains(CookiesDataComponentTypes.DYE);
	}

}
