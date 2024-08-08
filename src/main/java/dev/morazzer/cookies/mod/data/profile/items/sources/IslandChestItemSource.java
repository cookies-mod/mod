package dev.morazzer.cookies.mod.data.profile.items.sources;

import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemSource;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.services.ItemSearchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import net.minecraft.item.ItemStack;

/**
 * Item source for the chests on the players island.
 */
public class IslandChestItemSource implements ItemSource<ItemSearchService.BiBlockPosKey> {

    @Getter
    private static final IslandChestItemSource instance = new IslandChestItemSource();

    private IslandChestItemSource() {
    }

    @Override
    public Collection<Item<?>> getAllItems() {
        final Optional<ProfileData> optionalProfileData = ProfileStorage.getCurrentProfile();
        if (optionalProfileData.isEmpty()) {
            return Collections.emptySet();
        }

        final ProfileData profileData = optionalProfileData.get();
        final List<ItemSearchService.IslandItems> items =
            profileData.getGlobalProfileData().getIslandStorage().getItems();

        Collection<Item<?>> itemList = new HashSet<>();
        items.stream().flatMap(islandItems -> {
            List<Item<?>> item = new ArrayList<>();
            for (ItemStack stack : islandItems.stacks()) {
                item.add(new Item<>(stack, ItemSources.CHESTS, stack.getCount(), islandItems.blockPos()));
            }
            return item.stream();
        }).forEach(itemList::add);

        return itemList;
    }

    @Override
    public ItemSources getType() {
        return ItemSources.CHESTS;
    }
}
