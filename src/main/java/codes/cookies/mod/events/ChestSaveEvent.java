package codes.cookies.mod.events;

import java.util.List;

import codes.cookies.mod.data.profile.profile.IslandChestStorage;

import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChestSaveEvent {

	Event<ChestSaveEvent> EVENT = EventFactory.createArrayBacked(
			ChestSaveEvent.class,
			events -> ((blockPos, second, items) -> {
				for (ChestSaveEvent event : events) {
					event.onSave(blockPos, second, items);
				}
			}));

	void onSave(BlockPos blockPos, BlockPos second, List<IslandChestStorage.ChestItem> items);

}
