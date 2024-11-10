package codes.cookies.mod.render.mixins;

import codes.cookies.mod.render.WorldRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Allows for outline rendering without any active entity.
 */
@Mixin(WorldRenderer.class)
public abstract class AllowOutlinesWithoutEntities {
    @Shadow
    private @Nullable PostEffectProcessor entityOutlinePostProcessor;
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V", shift = At.Shift.AFTER), method = "render", locals = LocalCapture.CAPTURE_FAILHARD)
    @SuppressWarnings("MissingJavadoc")
    public void render(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
                       GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
                       Matrix4f matrix4f2, CallbackInfo ci, TickManager tickManager, float f, Profiler profiler,
                       Vec3d vec3d, double d, double e, double g, boolean bl, Frustum frustum, float h, boolean bl2,
                       Matrix4fStack matrix4fStack, boolean bl3) {
        if (!bl3 && WorldRender.isHasOutlines()) {
            this.entityOutlinePostProcessor.render(tickCounter.getLastFrameDuration());
            this.client.getFramebuffer().beginWrite(false);
        }
    }
}
