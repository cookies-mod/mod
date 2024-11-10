package codes.cookies.mod.services.item.search;

import codes.cookies.mod.services.IsSameResult;

import net.minecraft.item.ItemStack;

public interface ItemSearchFilter {

	int getColor();
	IsSameResult doesMatch(ItemStack stack);

}
