package by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon;

import by.dragonsurvivalteam.dragonsurvival.client.particles.DSParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public class SmallLightningParticleData implements ParticleOptions{
	public static final Deserializer<SmallLightningParticleData> DESERIALIZER = new Deserializer<SmallLightningParticleData>(){
		public SmallLightningParticleData fromCommand(ParticleType<SmallLightningParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException{
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			reader.expect(' ');
			boolean swirls = reader.readBoolean();
			return new SmallLightningParticleData(duration, swirls);
		}

		public SmallLightningParticleData fromNetwork(ParticleType<SmallLightningParticleData> particleTypeIn, FriendlyByteBuf buffer){
			return new SmallLightningParticleData(buffer.readFloat(), buffer.readBoolean());
		}
	};

	private final float duration;
	private final boolean swirls;

	public static Codec<SmallLightningParticleData> CODEC(ParticleType<SmallLightningParticleData> particleType){
		return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(SmallLightningParticleData::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(SmallLightningParticleData::getSwirls)).apply(codecBuilder, SmallLightningParticleData::new));
	}

	public SmallLightningParticleData(float duration, boolean spins){
		this.duration = duration;
		this.swirls = spins;
	}

	@OnlyIn( Dist.CLIENT )
	public float getDuration(){
		return this.duration;
	}	@Override
	public void writeToNetwork(FriendlyByteBuf buffer){
		buffer.writeFloat(this.duration);
		buffer.writeBoolean(this.swirls);
	}

	@OnlyIn( Dist.CLIENT )
	public boolean getSwirls(){
		return this.swirls;
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public String writeToString(){
		return String.format(Locale.ROOT, "%s %.2f %b", Registry.PARTICLE_TYPE.getKey(this.getType()), this.duration, this.swirls);
	}



	@Override
	public ParticleType<SmallLightningParticleData> getType(){
		return DSParticles.LIGHTNING.get();
	}
}