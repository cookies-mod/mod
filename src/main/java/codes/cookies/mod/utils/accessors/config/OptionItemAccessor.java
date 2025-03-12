package codes.cookies.mod.utils.accessors.config;

import com.teamresourceful.resourcefulconfig.client.components.options.OptionItem;

import net.minecraft.text.Text;

public interface OptionItemAccessor {

	static OptionItemAccessor cast(OptionItem optionItem) {
		return (OptionItemAccessor) optionItem;
	}

	void cookies$modifyTitle(Text title);

}
