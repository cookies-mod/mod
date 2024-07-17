package dev.morazzer.cookies.mod.features.misc.items;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.services.ItemSearchService;
import dev.morazzer.cookies.mod.utils.minecraft.LocationUtils;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Tracker to save the contents of chests on the private island.
 */
public class ChestTracker {
    private BlockPos lastInteraction;
    private BlockPos doubleChestPosition;

    /**
     * Creates a new chest tracker.
     */
    public ChestTracker() {
        UseBlockCallback.EVENT.register(this::blockClicked);
        ClientPlayerBlockBreakEvents.AFTER.register(this::blockBreak);
        ScreenEvents.BEFORE_INIT.register(this::openScreen);
    }

    private ActionResult blockClicked(
        PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        this.resetCoords();

        if (!ConfigManager.getConfig().helpersConfig.itemChestTracker.getValue()) {
            return ActionResult.PASS;
        }

        if (!LocationUtils.Island.PRIVATE_ISLAND.isActive()) {
            return ActionResult.PASS;
        }

        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return ActionResult.PASS;
        }

        final BlockState blockState = world.getBlockState(blockHitResult.getBlockPos());
        final Block block = blockState.getBlock();
        if (block != Blocks.CHEST && block != Blocks.TRAPPED_CHEST) {
            return ActionResult.PASS;
        }

        this.lastInteraction = blockHitResult.getBlockPos();
        final ChestType chestType = blockState.get(Properties.CHEST_TYPE);
        if (chestType != ChestType.SINGLE) {
            BlockPos diff = switch (blockState.get(Properties.HORIZONTAL_FACING)) {
                case EAST -> new BlockPos(0, 0, 1);
                case WEST -> new BlockPos(0, 0, -1);
                case NORTH -> new BlockPos(1, 0, 0);
                case SOUTH -> new BlockPos(-1, 0, 0);
                default -> BlockPos.ORIGIN;
            };

            if (chestType != ChestType.LEFT) {
                diff = diff.multiply(-1);
            }
            this.doubleChestPosition = blockHitResult.getBlockPos().add(diff);
        }

        return ActionResult.PASS;
    }

    private void blockBreak(
        ClientWorld clientWorld, ClientPlayerEntity clientPlayerEntity, BlockPos blockPos, BlockState blockState) {
        if (!LocationUtils.Island.PRIVATE_ISLAND.isActive()) {
            return;
        }

        if (!blockState.isOf(Blocks.CHEST) && !blockState.isOf(Blocks.TRAPPED_CHEST)) {
            return;
        }

        ItemSearchService.chestBreak(blockPos);
    }

    private void openScreen(MinecraftClient minecraftClient, Screen screen, int scaledWidth, int scaledHeight) {
        if (!(screen instanceof HandledScreen<?>)) {
            return;
        }

        if (!ConfigManager.getConfig().helpersConfig.itemChestTracker.getValue()) {
            return;
        }

        if (!LocationUtils.Island.PRIVATE_ISLAND.isActive()) {
            return;
        }

        if (this.lastInteraction == null) {
            return;
        }

        if (!(screen.getTitle() instanceof MutableText mutable)) {
            return;
        }

        if (!(mutable.getContent() instanceof TranslatableTextContent translatableText)) {
            return;
        }

        final String translationKey = translatableText.getKey();

        if (!translationKey.startsWith("container.chest")) {
            return;
        }

        ScreenEvents.remove(screen).register(this::saveScreen);
    }

    private void resetCoords() {
        lastInteraction = null;
        doubleChestPosition = null;
    }

    private void saveScreen(Screen screen) {
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;

        List<ItemStack> items = new ArrayList<>();
        for (Slot slot : handledScreen.getScreenHandler().slots) {
            if (slot.inventory instanceof PlayerInventory || slot.getStack().isEmpty()) {
                continue;
            }

            items.add(slot.getStack());
        }

        saveItems(items, this.lastInteraction, this.doubleChestPosition);
    }

    private void saveItems(List<ItemStack> items, BlockPos first, BlockPos second) {
        ItemSearchService.add(first, second, items);
    }

}
