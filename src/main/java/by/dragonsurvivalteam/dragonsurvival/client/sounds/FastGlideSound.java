package by.dragonsurvivalteam.dragonsurvival.client.sounds;

import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.util.Mth;


public class FastGlideSound extends ElytraOnPlayerSoundInstance{
	private final LocalPlayer player;
	private int time;

	public FastGlideSound(LocalPlayer p_i47113_1_){
		super(p_i47113_1_);
		this.player = p_i47113_1_;
		this.looping = true;
		this.delay = 0;
		this.volume = 0.1F;
	}

	public void tick(){
		++this.time;
		if(!this.player.isRemoved() && (this.time <= 20 || ServerFlightHandler.isGliding(player))){
			this.x = (float)this.player.getX();
			this.y = (float)this.player.getY();
			this.z = (float)this.player.getZ();
			float f = (float)this.player.getDeltaMovement().lengthSqr();
			if((double)f >= 1.0E-7D){
				this.volume = Mth.clamp(f / 4.0F, 0.0F, 1.0F);
			}else{
				this.volume = 0.0F;
			}

			if(this.time < 20){
				this.volume = 0.0F;
			}else if(this.time < 40){
				this.volume = (float)((double)this.volume * ((double)(this.time - 20) / 20.0D));
			}

			float f1 = 0.8F;
			if(this.volume > 0.8F){
				this.pitch = 1.0F + (this.volume - 0.8F);
			}else{
				this.pitch = 1.0F;
			}
		}else{
			this.stop();
		}
	}
}