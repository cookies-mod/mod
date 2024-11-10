package codes.cookies.mod.data.profile.items.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSource;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.data.profile.profile.GlobalProfileData;
import codes.cookies.mod.data.profile.profile.IslandChestStorage;
import codes.cookies.mod.utils.dev.FunctionUtils;
import lombok.Getter;

/**
 * Item source for the chests on the players island.
 */
public class IslandChestItemSource implements ItemSource<IslandChestStorage.ChestItem> {

	@Getter
	private static final IslandChestItemSource instance = new IslandChestItemSource();

	private IslandChestItemSource() {
	}

	@Override
	public Collection<Item<?>> getAllItems() {
		List<IslandChestStorage.ChestItem> items = ProfileStorage.getCurrentProfile()
				.flatMap(ProfileData::getGlobalProfileData)
				.map(GlobalProfileData::getIslandStorage)
				.map(IslandChestStorage::getItems)
				.orElse(Collections.emptyList());

		Collection<Item<?>> item = new ArrayList<>();
		items.forEach(chestItem -> item.add(new Item<>(chestItem.itemStack(),
				ItemSources.CHESTS,
				chestItem.itemStack().getCount(),
				chestItem)));

		return item;
	}

	@Override
	public ItemSources getType() {
		return ItemSources.CHESTS;
	}

	@Override
	public void remove(Item<?> item) {
		final IslandChestStorage.ChestItem data = (IslandChestStorage.ChestItem) item.data();

		ProfileStorage.getCurrentProfile()
				.flatMap(ProfileData::getGlobalProfileData)
				.map(GlobalProfileData::getIslandStorage)
				.map(FunctionUtils.function(IslandChestStorage::remove))
				.orElse(FunctionUtils.noOp())
				.accept(data);
	}
}
