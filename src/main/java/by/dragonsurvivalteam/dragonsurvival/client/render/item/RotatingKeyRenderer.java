package by.dragonsurvivalteam.dragonsurvival.client.render.item;

import by.dragonsurvivalteam.dragonsurvival.common.items.RotatingKeyItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RotatingKeyRenderer extends GeoItemRenderer<RotatingKeyItem> {
    public RotatingKeyRenderer() {
        super(new RotatingKeyModel());
    }
}