package by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class DragonHeartLootModifier extends LootModifier {
    // No codec at the moment. This is just a formality.
    public static final Supplier<MapCodec<DragonHeartLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, DragonHeartLootModifier::new)));

    public DragonHeartLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if(!(entity instanceof LivingEntity) || entity instanceof Player){
            return generatedLoot;
        }


        Player player = context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);
        if(player != null && !DragonStateProvider.isDragon(player)){
            return generatedLoot;
        }

        float health = ((LivingEntity)entity).getMaxHealth();

        boolean canDropDragonHeart =
                (ServerConfig.dragonHeartEntityList.contains(ResourceHelper.getKey(entity).toString()) == ServerConfig.dragonHeartWhiteList && ServerConfig.dragonHeartUseList)
                || health >= 14 && health < 20;
        boolean canDropWeakDragonHeart =
                (ServerConfig.weakDragonHeartEntityList.contains(ResourceHelper.getKey(entity).toString()) == ServerConfig.weakDragonHeartWhiteList && ServerConfig.weakDragonHeartUseList)
                || health >= 20 && health < 50;
        boolean canDropElderDragonHeart =
                (ServerConfig.elderDragonHeartEntityList.contains(ResourceHelper.getKey(entity).toString()) == ServerConfig.elderDragonHeartWhiteList && ServerConfig.elderDragonHeartUseList)
                || health >= 50;

        //FIXME: Is context.getLuck() right?
        if(canDropDragonHeart){
            if(context.getRandom().nextInt(100) <= ServerConfig.dragonHeartShardChance * 100 + context.getLuck() * (ServerConfig.dragonHeartShardChance * 100 / 4)){
                generatedLoot.add(new ItemStack(DSItems.DRAGON_HEART_SHARD));
            }
        }

        if(canDropWeakDragonHeart){
            if(context.getRandom().nextInt(100) <= ServerConfig.weakDragonHeartChance * 100 + context.getLuck() * (ServerConfig.weakDragonHeartChance * 100 / 4)){
                generatedLoot.add(new ItemStack(DSItems.WEAK_DRAGON_HEART));
            }
        }

        if(canDropElderDragonHeart){
            if(context.getRandom().nextInt(100) <= ServerConfig.elderDragonHeartChance * 100 + context.getLuck() * (ServerConfig.elderDragonHeartChance * 100 / 4)){
                generatedLoot.add(new ItemStack(DSItems.ELDER_DRAGON_HEART));
            }
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
