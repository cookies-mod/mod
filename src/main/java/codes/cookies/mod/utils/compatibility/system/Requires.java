package codes.cookies.mod.utils.compatibility.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate that the compatibility instance should be loaded if the condition is met.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Requires {

	/**
	 * @return The mod id that has to be present for the instance to be loaded.
	 */
	String value();

}
