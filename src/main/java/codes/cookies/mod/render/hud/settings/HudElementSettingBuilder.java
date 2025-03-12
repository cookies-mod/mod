package codes.cookies.mod.render.hud.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
}
