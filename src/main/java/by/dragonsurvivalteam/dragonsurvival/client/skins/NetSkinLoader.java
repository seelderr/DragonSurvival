package by.dragonsurvivalteam.dragonsurvival.client.skins;

import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

abstract public class NetSkinLoader {
    abstract public Collection<SkinObject> querySkinList();

    abstract public InputStream querySkinImage(SkinObject skin) throws IOException;

    abstract public boolean ping();

    protected String makeResourceName(String playerName, DragonLevel dragonStage, String... extra) {
        String playerKey = playerName + "_" + dragonStage.name;
        String[] text = ArrayUtils.addAll(new String[]{playerKey}, extra);
        return StringUtils.join(text, "_");
    }

    protected InputStream internetGetStream(URL url, int timeout) throws IOException {
        return internetGetStream(url, null, timeout);
    }

    protected InputStream internetGetStream(URL url, HashMap<String, String> headers, int timeout) throws IOException {
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        if (headers != null) {
            for (Map.Entry<String, String> headerItem : headers.entrySet()) {
                huc.setRequestProperty(headerItem.getKey(), headerItem.getValue());
            }
        }
        huc.setConnectTimeout(timeout);
        huc.setDoInput(true);
        huc.setRequestMethod("GET");
        huc.connect();
        return huc.getInputStream();
    }
}
