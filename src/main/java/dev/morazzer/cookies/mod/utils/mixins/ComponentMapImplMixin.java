package dev.morazzer.cookies.mod.utils.mixins;

import dev.morazzer.cookies.mod.utils.accessors.CustomComponentMapAccessor;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.ComponentType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for addition of custom components without interfering with the real component map.
 */
@Mixin(ComponentMapImpl.class)
public class ComponentMapImplMixin implements CustomComponentMapAccessor {
    @Unique
    private ComponentMapImpl cookies$componentMap;

    /**
     * Redirects all custom component types to the {@linkplain ComponentMapImplMixin#cookies$componentMap}.
     *
     * @param dataComponentType The component type.
     * @param object            The value of the component.
     * @param cir               The callback.
     * @param <T>               The type of the component.
     */
    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    public <T> void set(
        ComponentType<? super T> dataComponentType,
        @Nullable T object,
        CallbackInfoReturnable<T> cir
    ) {
        if (this.cookies$componentMap != null && CookiesDataComponentTypes.isCustomType(dataComponentType)) {
            this.cookies$componentMap.set(dataComponentType, object);
            cir.setReturnValue(null);
        }
    }

    /**
     * Redirects all custom component types to the {@linkplain ComponentMapImplMixin#cookies$componentMap}.
     *
     * @param dataComponentType The component type.
     * @param cir               The callback.
     * @param <T>               The type of the component.
     */
    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public <T> void remove(
        ComponentType<? extends T> dataComponentType,
        CallbackInfoReturnable<T> cir
    ) {
        if (this.cookies$componentMap != null && CookiesDataComponentTypes.isCustomType(dataComponentType)) {
            cir.setReturnValue(this.cookies$componentMap.remove(dataComponentType));
        }
    }

    /**
     * Redirects all custom component types to the {@linkplain ComponentMapImplMixin#cookies$componentMap}.
     *
     * @param dataComponentType The component type.
     * @param cir               The callback.
     * @param <T>               The type of the component.
     */
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public <T> void get(
        ComponentType<? extends T> dataComponentType,
        CallbackInfoReturnable<T> cir
    ) {
        if (this.cookies$componentMap != null && CookiesDataComponentTypes.isCustomType(dataComponentType)) {
            cir.setReturnValue(this.cookies$componentMap.get(dataComponentType));
        }
    }


    @Override
    @Unique
    public void cookies$setComponentMapImpl(ComponentMapImpl componentMap) {
        cookies$componentMap = componentMap;
    }

    @Override
    @Unique
    public ComponentMapImpl cookies$getComponentMapImpl() {
        return cookies$componentMap;
    }
}
