//package by.dragonsurvivalteam.dragonsurvival.common.dragon_types;
//
//import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
//import by.dragonsurvivalteam.dragonsurvival.common.capability.NBTInterface;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.tags.TagKey;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.block.Block;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Locale;
//
//public abstract class AbstractDragonType implements NBTInterface, Comparable<AbstractDragonType> {
//    /** Determines which claw tool slot affects the claw texture */
//    public int clawTextureSlot;
//
//    @Override
//    public int compareTo(@NotNull AbstractDragonType type) {
//        return getTypeName().compareTo(type.getTypeName());
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        return super.equals(object) || object instanceof AbstractDragonType type && type.getSubtypeName().equals(getSubtypeName());
//    }
//
//    @Override
//    public String toString() {
//        return getTypeName();
//    }
//
//    public String getSubtypeName() {
//        return getTypeName();
//    }
//
//    public String getSubtypeNameLowerCase() {
//        return getSubtypeName().toLowerCase(Locale.ENGLISH);
//    }
//
//    public String getTypeNameLowerCase() {
//        return getTypeName().toLowerCase(Locale.ENGLISH);
//    }
//
//    public abstract String getTypeName();
//
//    public abstract void onPlayerUpdate(Player player, DragonStateHandler handler);
//    public abstract void onPlayerDeath();
//
//    public abstract TagKey<Block> harvestableBlocks();
//    public abstract boolean isInManaCondition(Player player);
//
//    public abstract ResourceLocation getFoodIcons();
//    public abstract ResourceLocation getManaIcons();
//    public abstract Component translatableName();
//}