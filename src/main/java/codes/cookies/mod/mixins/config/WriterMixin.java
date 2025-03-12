package codes.cookies.mod.mixins.config;

import codes.cookies.mod.utils.json.JsonSerializable;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigObjectEntry;
import com.teamresourceful.resourcefulconfig.common.jsonc.JsoncElement;
import com.teamresourceful.resourcefulconfig.common.loader.Writer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Writer.class, remap = false)
public class WriterMixin {

	@Inject(method = "toElement", at = @At("HEAD"), cancellable = true)
	private static void toElement(
			ResourcefulConfigEntry entry,
			 CallbackInfoReturnable<JsoncElement> cir
	) {
		if (entry instanceof ResourcefulConfigObjectEntry objectEntry && objectEntry.instance() instanceof JsonSerializable jsonSerializable) {
			cir.setReturnValue(jsonSerializable.writeAsJsonc());
		}
	}

}
