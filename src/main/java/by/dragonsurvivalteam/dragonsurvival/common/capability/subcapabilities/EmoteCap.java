package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.client.emotes.Emote;
import by.dragonsurvivalteam.dragonsurvival.client.emotes.EmoteRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;

public class EmoteCap extends SubCap{
	public int emoteTick;
	public boolean emoteMenuOpen = false;
	private Emote currentEmote;

	public EmoteCap(DragonStateHandler handler){
		super(handler);
	}

	@Override
	public CompoundNBT writeNBT(){
		CompoundNBT tag = new CompoundNBT();

		tag.putString("emote", getCurrentEmote() != null ? getCurrentEmote().animation : "nil");
		tag.putBoolean("emoteOpen", emoteMenuOpen);
		tag.putInt("emoteTick", emoteTick);

		return tag;
	}

	public Emote getCurrentEmote(){
		return currentEmote;
	}

	@Override
	public void readNBT(CompoundNBT tag){
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
		emoteTick = tag.getInt("emoteTick");
	}

	public void setCurrentEmote(Emote currentEmote){
		this.currentEmote = currentEmote;
		emoteTick = 0;
	}
}