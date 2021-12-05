package by.jackraidenph.dragonsurvival.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.emotes.Emote;
import by.jackraidenph.dragonsurvival.emotes.EmoteRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashMap;
import java.util.Objects;

public class EmoteCap implements DragonCapability
{
	private Emote currentEmote;
	public int emoteTick;
	
	public HashMap<String, Integer> emoteUsage = new HashMap<>();
	public boolean emoteMenuOpen = false;
	
	public Emote getCurrentEmote()
	{
		return currentEmote;
	}
	
	public void setCurrentEmote(Emote currentEmote)
	{
		this.currentEmote = currentEmote;
		emoteTick = 0;
	}
	
	@Override
	public INBT writeNBT(Capability<DragonStateHandler> capability, DragonStateHandler instance, Direction side)
	{
		CompoundNBT tag = new CompoundNBT();
		
		tag.putString("emote", instance.getEmotes().getCurrentEmote() != null ? instance.getEmotes().getCurrentEmote().animation : "nil");
		tag.putBoolean("emoteOpen", instance.getEmotes().emoteMenuOpen);
		
		CompoundNBT emoteUsage = new CompoundNBT();
		
		for(Emote emote : EmoteRegistry.EMOTES){
			if(instance.getEmotes().emoteUsage.containsKey(emote.name)){
				emoteUsage.putInt(emote.name, instance.getEmotes().emoteUsage.get(emote.name));
			}
		}
		tag.put("emoteUsage", emoteUsage);
		
		return tag;
	}
	
	@Override
	public void readNBT(Capability<DragonStateHandler> capability, DragonStateHandler instance, Direction side, INBT base)
	{
		CompoundNBT tag = (CompoundNBT) base;
		
		String emoteId = tag.getString("emote");
		
		if(!emoteId.equals("nil")){
			for(Emote emote : EmoteRegistry.EMOTES){
				if(Objects.equals(emote.animation, emoteId)){
					instance.getEmotes().setCurrentEmote(emote);
					break;
				}
			}
		}
		
		instance.getEmotes().emoteMenuOpen = tag.getBoolean("emoteOpen");
		
		CompoundNBT emoteUsage = tag.getCompound("emoteUsage");
		
		for(Emote emote : EmoteRegistry.EMOTES){
			if(emoteUsage.contains(emote.name)){
				instance.getEmotes().emoteUsage.put(emote.name, tag.getInt(emote.name));
			}
		}
	}
	
	@Override
	public void clone(DragonStateHandler oldCap)
	{
		emoteMenuOpen = oldCap.getEmotes().emoteMenuOpen;
		emoteUsage = oldCap.getEmotes().emoteUsage;
	}
}
