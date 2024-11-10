package codes.cookies.mod.mixins.render;

import codes.cookies.mod.features.misc.render.ArmorRenderHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows for cancellation of armor rendering.
 */
@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRenderMixin {

    @Inject(
        at = @At("HEAD"),
        method = "renderArmor",
        cancellable = true
    )
    @SuppressWarnings("MissingJavadoc")
    public <T extends LivingEntity, A extends BipedEntityModel<T>> void render(
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        T entity,
        EquipmentSlot armorSlot,
        int light,
        A model,
        CallbackInfo ci) {
        if (ArmorRenderHelper.shouldNotRender(entity, entity.getEquippedStack(armorSlot))) {
            ci.cancel();
        }
    }

}
