package codes.cookies.mod.features.misc.render;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * Helper class to prevent duplicate code in the
 * {@linkplain cm.mixins.render.ArmorFeatureRenderMixin}
 * and {@linkplain cm.mixins.render.HeadFeatureRenderMixin}.
 */
public interface ArmorRenderHelper {

    /**
     * Whether the armor should be rendered or not.
     *
     * @param livingEntity The entity to check for.
     * @return Whether it should be rendered.
     */
    static boolean shouldNotRender(final LivingEntity livingEntity, final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return (hideOtherArmor(livingEntity) || hideOwnArmor(livingEntity)) && !showDyedArmor(itemStack);
    }

    static boolean hideOtherArmor(final LivingEntity livingEntity) {
        return (livingEntity instanceof OtherClientPlayerEntity &&
                ConfigManager.getConfig().miscConfig.hideOtherArmor.getValue());
    }

    static boolean hideOwnArmor(final LivingEntity livingEntity) {
        return (livingEntity instanceof ClientPlayerEntity &&
                ConfigManager.getConfig().miscConfig.hideOwnArmor.getValue());
    }

    static boolean showDyedArmor(final ItemStack itemStack) {
        return ConfigManager.getConfig().miscConfig.showDyeArmor.getValue() &&
               ItemUtils.getData(itemStack, CookiesDataComponentTypes.DYE) != null;
    }
}
