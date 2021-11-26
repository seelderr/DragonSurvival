package by.jackraidenph.dragonsurvival.magic.entity.particle.ForestDragon;

import by.jackraidenph.dragonsurvival.registration.ParticleRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public class ForestParticleData implements IParticleData
{
	public static final IDeserializer<ForestParticleData> DESERIALIZER = new IDeserializer<ForestParticleData>()
	{
		public ForestParticleData fromCommand(ParticleType<ForestParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
		{
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			reader.expect(' ');
			boolean swirls = reader.readBoolean();
			return new ForestParticleData(duration, swirls);
		}
		
		public ForestParticleData fromNetwork(ParticleType<ForestParticleData> particleTypeIn, PacketBuffer buffer)
		{
			return new ForestParticleData(buffer.readFloat(), buffer.readBoolean());
		}
	};
	
	private final float duration;
	private final boolean swirls;
	
	public ForestParticleData(float duration, boolean spins)
	{
		this.duration = duration;
		this.swirls = spins;
	}
	
	@Override
	public void writeToNetwork(PacketBuffer buffer)
	{
		buffer.writeFloat(this.duration);
		buffer.writeBoolean(this.swirls);
	}
	
	@SuppressWarnings( "deprecation" )
	@Override
	public String writeToString()
	{
		return String.format(Locale.ROOT, "%s %.2f %b", Registry.PARTICLE_TYPE.getKey(this.getType()), this.duration, this.swirls);
	}
	
	@Override
	public ParticleType<ForestParticleData> getType()
	{
		return ParticleRegistry.FOREST.get();
	}
	
	@OnlyIn( Dist.CLIENT )
	public float getDuration()
	{
		return this.duration;
	}
	
	@OnlyIn( Dist.CLIENT )
	public boolean getSwirls()
	{
		return this.swirls;
	}
	
	public static Codec<ForestParticleData> CODEC(ParticleType<ForestParticleData> particleType)
	{
		return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(ForestParticleData::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(ForestParticleData::getSwirls)).apply(codecBuilder, ForestParticleData::new));
	}
}
