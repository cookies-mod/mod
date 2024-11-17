package codes.cookies.mod.services.item.search;

import codes.cookies.mod.services.IsSameResult;

import net.minecraft.item.ItemStack;

/**
 * Filter that can be used by item search.
 */
public interface ItemSearchFilter {

	/**
	 * The color to use for highlighting.
	 */
	int getColor();

	/**
	 * Checks if the item matches the filter.
	 * @param stack The stack to match.
	 * @return Whether the stack does match the filter, and how exact the match is.
	 */
	IsSameResult doesMatch(ItemStack stack);

}
