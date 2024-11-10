package codes.cookies.mod.events.profile;

import codes.cookies.mod.utils.SkyblockUtils;
import java.util.UUID;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Various events related to profile swaps.
 */
@FunctionalInterface
public interface ProfileSwapEvent {

    /**
     * Called when the player swapped their profile, contains old and new uuid.
     */
    Event<ProfileSwapEvent> EVENT = EventFactory.createArrayBacked(
        ProfileSwapEvent.class,
        profileSwapEvents -> (previous, current) -> {
            for (ProfileSwapEvent profileSwapEvent : profileSwapEvents) {
                profileSwapEvent.swapProfile(previous, current);
            }
        }
    );

    /**
     * Called when the player swapped their profile, contains old and new uuid, though it is called after the {@link SkyblockUtils#getLastProfileId()} was changed.
     */
    Event<ProfileSwapEvent> AFTER_SET = EventFactory.createArrayBacked(
        ProfileSwapEvent.class,
        profileSwapEvents -> (previous, current) -> {
            for (ProfileSwapEvent profileSwapEvent : profileSwapEvents) {
                profileSwapEvent.swapProfile(previous, current);
            }
        }
    );

    /**
     * Called when the player swapped their profile.
     */
    Event<Runnable> EVENT_NO_UUID = EventFactory.createArrayBacked(
        Runnable.class,
        subscribers -> () -> {
            for (Runnable runnable : subscribers) {
                runnable.run();
            }
        }
    );

    /**
     * Called when the player swapped their profile, though it is called after the {@link SkyblockUtils#getLastProfileId()} was changed.
     */
    Event<Runnable> AFTER_SET_NO_UUID = EventFactory.createArrayBacked(
        Runnable.class,
        subscribers -> () -> {
            for (Runnable runnable : subscribers) {
                runnable.run();
            }
        }
    );


    /**
     * Called when the mod detects a switch between two profiles.
     *
     * @param previous The previously loaded profile uuid.
     * @param current  The now loaded profile uuid.
     */
    void swapProfile(UUID previous, UUID current);


}
