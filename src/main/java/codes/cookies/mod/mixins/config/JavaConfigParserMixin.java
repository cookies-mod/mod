package codes.cookies.mod.mixins.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.CookiesOptions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamresourceful.resourcefulconfig.common.loader.JavaConfigParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = JavaConfigParser.class, remap = false)
public class JavaConfigParserMixin {

	@WrapOperation(method = "populateEntries(Ljava/lang/Object;Lcom/teamresourceful/resourcefulconfig/common/loader/entries/ParsedObjectEntry;)V", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getDeclaredFields()[Ljava/lang/reflect/Field;"))
	private static Field[] populateEntries(Class<?> instance, Operation<Field[]> original) {
		if (instance.isAnnotationPresent(CookiesOptions.CustomFieldBehaviour.class)) {
			List<Field> fields = new ArrayList<>();
			for (Field field : instance.getFields()) {
				if (field.getDeclaringClass() == instance) {
					fields.add(field);
					continue;
				}
				if (field.isAnnotationPresent(CookiesOptions.IncludeField.class)) {
					fields.add(field);
				}
			}
			return fields.toArray(new Field[0]);
		}
		return original.call(instance);
	}

	@WrapOperation(method = "populateEntries(Ljava/lang/Object;Lcom/teamresourceful/resourcefulconfig/common/loader/entries/ParsedObjectEntry;)V", at = @At(value = "INVOKE", target = "Ljava/util/LinkedHashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private static Object warnOnDuplicateId(
			LinkedHashMap<?, ?> map,
			Object id,
			Object valueEntry,
			Operation<Object> original,
			@Local(argsOnly = true) Object instance
	) {
		final Object returnValue = original.call(map, id, valueEntry);

		if (!instance.getClass().getPackageName().startsWith(CookiesMod.class.getPackageName())) {
			return returnValue;
		}

		if (returnValue != null) {
			if (instance.getClass().isAnnotationPresent(CookiesOptions.CustomFieldBehaviour.class)) {
				throw new UnsupportedOperationException("Duplicate id " + id + " in " + instance.getClass().getName() + " or a parent class!");
			}

			throw new UnsupportedOperationException("Duplicate id " + id + " in " + instance.getClass().getName() + "!");
		}

		return null;
	}
}
