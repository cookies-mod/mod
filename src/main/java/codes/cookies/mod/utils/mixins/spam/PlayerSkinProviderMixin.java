package codes.cookies.mod.utils.mixins.spam;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.TextureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.filter.StringMatchFilter;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hide spam from log.
 */
@Mixin(PlayerSkinProvider.class)
public abstract class PlayerSkinProviderMixin {

    @Shadow
    @Final
    static Logger LOGGER;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void addCustomCache(TextureManager textureManager, Path directory, MinecraftSessionService sessionService,
                                Executor executor, CallbackInfo ci) {
        final LoggerContext context = (LoggerContext)LogManager.getContext(false);
        final org.apache.logging.log4j.core.Logger logger = context.getLogger(LOGGER.getName());
        context.getConfiguration().addLoggerFilter(logger, new StringMatchFilter.Builder().setMatchString("Profile contained invalid signature for textures property (profile id: ").setOnMatch(
            Filter.Result.ACCEPT).setOnMatch(Filter.Result.DENY).build());
    }

}
