package dev.huskuraft.universal.vanilla.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import dev.huskuraft.universal.api.core.Axis;
import dev.huskuraft.universal.api.core.Block;
import dev.huskuraft.universal.api.core.BlockInteraction;
import dev.huskuraft.universal.api.core.BlockState;
import dev.huskuraft.universal.api.core.InteractionResult;
import dev.huskuraft.universal.api.core.ItemStack;
import dev.huskuraft.universal.api.core.Player;
import dev.huskuraft.universal.api.core.Property;
import dev.huskuraft.universal.api.core.PropertyValue;
import dev.huskuraft.universal.api.core.Revolve;
import dev.huskuraft.universal.api.core.fluid.Fluid;
import dev.huskuraft.universal.api.sound.SoundSet;
import dev.huskuraft.universal.vanilla.sound.MinecraftSoundSet;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

public record MinecraftBlockState(net.minecraft.world.level.block.state.BlockState refs) implements BlockState {

    public static BlockState ofNullable(net.minecraft.world.level.block.state.BlockState refs) {
        if (refs == null) return null;
        return new MinecraftBlockState(refs);
    }

    public static net.minecraft.world.level.block.state.BlockState mirrorTopBottom(net.minecraft.world.level.block.state.BlockState value) {
        // stairs
        if (value.getBlock() instanceof StairBlock) {
            return switch (value.getValue(StairBlock.HALF)) {
                case TOP -> value.setValue(StairBlock.HALF, Half.BOTTOM);
                case BOTTOM -> value.setValue(StairBlock.HALF, Half.TOP);
            };
        }

        // slabs
        if (value.getBlock() instanceof SlabBlock) {
            return switch (value.getValue(SlabBlock.TYPE)) {
                case TOP -> value.setValue(SlabBlock.TYPE, SlabType.BOTTOM);
                case BOTTOM -> value.setValue(SlabBlock.TYPE, SlabType.TOP);
                case DOUBLE -> value;
            };
        }

        // FIXME: 16/10/23
//        // buttons, endrod, observer, piston
        if (value.getBlock() instanceof DirectionalBlock) {
            switch (value.getValue(DirectionalBlock.FACING)) {
                case DOWN -> {
                    return value.setValue(DirectionalBlock.FACING, Direction.UP);
                }
                case UP -> {
                    return value.setValue(DirectionalBlock.FACING, Direction.DOWN);
                }
            }
        }

        // dispenser, dropper
        if (value.getBlock() instanceof DispenserBlock) {
            switch (value.getValue(DispenserBlock.FACING)) {
                case DOWN -> {
                    return value.setValue(DispenserBlock.FACING, Direction.UP);
                }
                case UP -> {
                    return value.setValue(DispenserBlock.FACING, Direction.DOWN);
                }
            }
        }
        return value;
    }

    public static net.minecraft.world.level.block.state.BlockState mirrorLeftRight(net.minecraft.world.level.block.state.BlockState value) {
        return value.mirror(Mirror.LEFT_RIGHT);
    }

    public static net.minecraft.world.level.block.state.BlockState mirrorFrontBack(net.minecraft.world.level.block.state.BlockState value) {
        return value.mirror(Mirror.FRONT_BACK);
    }

    @Override
    public BlockState rotate(Revolve revolve) {
        return ofNullable(refs.rotate(switch (revolve) {
            case NONE -> Rotation.NONE;
            case CLOCKWISE_90 -> Rotation.CLOCKWISE_90;
            case CLOCKWISE_180 -> Rotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
        }));
    }

