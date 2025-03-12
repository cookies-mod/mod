package codes.cookies.mod.config;

import com.google.common.base.Predicates;
import com.teamresourceful.resourcefulconfig.api.types.options.Option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CookiesOptions {
	public static final Option<Seperator, Seperator> SEPERATOR = Option.of(CookiesOptions.Seperator.class, Predicates.alwaysTrue());

	public static void register() {

	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CustomFieldBehaviour {}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IncludeField {}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Button {

		String value();
		String buttonText() default "";

	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Seperator {
		String value();
		boolean hasDescription() default false;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Translatable {

		String value();

	}
}
