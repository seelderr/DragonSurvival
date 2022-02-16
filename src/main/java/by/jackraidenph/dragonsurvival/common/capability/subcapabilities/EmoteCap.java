package by.jackraidenph.dragonsurvival.common.capability.subcapabilities;

import by.jackraidenph.dragonsurvival.client.emotes.Emote;
import by.jackraidenph.dragonsurvival.client.emotes.EmoteRegistry;
import by.jackraidenph.dragonsurvival.common.capability.NBTInterface;
import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;

public class EmoteCap implements NBTInterface
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
	public CompoundNBT writeNBT()
	{
		CompoundNBT tag = new CompoundNBT();
		
		tag.putString("emote", getCurrentEmote() != null ? getCurrentEmote().animation : "nil");
		tag.putBoolean("emoteOpen", emoteMenuOpen);
		
		return tag;
	}
	
	@Override
	public void readNBT(CompoundNBT tag)
	{
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