    public static net.minecraft.world.level.block.state.BlockState reorient(net.minecraft.world.level.block.state.BlockState value, UnaryOperator<Direction> mapping) {
        var result = value;
        var sides = new HashMap<Direction, net.minecraft.world.level.block.state.properties.Property<?>>();
        for (var property : value.getProperties()) {
            var side = Direction.byName(property.getName());
            if (side != null) {
                sides.put(side, property);
            } else if (property.getValueClass() == Direction.class) {
                result = setValueIfPossible(result, property, mapping.apply((Direction) value.getValue(property)));
            } else if (property.getValueClass() == Direction.Axis.class) {
                var axis = (Direction.Axis) value.getValue(property);
                result = setValueIfPossible(result, property, mapping.apply(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE)).getAxis());
            } else if (property.getValueClass() == Half.class) {
                var direction = mapping.apply(value.getValue(property) == Half.TOP ? Direction.UP : Direction.DOWN);
                if (direction.getAxis() == Direction.Axis.Y) {
                    result = setValueIfPossible(result, property, direction == Direction.UP ? Half.TOP : Half.BOTTOM);
                }
            } else if (property.getValueClass() == SlabType.class && value.getValue(property) != SlabType.DOUBLE) {
                var direction = mapping.apply(value.getValue(property) == SlabType.TOP ? Direction.UP : Direction.DOWN);
                if (direction.getAxis() == Direction.Axis.Y) {
                    result = setValueIfPossible(result, property, direction == Direction.UP ? SlabType.TOP : SlabType.BOTTOM);
                }
            }
        }
        // Properties for each side, for example on mushroom blocks and glow lichen. Change them only if each side has a new side.
        if (sides.keySet().stream().allMatch(side -> sides.containsKey(mapping.apply(side)))) {
            for (var entry : sides.entrySet()) {
                result = setValueIfPossible(result, sides.get(mapping.apply(entry.getKey())), value.getValue(entry.getValue()));
            }
        }
        return result;
    }

    private static <T extends Comparable<T>> net.minecraft.world.level.block.state.BlockState setValueIfPossible(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.block.state.properties.Property<T> property, Object value) {
        if (!property.getValueClass().isInstance(value) || !property.getPossibleValues().contains(value)) {
            return state;
        }
        return state.setValue(property, property.getValueClass().cast(value));
    }

    @Override
    public BlockState reorient(UnaryOperator<dev.huskuraft.universal.api.core.Direction> mapping) {
        return ofNullable(reorient(refs, direction -> MinecraftConvertor.toPlatformDirection(mapping.apply(MinecraftConvertor.fromPlatformDirection(direction)))));
    }

    @Override
    public boolean isAir() {
        return refs.isAir();
    }

    // Mods such as Full Slabs add an axis to vertical slabs. The slab type is then the side on that axis.
    private static net.minecraft.world.level.block.state.BlockState mirrorAxisSlab(net.minecraft.world.level.block.state.BlockState value, Axis axis) {
        var slabAxis = value.getValue(BlockStateProperties.AXIS);
        var mirrorAxis = switch (axis) {
            case X -> Direction.Axis.X;
            case Y -> Direction.Axis.Y;
            case Z -> Direction.Axis.Z;
        };
        if (slabAxis != mirrorAxis) {
            return value;
        }
        return switch (value.getValue(SlabBlock.TYPE)) {
            case TOP -> value.setValue(SlabBlock.TYPE, SlabType.BOTTOM);
            case BOTTOM -> value.setValue(SlabBlock.TYPE, SlabType.TOP);
            case DOUBLE -> value;
        };
    }

    @Override
    public BlockState mirror(Axis axis) {
        if (refs.getBlock() instanceof SlabBlock && refs.hasProperty(BlockStateProperties.AXIS)) {
            return ofNullable(mirrorAxisSlab(refs, axis));
        }
        return switch (axis) {
            case Y -> ofNullable(mirrorTopBottom(refs));
            case X -> ofNullable(mirrorFrontBack(refs));
            case Z -> ofNullable(mirrorLeftRight(refs));
        };
    }

    @Override
    public boolean canBeReplaced(Player player, BlockInteraction interaction) {
        ItemStack itemStack = player.getItemStack(interaction.getHand());
        return refs.canBeReplaced(new net.minecraft.world.item.context.BlockPlaceContext(
                player.reference(),
                MinecraftConvertor.toPlatformInteractionHand(interaction.getHand()),
                itemStack.reference(),
                MinecraftConvertor.toPlatformBlockInteraction(interaction)
        ));
    }

    @Override
    public boolean isReplaceable() {
        return false; // reference.canBeReplaced();
    }

    @Override
    public boolean hasTagFeatureCannotReplace() {
        return refs.is(BlockTags.FEATURES_CANNOT_REPLACE);
    }

    @Override
    public SoundSet getSoundSet() {
        return new MinecraftSoundSet(refs.getSoundType());
    }

    @Override
    public Map<Property, PropertyValue> getPropertiesMap() {
        return refs.getValues().entrySet().stream().collect(Collectors.toMap(entry -> new MinecraftProperty(entry.getKey()), entry -> new MinecraftPropertyValue(entry.getValue())));
    }

    @Override
    public Block getBlock() {
        return new MinecraftBlock(refs.getBlock());
    }

    @Override
    public boolean canBeReplaced(Fluid fluid) {
        return refs.canBeReplaced((net.minecraft.world.level.material.Fluid) fluid.reference());
    }

    @Override
    public InteractionResult use(Player player, BlockInteraction blockInteraction) {
        return MinecraftConvertor.fromPlatformInteractionResult(refs.use(player.getWorld().reference(), player.reference(), MinecraftConvertor.toPlatformInteractionHand(blockInteraction.getHand()), MinecraftConvertor.toPlatformBlockInteraction(blockInteraction)));
    }

    @Override
    public boolean requiresCorrectToolForDrops() {
        return refs.requiresCorrectToolForDrops();
    }

    @Override
    public int getRequiredItemCount() {
        if (refs.getBlock() instanceof SlabBlock && refs.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
            return 2;
        }
        return 1;
    }
}
