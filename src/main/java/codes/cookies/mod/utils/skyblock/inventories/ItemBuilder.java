package codes.cookies.mod.utils.skyblock.inventories;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import codes.cookies.mod.utils.TextUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;

/**
 * A builder for items.
 */
public class ItemBuilder {
	private static final String SKIN_DECODED_TEMPLATE = "{\"textures\":{\"SKIN\":{\"url\":\"%S\"}}}";

	private final ItemStack itemStack;

	/**
	 * Creates a new item builder with the same item.
	 *
	 * @param itemStack The item stack.
	 */
	public ItemBuilder(ItemStack itemStack) {
		this.itemStack = itemStack.copy();
	}

	/**
	 * Creates a new item builder with the given item.
	 *
	 * @param item The item.
	 */
	public ItemBuilder(Item item) {
		this.itemStack = new ItemStack(item);
	}

	/**
	 * Sets the name of the item.
	 *
	 * @param name The name.
	 */
	public ItemBuilder setName(String name) {
		return this.setName(TextUtils.literal(name));
	}

	/**
	 * Sets the name of the item.
	 *
	 * @param name The name.
	 */
	public ItemBuilder setName(Text name) {
		this.itemStack.set(DataComponentTypes.CUSTOM_NAME, name);
		this.itemStack.set(DataComponentTypes.ITEM_NAME, name);
		return this;
	}

	/**
	 * Sets the lore of the item.
	 *
	 * @param lore The lore.
	 */
	public ItemBuilder setLore(String... lore) {
		return this.setLore(Arrays.stream(lore).map(TextUtils::literal).toArray(MutableText[]::new));
	}

	/**
	 * Sets the lore of the item.
	 *
	 * @param lore The lore.
	 */
	public ItemBuilder setLore(Text... lore) {
		this.itemStack.set(CookiesDataComponentTypes.CUSTOM_LORE, Arrays.asList(lore));
		this.itemStack.set(DataComponentTypes.LORE, new LoreComponent(Arrays.asList(lore), Arrays.asList(lore)));
		return this;
	}

	/**
	 * Hides the tooltips of the item.
	 */
	public ItemBuilder hideTooltips() {
		this.itemStack.set(DataComponentTypes.HIDE_TOOLTIP, Unit.INSTANCE);
		return this;
	}

	/**
	 * Hides the additional tooltips of the item.
	 */
	public ItemBuilder hideAdditionalTooltips() {
		this.itemStack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
		return this;
	}

	/**
	 * Sets the lore to an empty list.
	 */
	public ItemBuilder setLore() {
		this.itemStack.set(CookiesDataComponentTypes.CUSTOM_LORE, Collections.emptyList());
		return this;
	}

	/**
	 * Sets the item to have glint.
	 */
	public ItemBuilder setGlint() {
		return this.setGlint(true);
	}

	/**
	 * Sets the item to have glint or not.
	 *
	 * @param hasGlint Whether the item should have glint.
	 */
	public ItemBuilder setGlint(boolean hasGlint) {
		this.itemStack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, hasGlint);
		return this;
	}

	/**
	 * Sets a runnable to execute when clicked.
	 *
	 * @param runnable The runnable.
	 */
	public ItemBuilder setClickRunnable(Runnable runnable) {
		this.itemStack.set(CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE, runnable);
		return this;
	}

	/**
	 * Sets a consumer to run when clicked.
	 *
	 * @param consumer The consumer.
	 */
	public ItemBuilder setClickConsumer(Consumer<Integer> consumer) {
		this.itemStack.set(CookiesDataComponentTypes.ITEM_CLICK_CONSUMER, consumer);
		return this;
	}

	/**
	 * Sets the skin of the item to the provided skin.
	 *
	 * @param skin The skin.
	 */
	public ItemBuilder setSkinUrl(String skin) {
		return setSkin(Base64.getEncoder()
				.encodeToString(SKIN_DECODED_TEMPLATE.formatted(skin.toLowerCase()).getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Sets the skin of the item to the provided skin.
	 *
	 * @param skin The skin.
	 */
	public ItemBuilder setSkin(String skin) {
		final ProfileComponent component = new ProfileComponent(new GameProfile(UUID.randomUUID(), "meowora"));
		component.properties().put("textures", new Property("textures", skin));
		return set(DataComponentTypes.PROFILE, component);
	}

	/**
	 * Sets an arbitrary component to the provided value.
	 *
	 * @param componentType The component to set.
	 * @param value         The value to set.
	 * @param <T>           The type of the value.
	 */
	public <T> ItemBuilder set(ComponentType<T> componentType, T value) {
		this.itemStack.set(componentType, value);
		return this;
	}

	/**
	 * @return The new item stack.
	 */
	public ItemStack build() {
		return this.itemStack.copy();
	}

	public ItemBuilder appendLore(Text... text) {
		for (Text text1 : text) {
			appendLore(text1);
		}
		return this;
	}

	public ItemBuilder appendLore(Text text) {
		final List<Text> texts = Optional.ofNullable(this.itemStack.get(CookiesDataComponentTypes.CUSTOM_LORE))
				.map(ArrayList::new)
				.orElseGet(ArrayList::new);
		if (texts.isEmpty()) {
			return this.setLore(text);
		}
		texts.add(text);
		return this.setLore(texts.toArray(Text[]::new));
	}
}
