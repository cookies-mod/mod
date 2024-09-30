package dev.morazzer.cookies.mod.data.profile.items.sources;

import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemSource;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;

import dev.morazzer.cookies.mod.data.profile.sub.MiscItemData;

import dev.morazzer.cookies.mod.utils.dev.FunctionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Item source for misc items.
 */
public abstract class MiscItemSource implements ItemSource<MiscItemSource.Context> {

	@Override
	public Collection<Item<?>> getAllItems() {
		final Optional<ProfileData> optionalProfile = ProfileStorage.getCurrentProfile();
		if (optionalProfile.isEmpty()) {
			return Collections.emptyList();
		}
		return optionalProfile.get()
				.getMiscTracker()
				.getValue()
				.stream()
				.filter(this::doFilter)
				.map(this::map)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public boolean doFilter(MiscItemData.MiscItem miscItem) {
		return true;
	}

	private Item<?> map(MiscItemData.MiscItem miscItem) {
		return new Item<>(
				miscItem.itemStack(),
				this.getType(),
				miscItem.itemStack().getCount(),
				new Context(miscItem.type(), miscItem.slot()));
	}
	@Override
	public void remove(Item<?> item) {
		final Context data = (Context) item.data();
		ProfileStorage.getCurrentProfile()
				.map(ProfileData::getMiscTracker)
				.map(FunctionUtils.function(MiscItemData::remove))
				.orElseGet(FunctionUtils::noOp2)
				.accept(data.type, data.slot);
	}

	public record Context(MiscItemData.Type type, int slot) {

	}

	public static MiscItemSource get(MiscItemData.Type type, ItemSources sources) {
		return new MiscItemSource() {
			@Override
			public boolean doFilter(MiscItemData.MiscItem miscItem) {
				return miscItem.type() == type;
			}

			@Override
			public ItemSources getType() {
				return sources;
			}
		};
	}

}
