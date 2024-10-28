package by.dragonsurvivalteam.dragonsurvival.config.obj;

public enum Validation { // TODO :: find a better way to do this
    /** Checks for {@link net.minecraft.resources.ResourceLocation}*/
    RESOURCE_LOCATION,
    /** Checks for {@link net.minecraft.resources.ResourceLocation} and 1 {@link Integer}*/
    RESOURCE_LOCATION_NUMBER,
    /** Checks for {@link net.minecraft.resources.ResourceLocation} and 1 optional {@link Integer}*/
    RESOURCE_LOCATION_OPTIONAL_NUMBER,
    /** Checks for {@link net.minecraft.resources.ResourceLocation}, 1 optional {@link Integer} and 1 optional {@link Double}*/
    RESOURCE_LOCATION_2_OPTIONAL_NUMBERS,
    /** Default check */
    DEFAULT
}
