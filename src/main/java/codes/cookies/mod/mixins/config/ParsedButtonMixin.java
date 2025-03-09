package codes.cookies.mod.mixins.config;

import java.lang.reflect.Field;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamresourceful.resourcefulconfig.api.types.options.Position;
import com.teamresourceful.resourcefulconfig.common.loader.buttons.ParsedButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ParsedButton.class, remap = false)
public class ParsedButtonMixin {

	@WrapOperation(method = "of", at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/teamresourceful/resourcefulconfig/api/types/options/Position;Ljava/lang/Runnable;Ljava/lang/String;)Lcom/teamresourceful/resourcefulconfig/common/loader/buttons/ParsedButton;"))
	private static ParsedButton of(
			String title,
			String description,
			String target,
			Position position,
			Runnable runnable,
			String text,
			Operation<ParsedButton> original,
			@Local(argsOnly = true) Field field
	) {
		if (!field.isAnnotationPresent(CookiesOptions.Button.class)) {
			return original.call(title, description, target, position, runnable, text);
		}

		CookiesOptions.Button annotation = field.getAnnotation(CookiesOptions.Button.class);
		return original.call(
				TranslationKeys.name(annotation.value()),
				TranslationKeys.tooltip(annotation.value()),
				target,
				position,
				runnable,
				annotation.buttonText());
	}

}
