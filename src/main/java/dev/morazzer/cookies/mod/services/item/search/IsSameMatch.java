package dev.morazzer.cookies.mod.services.item.search;

import java.util.Optional;

import dev.morazzer.cookies.mod.services.IsSameResult;
import dev.morazzer.cookies.mod.services.item.ItemServices;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemStack;

public record IsSameMatch(@NotNull ItemStack itemStack) implements ItemSearchFilter {

	@Override
	public int getColor() {
		return ItemServices.getRepositoryItem(itemStack)
				.map(ItemServices::getColor)
				.orElse(Constants.FAIL_COLOR);
	}

	@Override
	public IsSameResult doesMatch(ItemStack stack) {
		return getResult(stack).map(IsSameResult::wrapBoolean).orElse(IsSameResult.NO);
	}

	Optional<Boolean> getResult(ItemStack stack) {
		return Optional.ofNullable(stack).map(check -> ItemServices.isSame(check, itemStack));
	}
}
