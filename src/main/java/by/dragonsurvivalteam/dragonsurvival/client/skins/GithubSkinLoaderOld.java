package by.dragonsurvivalteam.dragonsurvival.client.skins;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

public class GithubSkinLoaderOld extends NetSkinLoader {
    public static final String SKINS_LIST_LINK = "https://api.github.com/repositories/280658566/contents/src/test/resources?ref=master";
    private static final String SKINS_PING = "https://api.github.com/repositories/280658566";

    private static class SkinFileMetaInfo {
        String content;
        String encoding;
    }

    private static class SkinResponseItem {
        String path;
        String sha;
        String url;
    }

    @Override
    public Collection<SkinObject> querySkinList() {
        ArrayList<SkinObject> result = new ArrayList<>();
        try {
            Gson gson = GsonFactory.getDefault();
            URL url = new URL(SKINS_LIST_LINK);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(internetGetStream(url, 2 * 1000)))) {
                SkinResponseItem[] responseItems = gson.fromJson(reader, new TypeToken<SkinResponseItem[]>() {}.getType());

                for (SkinResponseItem skinResponse : responseItems) {
                    SkinObject skinObject = new SkinObject();
                    skinObject.name = skinResponse.path.replace("src/test/resources/", "");
                    skinObject.id = skinResponse.sha;
                    skinObject.user_extra = skinResponse;
                    result.add(skinObject);
                }

                return result;
            } catch (IOException exception) {
                DragonSurvivalMod.LOGGER.warn("Reader could not be closed", exception);
            }
        } catch (IOException e) {
            DragonSurvivalMod.LOGGER.log(Level.WARN, "Failed to get skin information in Github.");
        }

        return null;
    }

    @Override
    public InputStream querySkinImage(SkinObject skin) throws IOException {
        Gson gson = GsonFactory.getDefault();
        SkinResponseItem skinExtra = (SkinResponseItem) skin.user_extra;
        try (InputStream skinMetadataStream = internetGetStream(new URL(skinExtra.url), 15 * 1000)) {
            SkinFileMetaInfo skinMetaInfo = gson.fromJson(new BufferedReader(new InputStreamReader(skinMetadataStream)), SkinFileMetaInfo.class);
            String base64Content = skinMetaInfo.content.replace("\n", "");
            return new ByteArrayInputStream(Base64.getDecoder().decode(base64Content));
        }
    }

    @Override
    public boolean ping() {
        try (InputStream ignore = internetGetStream(new URL(SKINS_PING), 3 * 1000)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
