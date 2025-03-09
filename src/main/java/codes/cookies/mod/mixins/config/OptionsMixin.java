package codes.cookies.mod.mixins.config;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.accessors.config.OptionItemAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import com.teamresourceful.resourcefulconfig.client.components.options.Options;
import com.teamresourceful.resourcefulconfig.client.components.options.OptionsListWidget;
import com.teamresourceful.resourcefulconfig.client.components.options.SeparatorItem;

import net.minecraft.util.Formatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.text.Text;

@Mixin(value = Options.class, remap = false)
public class OptionsMixin {

	@Inject(method = "populateOptions", at = @At(value = "INVOKE", target = "Lcom/teamresourceful/resourcefulconfig/api/types/options/EntryData;hasOption(Lcom/teamresourceful/resourcefulconfig/api/types/options/Option;)Z", ordinal = 1))
	private static void populate(
			CallbackInfo ci,
			@Local EntryData entryData,
			@Local(argsOnly = true) OptionsListWidget widget
	) {
		if (entryData.hasOption(CookiesOptions.SEPERATOR)) {
			final CookiesOptions.Seperator option = entryData.getOption(CookiesOptions.SEPERATOR);

			final Text title;
			final Text description;
			if (option.hasDescription()) {
				title = Text.translatable(TranslationKeys.name(option.value())).formatted(Formatting.BOLD);
				description = Text.translatable(TranslationKeys.tooltip(option.value()));
			} else {
				title = Text.translatable(option.value()).formatted(Formatting.BOLD).formatted(Formatting.GOLD);
				description = Text.empty();
			}

			final SeparatorItem separatorItem = new SeparatorItem(title, description);
			if (!option.hasDescription()) {
				OptionItemAccessor.cast(separatorItem).cookies$modifyTitle(title);
			}

			widget.add(separatorItem);
		}
	}

}
