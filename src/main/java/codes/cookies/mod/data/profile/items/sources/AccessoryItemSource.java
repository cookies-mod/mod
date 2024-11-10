package codes.cookies.mod.data.profile.items.sources;

import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSource;
import codes.cookies.mod.data.profile.items.ItemSources;

import codes.cookies.mod.data.profile.sub.AccessoryItemData;

import codes.cookies.mod.utils.dev.FunctionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import java.util.stream.Collectors;

import lombok.Getter;

/**
 * Item source for the accessory items.
 */
public class AccessoryItemSource implements ItemSource<AccessoryItemSource.Context> {
	@Getter
	private static final AccessoryItemSource instance = new AccessoryItemSource();

	@Override
	public Collection<Item<?>> getAllItems() {
		return ProfileStorage.getCurrentProfile().map(this::map).orElse(Collections.emptyList());
	}

	private List<Item<?>> map(ProfileData profileData) {
		return profileData.getAccessoryTracker()
				.getValue()
				.stream()
				.map(this::map)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Item<?> map(AccessoryItemData.AccessoryData accessoryData) {
		return new Item<>(
				accessoryData.itemStack(),
				this.getType(),
				accessoryData.itemStack().getCount(),
				new Context(accessoryData.page(), accessoryData.slot()));
	}

	@Override
	public ItemSources getType() {
		return ItemSources.ACCESSORY_BAG;
	}

	@Override
	public void remove(Item<?> item) {
		final Context data = (Context) item.data();

		ProfileStorage.getCurrentProfile()
				.map(ProfileData::getAccessoryTracker)
				.map(FunctionUtils.function(AccessoryItemData::remove))
				.orElseGet(FunctionUtils::noOp2)
				.accept(data.page(), data.slot());
	}

	record Context(int page, int slot) {

	}
}
