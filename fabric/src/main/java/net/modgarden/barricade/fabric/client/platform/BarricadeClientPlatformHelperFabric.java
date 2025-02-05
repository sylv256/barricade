package net.modgarden.barricade.fabric.client.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelper;
import net.modgarden.barricade.fabric.client.model.CreativeOnlyBakedModel;

import java.util.Collection;

public class BarricadeClientPlatformHelperFabric implements BarricadeClientPlatformHelper {
    @Override
    public Collection<BlockElement> fixSeamsOnNeoForge(Collection<BlockElement> collection, TextureAtlasSprite textureAtlasSprite) {
        return collection;
    }

    @Override
    public void tessellateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, RandomSource random, long seed) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
                level,
                model,
                state,
                pos,
                poseStack,
                consumer,
                true,
                random,
                seed,
                OverlayTexture.NO_OVERLAY);
    }

    @Override
    public BakedModel createCreativeOnlyModel(BakedModel model, Either<ResourceLocation, ResourceKey<Item>> requiredItem) {
        return new CreativeOnlyBakedModel(model, requiredItem);
    }

    @Override
    public String getPackName(String name) {
        return name;
    }

}