package codes.cookies.mod.events;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Event to keep track of various scoreboard changes.
 */
public interface ScoreboardUpdateEvent {

    Event<ScoreboardUpdateEvent> EVENT = EventFactory.createArrayBacked(ScoreboardUpdateEvent.class, events -> (line, content) -> {
        for (ScoreboardUpdateEvent event : events) {
            event.update(line, content);
        }
    });

	/**
	 * Registers listener that only reacts to a specific line.
	 * @param line The line to look for.
	 * @param consumer The listener.
	 */
    static void register(int line, Consumer<String> consumer) {
        EVENT.register((line1, content) -> {
            if (line1 == line) {
                consumer.accept(content);
            }
        });
    }

	/**
	 * Called whenever a scoreboard line updates.
	 * @param line The line the update was in.
	 * @param content The new content of the line.
	 */
    void update(int line, String content);

}
