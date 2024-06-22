package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks.DS_BLOCKS;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonDoor;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SourceOfMagicBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class BlockLootTableSubProvider extends BlockLootSubProvider {

    public BlockLootTableSubProvider(HolderLookup.Provider provider) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        DS_BLOCKS.getEntries().forEach((key) -> {
            Function<Block, LootTable.Builder> builder = block -> {
                if (block instanceof DragonDoor) {
                    return createSinglePropConditionTable(block, DragonDoor.PART, DragonDoor.Part.BOTTOM);
                } else if (block instanceof SourceOfMagicBlock) {
                    return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SourceOfMagicBlock.PRIMARY_BLOCK, true))))));
                } else if (block instanceof TreasureBlock) {
                    ArrayList<LootPoolSingletonContainer.Builder> list = new ArrayList<>();

                    for (Integer possibleValue : TreasureBlock.LAYERS.getPossibleValues()) {
                        LootPoolSingletonContainer.Builder entry = LootItem.lootTableItem(block)
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TreasureBlock.LAYERS, possibleValue)))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(possibleValue)));

                        list.add(entry);
                    }

                    LootPoolSingletonContainer.Builder[] arr = list.toArray(new LootPoolSingletonContainer.Builder[0]);

                    return LootTable.lootTable()
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
                                    .add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(arr))
                                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block))));
                }

                return createSingleItemTable(key.get().asItem());
            };

            add(key.get(), builder);
        });
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return DS_BLOCKS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toList());
    }
}
