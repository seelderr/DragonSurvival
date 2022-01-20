package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.client.emotes.Emote;
import by.jackraidenph.dragonsurvival.client.emotes.EmoteRegistry;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Objects;

public class EmoteCap implements DragonCapability
{
	private Emote currentEmote;
	public int emoteTick;
	
	public String serverEmote;
	public int serverTick;
	
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
	public Tag writeNBT(Capability<DragonStateHandler> capability, Direction side)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString("emote", getCurrentEmote() != null ? getCurrentEmote().animation : "nil");
		tag.putBoolean("emoteOpen", emoteMenuOpen);
		
		return tag;
	}
	
	@Override
	public void readNBT(Capability<DragonStateHandler> capability, Direction side, Tag base)
	{
		CompoundTag tag = (CompoundTag) base;
		
		String emoteId = tag.getString("emote");
		
		if(!emoteId.equals("nil")){
			for(Emote emote : EmoteRegistry.EMOTES){
				if(Objects.equals(emote.animation, emoteId)){
					setCurrentEmote(emote);
					break;
				}
			}
		}
		
		emoteMenuOpen = tag.getBoolean("emoteOpen");
	}
	
	@Override
	public void clone(DragonStateHandler oldCap)
	{
		emoteMenuOpen = oldCap.getEmotes().emoteMenuOpen;
		
		currentEmote = oldCap.getEmotes().getCurrentEmote();
		emoteTick = oldCap.getEmotes().emoteTick;
		
		serverEmote = oldCap.getEmotes().serverEmote;
		serverTick = oldCap.getEmotes().serverTick;
	}
}
