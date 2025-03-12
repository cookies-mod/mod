package codes.cookies.mod.mixins.config;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.AnnotationGetter;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntryData.class, remap = false)
public class EntryDataMixin {

	@Inject(method = "of(Lcom/teamresourceful/resourcefulconfig/api/types/options/AnnotationGetter;Ljava/lang/Class;)Lcom/teamresourceful/resourcefulconfig/api/types/options/EntryData;", at = @At(value = "INVOKE", target = "Lcom/teamresourceful/resourcefulconfig/api/types/options/EntryData$Builder;build()Lcom/teamresourceful/resourcefulconfig/api/types/options/EntryData;"))
	private static void of(CallbackInfoReturnable<EntryData> cir, @Local(argsOnly = true) AnnotationGetter getter, @Local EntryData.Builder builder, @Local ConfigEntry entry) {
		final CookiesOptions.Translatable cookiesTranslatable = getter.get(CookiesOptions.Translatable.class);
		if (cookiesTranslatable != null) {
			builder.translation("", TranslationKeys.name(cookiesTranslatable.value()));
			builder.comment("", TranslationKeys.tooltip(cookiesTranslatable.value()));
		}
	}
	
}
