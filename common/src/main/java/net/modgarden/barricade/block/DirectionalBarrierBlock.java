package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.component.BlockedDirectionsComponent;

import java.util.HashMap;
import java.util.Map;

public class DirectionalBarrierBlock extends BarrierBlock {
    public static final MapCodec<DirectionalBarrierBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockedDirectionsComponent.CODEC.fieldOf("directions").forGetter(DirectionalBarrierBlock::directions),
            propertiesCodec()
    ).apply(inst, DirectionalBarrierBlock::new));
    private static final Map<Direction, DirectionalBarrierBlock> DIRECTION_MAP = new HashMap<>() {
        @Override
        public DirectionalBarrierBlock put(Direction key, DirectionalBarrierBlock value) {
            if (containsKey(key))
                throw new RuntimeException("Cannot add direction '" + key.getName() + "' to map when it has already been added.");
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends Direction, ? extends DirectionalBarrierBlock> m) {
            if (m.keySet().stream().anyMatch(m::containsKey))
                throw new RuntimeException("Cannot add directions to map when one has already been added.");
            super.putAll(m);
        }
    };

    public BlockedDirectionsComponent directions;

    public DirectionalBarrierBlock(BlockedDirectionsComponent directions, Properties properties) {
        super(properties);
        this.directions = directions;
        if (directions.directions().size() == 1)
            DIRECTION_MAP.put(directions.directions().stream().findFirst().get(), this);
    }

    public BlockedDirectionsComponent directions() {
        return directions;
    }

    @Override
    public MapCodec<BarrierBlock> codec() {
        return CODEC.xmap(dir -> dir, barrierBlock -> (DirectionalBarrierBlock) barrierBlock);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(state.getBlock()) && !directions.blocks(direction);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (directions.doesNotBlock())
            return Shapes.empty();
        if (directions.blocksAll())
            return Shapes.block();
        Direction direction = directions.blockingDirection(pos, context);
        if (direction == null)
            return Shapes.empty();
        return Shapes.block();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ctx && ctx.getEntity() instanceof Player player && player.canUseGameMasterBlocks())
            return super.getShape(state, level, pos, context);
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        if (!(state.getBlock() instanceof DirectionalBarrierBlock directional) || directional.directions.directions().size() != 1)
            return state;
        return DIRECTION_MAP.get(rot.rotate(directional.directions.directions().stream().findFirst().get())).defaultBlockState();
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (!(state.getBlock() instanceof DirectionalBarrierBlock directional) || directional.directions.directions().size() != 1)
            return state;
        return DIRECTION_MAP.get(mirror.mirror(directional.directions.directions().stream().findFirst().get())).defaultBlockState();
    }
}
