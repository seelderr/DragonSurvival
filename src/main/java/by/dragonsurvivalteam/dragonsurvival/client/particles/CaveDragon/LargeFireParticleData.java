package by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon;

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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public class LargeFireParticleData implements ParticleOptions{
	public static final Deserializer<LargeFireParticleData> DESERIALIZER = new Deserializer<LargeFireParticleData>(){
		@Override
		public LargeFireParticleData fromCommand(ParticleType<LargeFireParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException{
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			reader.expect(' ');
			boolean swirls = reader.readBoolean();
			return new LargeFireParticleData(duration, swirls);
		}

		@Override
		public LargeFireParticleData fromNetwork(ParticleType<LargeFireParticleData> particleTypeIn, FriendlyByteBuf buffer){
			return new LargeFireParticleData(buffer.readFloat(), buffer.readBoolean());
		}
	};

	private final float duration;
	private final boolean swirls;

	public static Codec<LargeFireParticleData> CODEC(ParticleType<LargeFireParticleData> particleType){
		return RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(LargeFireParticleData::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(LargeFireParticleData::getSwirls)).apply(codecBuilder, LargeFireParticleData::new));
	}

	public LargeFireParticleData(float duration, boolean spins){
		this.duration = duration;
		swirls = spins;
	}

	@OnlyIn( Dist.CLIENT )
	public float getDuration(){
		return duration;
	}

	@OnlyIn( Dist.CLIENT )
	public boolean getSwirls(){
		return swirls;
	}	@Override
	public void writeToNetwork(FriendlyByteBuf buffer){
		buffer.writeFloat(duration);
		buffer.writeBoolean(swirls);
	}



	@SuppressWarnings( "deprecation" )
	@Override
	public String writeToString(){
		return String.format(Locale.ROOT, "%s %.2f %b", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), duration, swirls);
	}


	@Override
	public ParticleType<LargeFireParticleData> getType(){
		return DSParticles.LARGE_FIRE.get();
	}
}