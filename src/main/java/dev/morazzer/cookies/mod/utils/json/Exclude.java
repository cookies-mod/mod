package dev.morazzer.cookies.mod.utils.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells the {@linkplain dev.morazzer.cookies.mod.utils.json.JsonUtils#toJsonObject(Object)} and
 * {@linkplain dev.morazzer.cookies.mod.utils.json.JsonUtils#fromJson(Object, com.google.gson.JsonObject)} methods to not
 * serialize/deserialize this field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exclude {}
