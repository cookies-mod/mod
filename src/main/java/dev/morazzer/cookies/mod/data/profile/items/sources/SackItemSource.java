package dev.morazzer.cookies.mod.data.profile.items.sources;

import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemSource;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;

/**
 * Item source for the sacks.
 */
public class SackItemSource implements ItemSource<Object> {

    @Getter
    private static final SackItemSource instance = new SackItemSource();

    private SackItemSource() {}

    @Override
    public Collection<Item<?>> getAllItems() {
        final Optional<ProfileData> optionalProfileData = ProfileStorage.getCurrentProfile();
        if (optionalProfileData.isEmpty()) {
            return Collections.emptySet();
        }

        final ProfileData profileData = optionalProfileData.get();
        final Map<RepositoryItem, Integer> items = profileData.getSackTracker().getItems();
        Set<Item<?>> itemList = new HashSet<>();
        items.forEach((repositoryItem, amount) -> itemList.add(new Item<>(
            repositoryItem.constructItemStack(),
            ItemSources.SACKS,
            amount,
            null)));
        itemList.removeIf(item -> item.amount() == 0);

        return itemList;
    }

    @Override
    public ItemSources getType() {
        return ItemSources.SACKS;
    }

	@Override
	public void remove(Item<?> item) {
		final Optional<ProfileData> optionalProfileData = ProfileStorage.getCurrentProfile();
		if (optionalProfileData.isEmpty()) {
			return;
		}

		final ProfileData profileData = optionalProfileData.get();
		profileData.getSackTracker().set(item.itemStack().get(CookiesDataComponentTypes.REPOSITORY_ITEM), 0);
	}
}
