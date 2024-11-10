package codes.cookies.mod.events;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * Helper to make inventory based screen events easier.
 */
public class InventoryEvents {

    /**
     * Registers an after init event handler.
     *
     * @param name             The name of the inventory.
     * @param shouldInstrument Whether the event should be called or not.
     * @param inventoryEvent   The inventory event to run.
     */
    public static void afterInit(
        String name, Predicate<HandledScreen<?>> shouldInstrument, InventoryEvent inventoryEvent) {
        register(
            name,
            shouldInstrument,
            inventoryEvent,
            ScreenEvents.AFTER_INIT,
            event -> (client, screen, scaledWidth, scaledHeight) -> invokeIfInventory(screen, event));
    }

    /**
     * Registers an after init event handler.
     *
     * @param name                 The name of the inventory.
     * @param shouldInstrument     Whether the event should be called or not.
     * @param inventoryEvent       The inventory event to run.
     * @param event                The event to register to.
     * @param inventoryEventMapper A mapper to convert between {@link InventoryEvent} and {@link T}
     * @param <T>                  The type of the event.
     */
    private static <T> void register(
        String name,
        Predicate<HandledScreen<?>> shouldInstrument,
        InventoryEvent inventoryEvent,
        Event<T> event,
        Function<InventoryEvent, T> inventoryEventMapper) {
        event.register(inventoryEventMapper.apply(registerInternal(name, shouldInstrument, inventoryEvent)));
    }

    private static void invokeIfInventory(Screen screen, InventoryEvent inventoryEvent) {
        if (!(screen instanceof HandledScreen<?> handledScreen)) {
            return;
        }
        inventoryEvent.open(handledScreen);
    }

    private static InventoryEvent registerInternal(
        String name, Predicate<HandledScreen<?>> shouldInstrument, InventoryEvent inventoryEvent) {
        return screen -> {
            if (screen.getTitle() == null) {
                return;
            }
            String literalName = screen.getTitle().getString();
            if (!CookiesUtils.match(literalName, name)) {
                return;
            }

            if (!shouldInstrument.test(screen)) {
                return;
            }

            inventoryEvent.open(screen);
        };
    }

    /**
     * Registers an on remove handler.
     *
     * @param originalScreen   The screen.
     * @param name             The name of the inventory.
     * @param shouldInstrument Whether the event should be called or not.
     * @param inventoryEvent   The inventory event to run.
     */
    public static void remove(
        Screen originalScreen,
        String name,
        Predicate<HandledScreen<?>> shouldInstrument,
        InventoryEvent inventoryEvent) {
        register(
            name,
            shouldInstrument,
            inventoryEvent,
            ScreenEvents.remove(originalScreen),
            event -> screen -> invokeIfInventory(screen, event));
    }

    /**
     * Registers an before init event handler.
     *
     * @param name             The name of the inventory.
     * @param shouldInstrument Whether the event should be called or not.
     * @param inventoryEvent   The inventory event to run.
     */
    public static void beforeInit(
        String name, Predicate<HandledScreen<?>> shouldInstrument, InventoryEvent inventoryEvent) {
        register(
            name,
            shouldInstrument,
            inventoryEvent,
            ScreenEvents.BEFORE_INIT,
            event -> (client, screen, scaledWidth, scaledHeight) -> invokeIfInventory(screen, event));
    }

    private static <T> void register(
        String name, Event<T> event, InventoryEvent inventoryEvent, Function<InventoryEvent, T> inventoryEventMapper) {
        register(name, screen -> true, inventoryEvent, event, inventoryEventMapper);
    }

    /**
     * An inventory event.
     */
    public interface InventoryEvent {
        void open(HandledScreen<?> handledScreen);
    }
}
