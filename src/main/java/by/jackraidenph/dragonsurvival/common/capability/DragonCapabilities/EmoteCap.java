package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.client.emotes.Emote;
import by.jackraidenph.dragonsurvival.client.emotes.EmoteRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

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
	public Tag writeNBT()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString("emote", getCurrentEmote() != null ? getCurrentEmote().animation : "nil");
		tag.putBoolean("emoteOpen", emoteMenuOpen);
		
		return tag;
	}
	
	@Override
	public void readNBT(Tag base)
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
	
}
