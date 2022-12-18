package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.items.food.DragonFoodItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChargedCoalItem extends DragonFoodItem{
	public ChargedCoalItem(Properties p_i48487_1_){
		super(p_i48487_1_);
	}
	
	public ChargedCoalItem(Properties p_i48487_1_, Supplier<MobEffectInstance>... effectInstances)
	{
		super(p_i48487_1_, effectInstances);
	}
	
	public ChargedCoalItem(Properties p_i48487_1_, AbstractDragonType dragonType, Supplier<MobEffectInstance>... effectInstances)
	{
		super(p_i48487_1_, dragonType, effectInstances);
	}
	
	public ChargedCoalItem(Properties p_i48487_1_, AbstractDragonType dragonType, Consumer<LivingEntity> onEat, Supplier<MobEffectInstance>... effectInstances)
	{
		super(p_i48487_1_, dragonType, onEat, effectInstances);
	}
	
	public ChargedCoalItem(Properties p_i48487_1_, AbstractDragonType dragonType, Consumer<LivingEntity> onEat)
	{
		super(p_i48487_1_, dragonType, onEat);
	}
	
	
	@Override
	public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType){
		return 4000;
	}

	@Override
	public void appendHoverText(ItemStack p_77624_1_,
		@Nullable
			Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_){

		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslatableComponent("ds.description.chargedCoal"));
	}
}