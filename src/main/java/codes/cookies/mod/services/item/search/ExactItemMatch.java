package codes.cookies.mod.services.item.search;

import codes.cookies.mod.services.IsSameResult;

import codes.cookies.mod.services.item.ItemServices;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Matches an item as good as possible.
 * @param itemStack The item to match.
 */
public record ExactItemMatch(ItemStack itemStack) implements ItemSearchFilter {
	@Override
	public int getColor() {
		return ItemServices.getRepositoryItem(itemStack)
				.map(ItemServices::getColor)
				.orElse(Constants.FAIL_COLOR);
	}

	@Override
	public IsSameResult doesMatch(ItemStack currentlySearched) {
		final UUID uuid = itemStack.get(CookiesDataComponentTypes.UUID);
		boolean failedOne = false;
		if (uuid != null) {
			if (uuid.equals(currentlySearched.get(CookiesDataComponentTypes.UUID))) {
				return IsSameResult.YES;
			}
			failedOne = true;
		}

		if (itemStack.get(DataComponentTypes.LORE) != null || currentlySearched.get(DataComponentTypes.LORE) != null) {
			final List<Text> lines = Optional.ofNullable(itemStack.get(DataComponentTypes.LORE))
					.map(LoreComponent::lines)
					.orElse(Collections.emptyList());
			final List<Text> otherLines = Optional.ofNullable(currentlySearched.get(DataComponentTypes.LORE))
					.map(LoreComponent::lines)
					.orElse(Collections.emptyList());

			if (lines.size() != otherLines.size()) {
				failedOne = true;
			} else {
				for (int i = 0; i < lines.size(); i++) {
					final Text line = lines.get(i);
					final Text otherLine = otherLines.get(i);
					if (!line.getString().equals(otherLine.getString())) {
						failedOne = true;
						break;
					}
				}
			}
		}


		if (itemStack.get(CookiesDataComponentTypes.TIMESTAMP) != null ||
				currentlySearched.get(CookiesDataComponentTypes.TIMESTAMP) != null) {
			long timestamp = Optional.ofNullable(itemStack.get(CookiesDataComponentTypes.TIMESTAMP))
					.map(Instant::toEpochMilli)
					.orElse(-1L);
			long otherTimestamp = Optional.ofNullable(currentlySearched.get(CookiesDataComponentTypes.TIMESTAMP))
					.map(Instant::toEpochMilli)
					.orElse(-1L);

			if (timestamp != otherTimestamp) {
				failedOne = true;
			}
		}

		if (itemStack.getCount() != currentlySearched.getCount()) {
			failedOne = true;
		}

		if (ItemServices.isSame(itemStack, currentlySearched)) {
			if (failedOne) {
				return IsSameResult.ALMOST;
			}
			return IsSameResult.YES;
		}
		return IsSameResult.NO;
	}
}
