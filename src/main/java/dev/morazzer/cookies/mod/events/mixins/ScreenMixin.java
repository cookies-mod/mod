package dev.morazzer.cookies.mod.events.mixins;

import dev.morazzer.cookies.mod.events.api.ScreenKeyEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the {@link ScreenKeyEvents} to every screen.
 */
@Mixin(Screen.class)
public class ScreenMixin implements ScreenKeyEvents {
    @Unique
    Event<AllowCharTyped> cookies$allowCharTypedEvent;
    @Unique
    Event<CharTyped> cookies$beforeCharTypedEvent;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.cookies$allowCharTypedEvent = EventFactory.createArrayBacked(
            AllowCharTyped.class,
            events -> (screen, chr, modifiers) -> {
                boolean allow = true;
                for (AllowCharTyped event : events) {
                    allow = allow && event.allowKeyPress(screen, chr, modifiers);
                }
                return allow;
            });
        this.cookies$beforeCharTypedEvent = EventFactory.createArrayBacked(
            CharTyped.class,
            events -> (screen, chr, modifiers) -> {
                for (CharTyped event : events) {
                    event.keyPressed(screen, chr, modifiers);
                }
            });
    }

    @Override
    public Event<AllowCharTyped> cookies$allowCharTyped() {
        return this.cookies$allowCharTypedEvent;
    }

    @Override
    public Event<CharTyped> cookies$beforeCharTyped() {
        return this.cookies$beforeCharTypedEvent;
    }
}