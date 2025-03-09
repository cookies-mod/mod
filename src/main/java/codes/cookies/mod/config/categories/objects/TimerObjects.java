package codes.cookies.mod.config.categories.objects;

import codes.cookies.mod.config.CookiesOptions;
import codes.cookies.mod.features.misc.timer.NotificationManager;
import codes.cookies.mod.translations.TranslationKeys;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject;

@ConfigObject
public class TimerObjects {

	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_NOTIFICATIONS_ENABLED)
	@CookiesOptions.IncludeField
	@ConfigEntry(id = "enabled")
	public boolean enabled = false;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_NOTIFICATIONS_TYPE)
	@CookiesOptions.IncludeField
	@ConfigEntry(id = "type")
	public NotificationManager.NotificationType type = NotificationManager.NotificationType.TOAST;
	@CookiesOptions.Translatable(TranslationKeys.CONFIG_MISC_NOTIFICATION_SOUND)
	@CookiesOptions.IncludeField
	@ConfigEntry(id = "enable_sound")
	public boolean enableSound = true;

}
