package codes.cookies.mod.config.system;

import codes.cookies.mod.render.hud.elements.HudElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to attach an option to a hud element.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(HudSetting.HudSettings.class)
public @interface HudSetting {

	Class<? extends HudElement> value();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface HudSettings {

		HudSetting[] value();

	}
}
