package codes.cookies.mod.config.categories.mining;

import codes.cookies.mod.config.system.Foldable;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.ColorOption;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.Constants;

import java.awt.*;

/**
 * Config for the mine shaft corpse highlight.
 */
public class ShaftConfig extends Foldable {

	public BooleanOption enable = new BooleanOption(TranslationKeys.CONFIG_MINING_SHAFT_ENABLE, true);
	public BooleanOption text = new BooleanOption(TranslationKeys.CONFIG_MINING_SHAFT_TEXT, true).onlyIf(enable);
	public BooleanOption box = new BooleanOption(TranslationKeys.CONFIG_MINING_SHAFT_BOX, true).onlyIf(enable);
	public BooleanOption beam = new BooleanOption(TranslationKeys.CONFIG_MINING_SHAFT_BEAM, true).onlyIf(enable);
	public ColorOption color = new ColorOption(
			TranslationKeys.CONFIG_MINING_SHAFT_COLOR,
			new Color(Constants.MAIN_COLOR))
			.withAlpha().onlyIf(enable);

	@Override
	public String getName() {
		return TranslationKeys.CONFIG_MINING_SHAFT;
	}
}
