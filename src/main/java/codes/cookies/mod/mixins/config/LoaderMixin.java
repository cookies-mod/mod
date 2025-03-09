package codes.cookies.mod.mixins.config;

import java.util.LinkedHashMap;

import codes.cookies.mod.utils.json.JsonSerializable;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigObjectEntry;
import com.teamresourceful.resourcefulconfig.common.loader.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Loader.class, remap = false)
public class LoaderMixin {

	@WrapOperation(method = "loadConfig", at = @At(value = "INVOKE", target = "Ljava/util/LinkedHashMap;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
	private static Object modifyStuff(
			LinkedHashMap<?, ?> instance, Object id, Operation<Object> original, @Local JsonElement jsonElement
	) {
		final Object call = original.call(instance, id);
		if (call instanceof ResourcefulConfigObjectEntry objectEntry && (objectEntry.instance() instanceof JsonSerializable jsonSerializable)) {
			jsonSerializable.read(jsonElement);
			return null;
		}
		return call;
	}

}
