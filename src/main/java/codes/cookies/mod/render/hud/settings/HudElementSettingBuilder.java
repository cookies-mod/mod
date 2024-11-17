package codes.cookies.mod.render.hud.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.ColorOption;
import codes.cookies.mod.config.system.options.EnumCycleOption;
import codes.cookies.mod.config.system.options.TextDisplayOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for the hud element settings.
 */
public class HudElementSettingBuilder {

	private static final Logger log = LoggerFactory.getLogger(HudElementSettingBuilder.class);
	private final List<HudElementSetting> settings = new ArrayList<>();

	/**
	 * Adds a setting to the list.
	 * @param setting The setting to add.
	 */
	public void addSetting(HudElementSetting setting) {
		settings.add(setting);
	}

	/**
	 * Prepend setting to the list.
	 * @param setting The setting to prepend.
	 */
	public void prependSetting(HudElementSetting setting) {
		settings.addFirst(setting);
	}

	/**
	 * Builds the settings list.
	 */
	public List<HudElementSetting> build() {
		return settings.stream()
				.sorted(Comparator.comparing(HudElementSetting::getSettingType, HudElementSettingType::compareTo))
				.toList();
	}

	/**
	 * Creates a setting based on the provided option if applicable.
	 * @param option The option to use.
	 */
	public void addOption(Option<?, ?> option) {
		switch (option) {
			case BooleanOption booleanOption -> addSetting(new BooleanSetting(booleanOption));
			case EnumCycleOption<?> enumCycleOption -> addSetting(new EnumCycleSetting<>(enumCycleOption));
			case ColorOption colorOption -> addSetting(new ColorSetting(colorOption));
			case TextDisplayOption textDisplayOption ->
					addSetting(new LiteralSetting(textDisplayOption.getName(), HudElementSettingType.CUSTOM));
			default -> log.warn("Can't transform option {} into a hud settings!", option.getClass().getSimpleName());
		}
	}
}
