package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.ik;

import java.util.Objects;

public record GeoIkChainDefinition(String anchorBoneName, String controlledRootBoneName) {
    public GeoIkChainDefinition {
        Objects.requireNonNull(anchorBoneName, "anchorBoneName");
        Objects.requireNonNull(controlledRootBoneName, "controlledRootBoneName");

        if (anchorBoneName.isBlank()) {
            throw new IllegalArgumentException("anchorBoneName cannot be blank");
        }

        if (controlledRootBoneName.isBlank()) {
            throw new IllegalArgumentException("controlledRootBoneName cannot be blank");
        }
    }
}
