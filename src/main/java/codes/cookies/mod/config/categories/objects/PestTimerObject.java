package codes.cookies.mod.config.categories.objects;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject;

@ConfigObject
@CookiesOptions.CustomFieldBehaviour
public class PestTimerObject extends TimerObjects {

	public enum TimerTreatment {
		FIRST, CURRENT, LATEST
	}

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_NOTIFICATIONS_PEST_ORDER)
	@ConfigEntry(id = "timer_type")
	public TimerTreatment timerType = TimerTreatment.FIRST;

}

