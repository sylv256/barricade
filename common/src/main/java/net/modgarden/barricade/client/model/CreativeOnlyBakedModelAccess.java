package net.modgarden.barricade.client.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.modgarden.barricade.client.util.OperatorItemPsuedoTag;

public interface CreativeOnlyBakedModelAccess {
    Either<OperatorItemPsuedoTag, ResourceKey<Item>> requiredItem();
}
