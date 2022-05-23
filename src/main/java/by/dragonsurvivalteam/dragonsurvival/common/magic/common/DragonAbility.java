package by.dragonsurvivalteam.dragonsurvival.common.magic.common;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
	public Player player;
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

	public Player getPlayer(){
		return player;
	}

	public abstract DragonAbility createInstance();

	@OnlyIn( Dist.CLIENT )
	public Component getTitle(){
		return new TranslatableComponent("ds.skill." + getId());
	}

	public String getId(){return id;}

	@OnlyIn( Dist.CLIENT )
	public Component getDescription(){
		return new TranslatableComponent("ds.skill.description." + getId());
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getInfo(){return new ArrayList<>();}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){return new ArrayList<>();}

	public void onKeyPressed(Player player){}

	public CompoundTag saveNBT(){
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("level", level);
		return nbt;
	}

	public void loadNBT(CompoundTag nbt){
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

	public void setLevel(int level){
		this.level = Math.min(getMaxLevel(), Math.max(getMinLevel(), level));
	}

	public boolean isDisabled(){
		if(!ServerConfig.dragonAbilities){
			return true;
		}
		if(type == DragonType.CAVE && !ServerConfig.caveDragonAbilities){
			return true;
		}
		if(type == DragonType.SEA && !ServerConfig.seaDragonAbilities){
			return true;
		}
		return type == DragonType.FOREST && !ServerConfig.forestDragonAbilities;
	}

	public int getMaxLevel(){
		return maxLevel;
	}

	public int getMinLevel(){
		return minLevel;
	}
}