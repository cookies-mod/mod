package dev.morazzer.cookies.mod.data.profile.items.sources;

import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemSource;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.data.profile.sub.StorageData;
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

    public record Context(StorageData.StorageLocation location, int page, int slot) {}

}
