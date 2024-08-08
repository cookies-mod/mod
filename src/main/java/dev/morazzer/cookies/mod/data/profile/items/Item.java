package dev.morazzer.cookies.mod.data.profile.items;

import net.minecraft.item.ItemStack;

/**
 * An item from an {@link ItemSource}.
 * @param itemStack The item stack represented by this item.
 * @param source The source of the item.
 * @param amount The amount that is stored.
 * @param data Data that was attached to the item.
 * @param <T> The type of the data.
 */
public record Item<T>(ItemStack itemStack, ItemSources source, int amount, T data) {}
