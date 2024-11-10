package codes.cookies.mod.events.profile;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Event that is called when the mod detects a server swap.
 */
@FunctionalInterface
public interface ServerSwapEvent {

    /**
     * Called when the player swaps server.
     */
    Event<ServerSwapEvent> SERVER_SWAP = EventFactory.createArrayBacked(
        ServerSwapEvent.class,
        serverSwapEvents -> () -> {
            for (ServerSwapEvent serverSwapEvent : serverSwapEvents) {
                serverSwapEvent.onServerSwap();
            }
        }
    );

    /**
     * Called when the player swaps server, also contains the server id.
     */
    Event<Consumer<String>> SERVER_SWAP_ID = EventFactory.createArrayBacked(
        Consumer.class,
        serverSwapEvents -> serverId -> {
            for (Consumer<String> serverSwapEvent : serverSwapEvents) {
                serverSwapEvent.accept(serverId);
            }
        }
    );

    /**
     * Called when the mod detects a server swap.
     */
    void onServerSwap();

}
