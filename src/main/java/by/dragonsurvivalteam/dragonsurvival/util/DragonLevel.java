package by.dragonsurvivalteam.dragonsurvival.util;


import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import com.google.gson.annotations.SerializedName;
import net.minecraft.network.chat.Component;

public enum DragonLevel {
    @SerializedName(value = "NEWBORN", alternate = "BABY")
    @Translation(key = "level.newborn", type = Translation.Type.DESCRIPTION, comments = "Newborn")
    NEWBORN(14, 20, 1.1f, "newborn"),
    @Translation(key = "level.young", type = Translation.Type.DESCRIPTION, comments = "Young")
    YOUNG(20, 30, 1.6f, "young"),
    @Translation(key = "level.adult", type = Translation.Type.DESCRIPTION, comments = "Adult")
    ADULT(30, 40, 2.1f, "adult");

    public final int size, maxSize;
    public final float jumpHeight;
    public final String name;

    DragonLevel(int size, int maxSize, float jumpHeight, String name) {
        this.size = size;
        this.maxSize = maxSize;
        this.jumpHeight = jumpHeight;
        this.name = name;
    }

    public Component translatableName() {
        return Component.translatable(Translation.Type.DESCRIPTION.wrap("level." + name));
    }

    public String getRawName() {
        return name;
    }
}