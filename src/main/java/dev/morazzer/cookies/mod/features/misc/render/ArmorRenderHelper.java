package dev.morazzer.cookies.mod.features.misc.render;

import dev.morazzer.cookies.mod.config.ConfigManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

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
    static boolean shouldNotRender(final LivingEntity livingEntity) {
        return (livingEntity instanceof ClientPlayerEntity
                && ConfigManager.getConfig().miscConfig.hideOwnArmor.getValue())
               || (livingEntity instanceof OtherClientPlayerEntity
                   && ConfigManager.getConfig().miscConfig.hideOtherArmor.getValue());
    }

}
