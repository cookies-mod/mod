package dev.morazzer.cookies.mod.utils.mixins;

import dev.morazzer.cookies.mod.utils.accessors.CustomComponentMapAccessor;
import dev.morazzer.cookies.mod.utils.items.SkyblockDataComponentTypes;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows for custom data component types.
 */
@Mixin(ItemStack.class)
@SuppressWarnings("MissingJavadoc")
public abstract class ItemStackMixin {

    @Shadow
    @Final
    ComponentMapImpl components;

    @Inject(
        method = "<init>(Lnet/minecraft/item/ItemConvertible;ILnet/minecraft/component/ComponentMapImpl;)V",
        at = @At(value = "RETURN")
    )
    public void initializeItemStackWithComponents(
        ItemConvertible itemConvertible,
        int i,
        ComponentMapImpl componentMapImpl,
        CallbackInfo ci
    ) {
        this.cookies$setComponents();
        SkyblockDataComponentTypes.getDataTypes().forEach(this::cookies$registerType);
    }

    @Unique
    private void cookies$setComponents() {
        ((CustomComponentMapAccessor) (Object) this.components).cookies$setComponentMapImpl(
            new ComponentMapImpl(ComponentMap.EMPTY));
    }

    @Unique
    private <T, D> void cookies$registerType(SkyblockDataComponentTypes.DataType<T, D> dataType) {
        for (String key : dataType.key()) {
            if (this.cookies$performeTest(dataType, key)) {
                final D data = getComponents().get(dataType.source());
                set(dataType.target(), dataType.mapper().apply(data, key));
                break;
            }
        }
    }

    @Unique
    private <D, T> boolean cookies$performeTest(SkyblockDataComponentTypes.DataType<T, D> dataType, String key) {
        final D data = this.components.get(dataType.source());
        if (data == null) {
            return false;
        }
        return dataType.test().apply(data, key);
    }

    @Shadow
    public abstract ComponentMap getComponents();

    @Shadow
    @Nullable
    public abstract <T> T set(ComponentType<? super T> dataComponentType,
                              @Nullable T object);

}
