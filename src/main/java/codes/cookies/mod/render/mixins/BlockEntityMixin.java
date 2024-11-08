package codes.cookies.mod.render.mixins;

import codes.cookies.mod.render.BlockEntityAccessor;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Allows for accessing information about outlines on block entities.
 */
@Mixin(BlockEntity.class)
public class BlockEntityMixin implements BlockEntityAccessor {

    @Unique
    private boolean cookies$highlighted = false;
    @Unique
    private int cookies$color = 0;

    @Override
    @Unique
    public void cookies$setHighlighted(boolean highlighted) {
        this.cookies$highlighted = highlighted;
    }

    @Override
    @Unique
    public void cookies$setHighlightedColor(int highlighted) {
        this.cookies$color = highlighted;
    }

    @Override
    @Unique
    public boolean cookies$isHighlighted() {
        return this.cookies$highlighted;
    }

    @Override
    @Unique
    public int cookies$getHighlightedColor() {
        return this.cookies$color;
    }
}
