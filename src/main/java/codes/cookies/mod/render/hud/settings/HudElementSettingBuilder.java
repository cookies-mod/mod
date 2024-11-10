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

public class HudElementSettingBuilder {

	private static final Logger log = LoggerFactory.getLogger(HudElementSettingBuilder.class);
	private final List<HudElementSetting> settings = new ArrayList<>();

	public void addSetting(HudElementSetting setting) {
		settings.add(setting);
	}

	public void prependSetting(HudElementSetting setting) {
		settings.addFirst(setting);
	}

	public void addAfter(HudElementSetting toAdd, HudElementSetting after) {
		settings.add(settings.indexOf(after) + 1, toAdd);
	}

	public List<HudElementSetting> build() {
		return settings.stream()
				.sorted(Comparator.comparing(HudElementSetting::getSettingType, HudElementSettingType::compareTo))
				.toList();
	}

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
