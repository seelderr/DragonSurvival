package by.dragonsurvivalteam.dragonsurvival.client.skins;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DragonSkins {
    protected static boolean initialized = false;
    public static NetSkinLoader skinLoader = new GithubSkinLoaderAPI();
    private static final ArrayList<String> hasFailedFetch = new ArrayList<>();
    public static HashMap<ResourceKey<DragonStage>, HashMap<String, SkinObject>> SKIN_USERS = new HashMap<>();
    public static HashMap<String, ResourceLocation> playerSkinCache = new HashMap<>();
    public static HashMap<String, ResourceLocation> playerGlowCache = new HashMap<>();
    private static double lastSkinFetchAttemptTime = 0;
    private static int numSkinFetchAttempts = 0;

    public static ResourceLocation getPlayerSkin(String playerName, ResourceKey<DragonStage> dragonStage) {
        String skinKey = playerName + "_" + dragonStage.location().getPath();

        if (playerSkinCache.containsKey(skinKey) && playerSkinCache.get(skinKey) != null) {
            return playerSkinCache.get(skinKey);
        }

        if (!hasFailedFetch.contains(skinKey) && !playerSkinCache.containsKey(skinKey)) {
            ResourceLocation texture = fetchSkinFile(playerName, dragonStage);

            if (texture != null) {
                playerSkinCache.put(skinKey, texture);
                return texture;
            }
        }

        return null;
    }

    public static ResourceLocation getPlayerGlow(String playerName, ResourceKey<DragonStage> dragonStage) {
        String skinKey = playerName + "_" + dragonStage.location().getPath();

        if (playerGlowCache.containsKey(skinKey)) {
            return playerGlowCache.get(skinKey);
        } else {
            ResourceLocation texture = fetchSkinFile(playerName, dragonStage, "glow");
            playerGlowCache.put(skinKey, texture);
            return texture;
        }
    }


    public static ResourceLocation getPlayerSkin(Player player, ResourceKey<DragonStage> dragonStage) {
        ResourceLocation texture = null;
        String playerKey = player.getGameProfile().getName() + "_" + dragonStage.location().getPath();
        boolean renderCustomSkin = DragonStateProvider.getData(player).getSkinData().renderCustomSkin;

        if ((ClientDragonRenderer.renderOtherPlayerSkins || player == DragonSurvival.PROXY.getLocalPlayer()) && renderCustomSkin) {
            if (playerSkinCache.containsKey(playerKey) && playerSkinCache.get(playerKey) != null) {
                return playerSkinCache.get(playerKey);
            }

            if (!hasFailedFetch.contains(playerKey) && !playerSkinCache.containsKey(playerKey)) {
                texture = fetchSkinFile(player, dragonStage);

                if (texture != null) {
                    playerSkinCache.put(playerKey, texture);
                }
            }
        }

        return texture;
    }

    public static ResourceLocation fetchSkinFile(final String playerName, final ResourceKey<DragonStage> level, final String... extra) {
        String playerKey = playerName + "_" + level.location().getPath();
        String[] text = ArrayUtils.addAll(new String[]{playerKey}, extra);

        String resourceName = StringUtils.join(text, "_");
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, resourceName.toLowerCase(Locale.ENGLISH));

        try (SimpleTexture simpleTexture = new SimpleTexture(resourceLocation)) {
            if (Minecraft.getInstance().getTextureManager().getTexture(resourceLocation, simpleTexture) != simpleTexture) {
                return resourceLocation;
            }
        }

        HashMap<String, SkinObject> playerSkinMap = SKIN_USERS.getOrDefault(level, null);

        // Wait an increasing amount of time depending on the number of failed attempts
        if (playerSkinMap == null && lastSkinFetchAttemptTime + numSkinFetchAttempts < Blaze3D.getTime() && numSkinFetchAttempts < 10) {
            DragonSurvival.LOGGER.warn("Customs skins are not yet fetched, re-fetching...");
            init();

            numSkinFetchAttempts++;
            lastSkinFetchAttemptTime = Blaze3D.getTime();

            playerSkinMap = SKIN_USERS.getOrDefault(level, null);

            if (playerSkinMap == null) {
                DragonSurvival.LOGGER.error("Custom skins could not be fetched");
            }
        }

        String skinName = StringUtils.join(ArrayUtils.addAll(new String[]{playerName}, extra), "_");
        SkinObject skin = null;

        if (playerSkinMap != null) {
            skin = playerSkinMap.getOrDefault(skinName, null);
        }

        // Only use the API to get the names (for the random button)
        if (skinLoader instanceof GithubSkinLoader gitHubOld) {
            try (InputStream imageStream = gitHubOld.querySkinImage(skinName, level)) {
                return readSkin(imageStream, resourceLocation);
            } catch (IOException exception) {
                boolean isNormalSkin = extra == null || extra.length == 0;
                handleSkinFetchError(playerKey, isNormalSkin);
                return null;
            }
        }

        if (skin == null) {
            return null;
        }

        try (InputStream imageStream = skinLoader.querySkinImage(skin)) {
            return readSkin(imageStream, resourceLocation);
        } catch (IOException exception) {
            boolean isNormalSkin = extra == null || extra.length == 0;
            handleSkinFetchError(playerKey, isNormalSkin);
            return null;
        }
    }

    private static ResourceLocation readSkin(final InputStream imageStream, final ResourceLocation location) throws IOException {
        if (imageStream == null) {
            throw new IOException("Skin was not successfully fetched for [" + location + "]");
        }

        NativeImage customTexture = NativeImage.read(imageStream);
        Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(customTexture));
        return location;
    }

    private static void handleSkinFetchError(final String playerKey, boolean isNormalSkin) {
        // A failed attempt for fetching a glow skin should not result in no longer attempting to fetch the normal skin
        if (isNormalSkin) {
            if (!hasFailedFetch.contains(playerKey)) {
                DragonSurvival.LOGGER.info("Custom skin for user {} doesn't exist", playerKey);
                hasFailedFetch.add(playerKey);
            }
        }
    }

    public static ResourceLocation fetchSkinFile(Player playerEntity, ResourceKey<DragonStage> dragonStage, String... extra) {
        return fetchSkinFile(playerEntity.getGameProfile().getName(), dragonStage, extra);
    }

    public static ResourceLocation getGlowTexture(Player player, ResourceKey<DragonStage> dragonStage) {
        ResourceLocation texture = null;
        String playerKey = player.getGameProfile().getName() + "_" + dragonStage.location().getPath();
        boolean renderCustomSkin = DragonStateProvider.getData(player).getSkinData().renderCustomSkin;

        if ((ClientDragonRenderer.renderOtherPlayerSkins || player == DragonSurvival.PROXY.getLocalPlayer()) && playerSkinCache.containsKey(playerKey) && renderCustomSkin) {
            if (playerGlowCache.containsKey(playerKey)) {
                return playerGlowCache.get(playerKey);
            } else {
                texture = fetchSkinFile(player, dragonStage, "glow");
                playerGlowCache.put(playerKey, texture);

                if (texture == null) {
                    DragonSurvival.LOGGER.info("Custom glow for user {} doesn't exist", player.getGameProfile().getName());
                }
            }
        }

        return texture;
    }

    public static void init() {
        init(false);
    }

    public static void init(boolean force) {
        if (initialized && !force) {
            return;
        }

        initialized = true;
        Collection<SkinObject> skins;
        invalidateSkins();
        String currentLanguage = Minecraft.getInstance().getLanguageManager().getSelected();
        NetSkinLoader first, second;

        if (currentLanguage.equals("zh_cn")) {
            first = new GitcodeSkinLoader();
            second = new GithubSkinLoader();
        } else {
            first = new GithubSkinLoader();
            second = new GitcodeSkinLoader();
        }

        if (!first.ping()) {
            if (!second.ping()) {
                DragonSurvival.LOGGER.warn("Unable to connect to skin database.");
                return;
            }

            first = second;
        }

        skinLoader = first;
        skins = skinLoader.querySkinList();

        if (skins != null) {
            parseSkinObjects(skins);
        }
    }

    public static void parseSkinObjects(Collection<SkinObject> skinObjects) {
        for (SkinObject skin : skinObjects) {
            boolean isGlow = false;
            String skinName = skin.name;

            skinName = skin.name.substring(0, skinName.indexOf("."));

            if (skinName.endsWith("_glow")) {
                isGlow = true;
                skinName = skin.name.substring(0, skinName.lastIndexOf("_"));
            }

            if (skinName.contains("_")) {
                String name = skinName.substring(0, skinName.lastIndexOf("_"));

                if (isGlow) {
                    name += "_glow";
                }

                String level = skinName.substring(skinName.lastIndexOf("_") + 1);

                try {
                    ResourceKey<DragonStage> dragonStage = ResourceKey.create(DragonStage.REGISTRY, DragonSurvival.res(level));

                    if (!SKIN_USERS.containsKey(dragonStage)) {
                        SKIN_USERS.put(dragonStage, new HashMap<>());
                    }

                    skin.short_name = name;
                    skin.glow = isGlow;
                    SKIN_USERS.get(dragonStage).putIfAbsent(name, skin);
                } catch (ResourceLocationException exception) {
                    DragonSurvival.LOGGER.warn("Could not parse dragon stage from the skin [{}] due to [{}]", skin.name, exception.getMessage());
                }
            }
        }
    }

    private static void invalidateSkins() {
        SKIN_USERS.clear();
        playerSkinCache.clear();
        playerGlowCache.clear();
        hasFailedFetch.clear();
    }

    public static boolean renderCustomSkin(final LocalPlayer player) {
        return DragonStateProvider.getData(player).getSkinData().renderCustomSkin;
    }
}