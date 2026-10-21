package dev.huskuraft.universal.api.core;

import java.util.function.UnaryOperator;

import dev.huskuraft.universal.api.core.fluid.Fluid;
import dev.huskuraft.universal.api.platform.RegistryFactory;
import dev.huskuraft.universal.api.sound.SoundSet;

public interface BlockState extends StateHolder {

    Registry<BlockState> REGISTRY = RegistryFactory.getInstance().getRegistry();

    BlockState mirror(Axis axis);

    BlockState rotate(Revolve revolve);

    /**
     * Changes the direction properties of this state with the mapping, for example a 90 degree turn around the X axis.
     * These properties include facing, axis, half and the connections on each side. If a property cannot hold the new
     * value, the property does not change.
     *
     * @param mapping gives the new direction for each direction. Each direction must have a different result.
     * @return the new state, or this state if the platform does not support this function
     */
    default BlockState reorient(UnaryOperator<Direction> mapping) {
        return this;
    }

    boolean isAir();

    boolean canBeReplaced(Player player, BlockInteraction interaction);

    boolean isReplaceable();

    boolean hasTagFeatureCannotReplace();

    SoundSet getSoundSet();

    Block getBlock();

    boolean canBeReplaced(Fluid fluid);

    InteractionResult use(Player player, BlockInteraction blockInteraction);

    boolean requiresCorrectToolForDrops();

    default Item getItem() {
        return getBlock().asItem();
    }

    default BlockEntity getEntity(BlockPosition blockPosition) {
        return getBlock().getEntity(blockPosition, this);
    }

    @Deprecated
    int getRequiredItemCount(); // this is a temp solution for slab blocks drop count, will replace with loot table in the future.

}
