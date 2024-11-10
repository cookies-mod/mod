package codes.cookies.mod.utils.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells the {@linkplain JsonUtils#toJsonObject(Object)} and
 * {@linkplain JsonUtils#fromJson(Object, com.google.gson.JsonObject)} methods to not
 * serialize/deserialize this field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exclude {}
