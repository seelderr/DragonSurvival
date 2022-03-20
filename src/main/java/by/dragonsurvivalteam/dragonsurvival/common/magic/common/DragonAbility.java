package by.dragonsurvivalteam.dragonsurvival.common.magic.common;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class DragonAbility{
	protected final String id, icon;
	protected final int maxLevel;
	protected final int minLevel;
	protected static NumberFormat nf = NumberFormat.getInstance();
	protected static HashMap<String, ResourceLocation> iconCache = new HashMap<>();
	public PlayerEntity player;
	protected int level;
	protected DragonType type;

	public DragonAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		nf.setMaximumFractionDigits(1);

		this.id = abilityId;
		this.icon = icon;
		this.maxLevel = maxLevel;
		this.minLevel = minLevel;
		this.level = minLevel;
		this.type = type;
	}

	public PlayerEntity getPlayer(){
		return player;
	}

	public abstract DragonAbility createInstance();

	@OnlyIn( Dist.CLIENT )
	public IFormattableTextComponent getTitle(){
		return new TranslationTextComponent("ds.skill." + getId());
	}

	public String getId(){return id;}

	@OnlyIn( Dist.CLIENT )
	public IFormattableTextComponent getDescription(){
		return new TranslationTextComponent("ds.skill.description." + getId());
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getInfo(){return new ArrayList<>();}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){return new ArrayList<>();}

	public void setLevel(int level){
		this.level = Math.min(getMaxLevel(), Math.max(getMinLevel(), level));
	}

	public int getMaxLevel(){
		return maxLevel;
	}

	public int getMinLevel(){
		return minLevel;
	}

	public void onKeyPressed(PlayerEntity player){}

	public CompoundNBT saveNBT(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("level", level);
		return nbt;
	}

	public void loadNBT(CompoundNBT nbt){
		level = nbt.getInt("level");
	}

	@Override
	public int hashCode(){
		return Objects.hash(getLevel(), getId(), getIcon(), getMaxLevel(), getMinLevel());
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof DragonAbility)){
			return false;
		}
		DragonAbility ability = (DragonAbility)o;
		return getLevel() == ability.getLevel() && getMaxLevel() == ability.getMaxLevel() && getMinLevel() == ability.getMinLevel() && getId().equals(ability.getId()) && getIcon().equals(ability.getIcon());
	}

	@OnlyIn( Dist.CLIENT )
	public ResourceLocation getIcon(){
		if(!iconCache.containsKey(getLevel() + "_" + getId())){
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/" + icon + "_" + getLevel() + ".png");
			iconCache.put(getLevel() + "_" + getId(), texture);
		}

		return iconCache.get(getLevel() + "_" + getId());
	}

	public int getLevel(){
		if(isDisabled()){
			return 0;
		}
		return this.level;
	}

	public boolean isDisabled(){
		if(!ConfigHandler.SERVER.dragonAbilities.get()){
			return true;
		}
		if(type == DragonType.CAVE && !ConfigHandler.SERVER.caveDragonAbilities.get()){
			return true;
		}
		if(type == DragonType.SEA && !ConfigHandler.SERVER.seaDragonAbilities.get()){
			return true;
		}
		return type == DragonType.FOREST && !ConfigHandler.SERVER.forestDragonAbilities.get();
	}
}