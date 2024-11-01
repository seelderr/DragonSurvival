package by.dragonsurvivalteam.dragonsurvival.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public enum Keybind {
    // Implementation inspired by Create

    TOGGLE_WINGS("ds.keybind.wings", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_G),
    DRAGON_INVENTORY("ds.keybind.dragon_inv", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_UNKNOWN),

    USE_ABILITY("ds.keybind.use_ability", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_C),
    TOGGLE_ABILITIES("ds.keybind.toggle_abilities", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_X),

    NEXT_ABILITY("ds.keybind.next_ability", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_R),
    PREV_ABILITY("ds.keybind.prev_ability", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_F),

    ABILITY1("ds.keybind.ability1", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_KP_1),
    ABILITY2("ds.keybind.ability2", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_KP_2),
    ABILITY3("ds.keybind.ability3", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_KP_3),
    ABILITY4("ds.keybind.ability4", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_KP_4),

    SPIN_ABILITY("ds.keybind.spin", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_V),
    FREE_LOOK("ds.keybind.free_look", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_LEFT_ALT),
    DISABLE_DESTRUCTION("ds.keybind.toggle_destruction", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_RIGHT_ALT);

    public static final int KEY_RELEASED = 0;
    public static final int KEY_PRESSED = 1;
    public static final int KEY_HELD = 2;

    private final Lazy<KeyMapping> keyMapping;

    Keybind(String description, IKeyConflictContext keyConflictContext, int defaultKey) {
        this(description, keyConflictContext, defaultKey, "ds.keybind.category");
    }

    Keybind(String description, IKeyConflictContext keyConflictContext, int defaultKey, String category) {
        keyMapping = Lazy.of(() -> new KeyMapping(description, keyConflictContext, InputConstants.Type.KEYSYM, defaultKey, category));
    }

    @SubscribeEvent
    public static void registerAllKeys(RegisterKeyMappingsEvent event) {
        for (Keybind keybind : values()) {
            event.register(keybind.get());
        }
    }

    public KeyMapping get() {
        return keyMapping.get();
    }

    /**
     * Mirror for {@link KeyMapping#consumeClick()}
     * Tries to consume a click triggered by {@link KeyMapping#click(InputConstants.Key)}.
     *
     * @return True if a click was consumed. False if the key has no clicks to consume.
     */
    public boolean consumeClick() {
        return get().consumeClick();
    }

    /**
     * Mirror for {@link KeyMapping#isDown()}
     *
     * @return True if the key is down (in the current KeyConflictContext).
     */
    public boolean isDown() {
        return get().isDown();
    }

    /**
     * Mirror for {@link KeyMapping#getKey()}
     *
     * @return Key for this KeyMapping.
     */
    public InputConstants.Key getKey() {
        return get().getKey();
    }

    /** Checks if the supplied key code (see {@link InputConstants}) matches the key */
    public boolean isKey(int keyCode) {
        return getKey().getValue() == keyCode;
    }
}
