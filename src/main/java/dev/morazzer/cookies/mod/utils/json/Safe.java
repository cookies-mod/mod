package dev.morazzer.cookies.mod.utils.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells the {@linkplain cm.utils.json.JsonUtils#toJsonObject(Object)} and
 * {@linkplain cm.utils.json.JsonUtils#fromJson(Object, com.google.gson.JsonObject)} methods to not
 * use {@linkplain com.google.gson.Gson} for the annotated field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Safe {
}
