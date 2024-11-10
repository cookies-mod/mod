package codes.cookies.mod.utils.mixins;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.events.ItemLoreEvent;
import codes.cookies.mod.events.ItemStackEvents;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.accessors.CustomComponentMapAccessor;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
        ItemConvertible itemConvertible, int i, ComponentMapImpl componentMapImpl, CallbackInfo ci) {
        this.cookies$setComponents();
        CookiesDataComponentTypes.getDataTypes().forEach(this::cookies$registerType);

        NbtComponent nbtComponent = componentMapImpl.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbtCompound = nbtComponent == null ? null : nbtComponent.copyNbt();

        final Map<String, Integer> enchantMap =
            ItemUtils.getData((ItemStack) (Object) this, CookiesDataComponentTypes.ENCHANTMENTS);

        if (enchantMap != null && itemConvertible.asItem() == Items.ENCHANTED_BOOK) {
            if (enchantMap.size() == 1) {
                final Map.Entry<String, Integer> next = enchantMap.entrySet().iterator().next();
				final String internal_name = "%s;%s".formatted(next.getKey(), next.getValue()).toUpperCase(Locale.ROOT);
				set(CookiesDataComponentTypes.SKYBLOCK_ID, internal_name);
				set(CookiesDataComponentTypes.REPOSITORY_ITEM, RepositoryItem.of(internal_name));
            }
        }

        set(CookiesDataComponentTypes.SELF, (ItemStack) (Object) this);

        ItemStackEvents.EVENT.invoker().create((ItemStack) (Object) this);

        if (!ConfigManager.isLoaded()) {
            return;
        }

        this.cookies$setPetLevel(nbtCompound, componentMapImpl);

        final LoreComponent loreComponent = componentMapImpl.get(DataComponentTypes.LORE);
        if (loreComponent == null || loreComponent.lines() == null || loreComponent.lines().isEmpty()) {
            return;
        }

        final List<MutableText> lines = loreComponent.lines().stream().map(Text::copy).collect(Collectors.toList());
        ItemLoreEvent.EVENT.invoker().accept(lines);
        ItemLoreEvent.EVENT_ITEM.invoker().modify((ItemStack) (Object) this, lines);

        if (lines.size() != loreComponent.lines().size()) {
            set(
                CookiesDataComponentTypes.CUSTOM_LORE,
                lines.stream().map(Text.class::cast).collect(Collectors.toList()));
        } else {
            for (int index = 0; index < lines.size(); index++) {
                if (!lines.get(index).equals(loreComponent.lines().get(index))) {
                    set(
                        CookiesDataComponentTypes.CUSTOM_LORE,
                        lines.stream().map(Text.class::cast).collect(Collectors.toList()));
                    break;
                }
            }
        }
    }

    @Unique
    private void cookies$setComponents() {
        if (((CustomComponentMapAccessor) (Object) this.components).cookies$getComponentMapImpl() == null) {
            ((CustomComponentMapAccessor) (Object) this.components).cookies$setComponentMapImpl(new ComponentMapImpl(
                ComponentMap.EMPTY));
        }
    }

    @Unique
    private <T, D> void cookies$registerType(CookiesDataComponentTypes.DataType<T, D> dataType) {
        for (String key : dataType.key()) {
            if (this.cookies$performeTest(dataType, key)) {
                final D data = getComponents().get(dataType.source());
                set(dataType.target(), dataType.mapper().apply(data, key));
                break;
            }
        }
    }

    @Shadow
    @Nullable
    public abstract <T> T set(ComponentType<? super T> dataComponentType, @Nullable T object);

    @Unique
    private void cookies$setPetLevel(NbtCompound nbtCompound, ComponentMapImpl componentMapImpl) {
        if (nbtCompound == null) {
            return;
        }
        if (!ConfigManager.getConfig().miscConfig.showPetLevelAsStackSize.getValue()) {
            return;
        }
        if (!nbtCompound.contains("petInfo")) {
            return;
        }

        final Text text = componentMapImpl.get(DataComponentTypes.CUSTOM_NAME);

        if (text == null) {
            return;
        }


        final String level = text.getString().replaceAll("\\[Lvl (\\d+)].*", "$1");

        final Formatting tier;
        if (ConfigManager.getConfig().miscConfig.showPetRarityInLevelText.getValue()) {
            JsonObject jsonObject = JsonParser.parseString(nbtCompound.getString("petInfo")).getAsJsonObject();
            tier = RepositoryItem.Tier.valueOf(jsonObject.get("tier").getAsString()).getFormatting();
        } else {
            tier = Formatting.WHITE;
        }

        set(CookiesDataComponentTypes.CUSTOM_SLOT_TEXT, tier.toString() + level);
    }

    @Unique
    private <D, T> boolean cookies$performeTest(CookiesDataComponentTypes.DataType<T, D> dataType, String key) {
        final D data = this.components.get(dataType.source());
        if (data == null) {
            return false;
        }
        return dataType.test().apply(data, key);
    }

    @Shadow
    public abstract ComponentMap getComponents();

    @Shadow
    public abstract ComponentChanges getComponentChanges();

    @WrapOperation(
        method = "getTooltip", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;" +
                 "Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;" +
                 "Lnet/minecraft/item/tooltip/TooltipType;)V",
        ordinal = 5
    )
    )
    private <T> void getLore(
        ItemStack instance,
        ComponentType<T> componentType,
        Item.TooltipContext context,
        Consumer<Text> textConsumer,
        TooltipType type,
        Operation<Void> original) {
        if (!componentType.equals(DataComponentTypes.LORE)) {
            return;
        }

        final List<Text> data = ItemUtils.getData(instance, CookiesDataComponentTypes.CUSTOM_LORE);
        if (data == null) {
            original.call(instance, componentType, context, textConsumer, type);
            return;
        }
        data.forEach(textConsumer);
    }

}
