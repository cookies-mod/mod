package codes.cookies.mod.features.misc.utils.crafthelper;

import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.repository.RepositoryItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ToolTipContext {

	public CraftHelperInstance instance;
	boolean childrenDone = true;
	public boolean canSupercraft = false;
	boolean hasChildren = false;
	ItemTracker itemTracker;
	int required;
	int amount;
	int amountThroughParents;
	final RepositoryItem repositoryItem;
	final String prefix;
	boolean isLast = false;
	boolean hasBeenInitialized = false;
	public boolean nonCachedText = false;
	String path;
	List<ItemSources> usedSources = new ArrayList<>();

	public boolean isDone() {
		return required == amount;
	}
}
