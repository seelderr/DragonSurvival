package by.dragonsurvivalteam.dragonsurvival.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public class TreasureParticleData implements ParticleOptions{
	public static final Codec<TreasureParticleData> CODEC = RecordCodecBuilder.create(p_239803_0_ -> {
		return p_239803_0_.group(Codec.FLOAT.fieldOf("r").forGetter(p_239807_0_ -> {
			return p_239807_0_.r;
		}), Codec.FLOAT.fieldOf("g").forGetter(p_239806_0_ -> {
			return p_239806_0_.g;
		}), Codec.FLOAT.fieldOf("b").forGetter(p_239805_0_ -> {
			return p_239805_0_.b;
		}), Codec.FLOAT.fieldOf("scale").forGetter(p_239804_0_ -> {
			return p_239804_0_.scale;
		})).apply(p_239803_0_, TreasureParticleData::new);
	});
	public static final ParticleOptions.Deserializer<TreasureParticleData> DESERIALIZER = new ParticleOptions.Deserializer<TreasureParticleData>(){
		@Override
		public TreasureParticleData fromCommand(ParticleType<TreasureParticleData> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException{
			p_197544_2_.expect(' ');
			float f = (float)p_197544_2_.readDouble();
			p_197544_2_.expect(' ');
			float f1 = (float)p_197544_2_.readDouble();
			p_197544_2_.expect(' ');
			float f2 = (float)p_197544_2_.readDouble();
			p_197544_2_.expect(' ');
			float f3 = (float)p_197544_2_.readDouble();
			return new TreasureParticleData(f, f1, f2, f3);
		}

		@Override
		public TreasureParticleData fromNetwork(ParticleType<TreasureParticleData> p_197543_1_, FriendlyByteBuf p_197543_2_){
			return new TreasureParticleData(p_197543_2_.readFloat(), p_197543_2_.readFloat(), p_197543_2_.readFloat(), p_197543_2_.readFloat());
		}
	};
	private final float r;
	private final float g;
	private final float b;
	private final float scale;

	public TreasureParticleData(float p_i47950_1_, float p_i47950_2_, float p_i47950_3_, float p_i47950_4_){
		r = p_i47950_1_;
		g = p_i47950_2_;
		b = p_i47950_3_;
		scale = Mth.clamp(p_i47950_4_, 0.01F, 4.0F);
	}

	@OnlyIn( Dist.CLIENT )
	public float getR(){
		return r;
	}

	@OnlyIn( Dist.CLIENT )
	public float getG(){
		return g;
	}

	@OnlyIn( Dist.CLIENT )
	public float getB(){
		return b;
	}

	@OnlyIn( Dist.CLIENT )
	public float getScale(){
		return scale;
	}	@Override
	public void writeToNetwork(FriendlyByteBuf p_197553_1_){
		p_197553_1_.writeFloat(r);
		p_197553_1_.writeFloat(g);
		p_197553_1_.writeFloat(b);
		p_197553_1_.writeFloat(scale);
	}




	@Override
	public String writeToString(){
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), r, g, b, scale);
	}

	@Override
	public ParticleType<TreasureParticleData> getType(){
		return DSParticles.TREASURE.get();
	}
}