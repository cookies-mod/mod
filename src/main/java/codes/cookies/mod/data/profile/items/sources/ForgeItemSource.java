package codes.cookies.mod.data.profile.items.sources;

import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSource;
import codes.cookies.mod.data.profile.items.ItemSources;

import codes.cookies.mod.data.profile.sub.ForgeTracker;

import codes.cookies.mod.repository.RepositoryItem;

import codes.cookies.mod.utils.dev.FunctionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import java.util.Objects;
import java.util.Optional;

import java.util.stream.Collectors;

import lombok.Getter;

/**
 * Item source for forge processes.
 */
public class ForgeItemSource implements ItemSource<ForgeItemSource.Context> {
	@Getter
	private static final ForgeItemSource instance = new ForgeItemSource();

	@Override
	public Collection<Item<?>> getAllItems() {
		final Optional<ProfileData> optionalProfile = ProfileStorage.getCurrentProfile();
		if (optionalProfile.isEmpty()) {
			return Collections.emptyList();
		}
		return optionalProfile.get()
				.getForgeTracker()
				.getData()
				.stream()
				.map(this::map)
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Item<?> map(ForgeTracker.Data data) {
		final RepositoryItem repositoryItem = data.repositoryItem();
		if (repositoryItem == null) {
			return null;
		}

		return new Item<>(
				repositoryItem.constructItemStack(),
				ItemSources.FORGE,
				1,
				new Context(data.slot(), data.startedSeconds()));
	}

	@Override
	public ItemSources getType() {
		return ItemSources.FORGE;
	}

	@Override
	public void remove(Item<?> item) {
		final Context data = (Context) item.data();

		ProfileStorage.getCurrentProfile()
				.map(ProfileData::getForgeTracker)
				.map(FunctionUtils.function(ForgeTracker::remove))
				.orElseGet(FunctionUtils::noOp)
				.accept(data.slot());
	}

	public record Context(int slot, long startTime) {}
}
