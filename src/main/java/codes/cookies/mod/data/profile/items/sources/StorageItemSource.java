package codes.cookies.mod.data.profile.items.sources;

import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSource;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.data.profile.sub.StorageData;

import codes.cookies.mod.utils.dev.FunctionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;

/**
 * Item source for storage items.
 */
public class StorageItemSource implements ItemSource<StorageItemSource.Context> {

	@Getter
	private static final StorageItemSource instance = new StorageItemSource();

	private StorageItemSource() {}

	@Override
	public Collection<Item<?>> getAllItems() {
		final Optional<ProfileData> optionalProfileData = ProfileStorage.getCurrentProfile();
		if (optionalProfileData.isEmpty()) {
			return Collections.emptySet();
		}

		Set<Item<?>> items = new HashSet<>();
		final ProfileData profileData = optionalProfileData.get();

		profileData.getStorageData().getAllItems().stream().map(item -> new Item<>(
				item.itemStack(),
				ItemSources.STORAGE,
				item.itemStack().getCount(),
				new Context(item.storageLocation(), item.page(), item.slot()))).forEach(items::add);
		return items;
	}

	@Override
	public ItemSources getType() {
		return ItemSources.STORAGE;
	}

	@Override
	public void remove(Item<?> item) {
		Context context = (Context) item.data();
		final StorageData.StorageLocation location = context.location();
		final int page = context.page;
		final int slot = context.slot;

		ProfileStorage.getCurrentProfile()
				.map(ProfileData::getStorageData)
				.map(FunctionUtils.function(StorageData::removeItem))
				.orElseGet(FunctionUtils::noOp3)
				.accept(page, slot, location);
	}

	public record Context(StorageData.StorageLocation location, int page, int slot) {
		public int getPageWithOffset() {
			if (this.location == StorageData.StorageLocation.BACKPACK) {
				return 10 + this.page;
			}
			return this.page + 1;
		}

		public boolean pageEquals(Object object) {
			if (object instanceof Context context) {
				return this.page == context.page && this.location == context.location;
			}
			return false;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Context other) {
				return other.location == this.location && other.page == this.page && other.slot == this.slot;
			}
			return false;
		}
	}

	public static int getActualPage(int page) {
		if (page > 9) {
			return page - 9;
		}
		return page;
	}

	public static StorageData.StorageLocation getLocation(int page) {
		if (page > 9) {
			return StorageData.StorageLocation.BACKPACK;
		}
		return StorageData.StorageLocation.ENDER_CHEST;
	}

}
