package by.dragonsurvivalteam.dragonsurvival.common.items.food;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DragonFoodItem extends Item {
    private Supplier<MobEffectInstance>[] effects;
    private AbstractDragonType dragonType;
    private Consumer<LivingEntity> onEat;

    public DragonFoodItem(Properties properties) {
        super(properties.food(genFoodProperties(null, (Supplier<MobEffectInstance>) null)));
    }

    public DragonFoodItem(Properties properties, Supplier<MobEffectInstance>... effectInstances) {
        super(properties.food(genFoodProperties(null, effectInstances)));
        effects = effectInstances;
    }


    public DragonFoodItem(Properties properties, AbstractDragonType dragonType, Supplier<MobEffectInstance>... effectInstances) {
        super(properties.food(genFoodProperties(dragonType, effectInstances)));
        this.dragonType = dragonType;
        effects = effectInstances;
    }

    public DragonFoodItem(Properties properties, AbstractDragonType dragonType, Consumer<LivingEntity> onEat, Supplier<MobEffectInstance>... effectInstances) {
        super(properties.food(genFoodProperties(dragonType, effectInstances)));
        this.dragonType = dragonType;
        effects = effectInstances;
        this.onEat = onEat;
    }

    public DragonFoodItem(Properties properties, AbstractDragonType dragonType, Consumer<LivingEntity> onEat) {
        super(properties.food(genFoodProperties(dragonType, (Supplier<MobEffectInstance>) null)));
        this.dragonType = dragonType;
        this.onEat = onEat;
    }

    @NotNull private static FoodProperties genFoodProperties(AbstractDragonType dragonType, Supplier<MobEffectInstance>... effectInstances) {
        Builder builder = new Builder();
        builder.nutrition(1);
        builder.saturationModifier(0.4F);
        builder.effect(() -> new MobEffectInstance(MobEffects.HUNGER, 20 * 15, 0), 1.0F);

        if (effectInstances != null) {
            builder.alwaysEdible();
        }

        return builder.build();
    }


    @Override
    public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        String langKey = "ds.description." + ResourceHelper.getKey(this).getPath();
        if (I18n.exists(langKey)) {
            pTooltipComponents.add(Component.translatable(langKey));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pLivingEntity);

        if (onEat != null) {
            if (DragonStateProvider.isDragon(pLivingEntity) && (dragonType == null || DragonUtils.isDragonType(pLivingEntity, dragonType))) {
                onEat.accept(pLivingEntity);
            }
        }
        if (effects != null && effects.length > 0) {
            if (DragonStateProvider.isDragon(pLivingEntity) && (dragonType == null || DragonUtils.isDragonType(pLivingEntity, dragonType))) {
                for (Supplier<MobEffectInstance> effect : effects) {
                    pLivingEntity.addEffect(effect.get());
                }
            }
        }

        return stack;
    }

}