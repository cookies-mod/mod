package codes.cookies.mod.mixins.config;

import codes.cookies.mod.utils.accessors.config.OptionItemAccessor;
import com.teamresourceful.resourcefulconfig.client.components.options.OptionItem;

import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OptionItem.class)
public class OptionItemMixin implements OptionItemAccessor {

	@Mutable
	@Shadow
	@Final
	private Text title;

	@Override
	public void cookies$modifyTitle(Text title) {
		this.title = title;
	}
}
