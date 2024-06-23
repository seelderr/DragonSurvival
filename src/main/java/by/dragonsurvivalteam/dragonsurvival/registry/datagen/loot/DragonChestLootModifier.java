package by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DragonChestLootModifier extends LootModifier {

    public static final MapCodec<DragonChestLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IGlobalLootModifier.LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(glm -> glm.conditions),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("table").forGetter(DragonChestLootModifier::table),
            ResourceKey.codec(Registries.LOOT_TABLE).listOf().fieldOf("tables_to_apply").forGetter(DragonChestLootModifier::tablesToApply))
                    .apply(instance, DragonChestLootModifier::new));

    private final ResourceKey<LootTable> table;
    private final List<ResourceKey<LootTable>> tablesToApply;

    public DragonChestLootModifier(LootItemCondition[] conditionsIn, ResourceKey<LootTable> table, List<ResourceKey<LootTable>> lootTables) {
        super(conditionsIn);
        this.table = table;
        this.tablesToApply = lootTables;
    }

    public ResourceKey<LootTable> table() {
        return this.table;
    }

    public List<ResourceKey<LootTable>> tablesToApply() {
        return this.tablesToApply;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        AtomicBoolean shouldApply = new AtomicBoolean(false);
        for(ResourceKey<LootTable> table : this.tablesToApply) {
            context.getResolver().get(Registries.LOOT_TABLE, table).ifPresent(tableToApply -> {
                tableToApply.unwrapKey().ifPresent(key -> {
                    if(key.location().equals(context.getQueriedLootTableId())) {
                        shouldApply.set(true);
                    }
                });
            });
        }

        if(!shouldApply.get()) {
            return generatedLoot;
        }

        context.getResolver().get(Registries.LOOT_TABLE, this.table).ifPresent(extraTable -> {
            // Don't run loot modifiers for subtables;
            // the added loot will be modifiable by downstream loot modifiers modifying the target table,
            // so if we modify it here then it could get modified twice.
            extraTable.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add));
        });
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
