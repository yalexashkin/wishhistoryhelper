package yaroslavalexashkin.wishhistoryhelper;

import org.jetbrains.annotations.NotNull;

public class WishLink {
    String link;
    String region = "unknown";
    String game = "unknown";

    static WishLink tryFromLog(@NotNull String entry) {
        if (!entry.contains("https://") || !entry.contains("UnityUtils.sendUnityMessage"))
            return null;
        else
            return new WishLink(entry);
    }

    private WishLink(@NotNull String entry) {
        link = entry.substring(entry.indexOf("https://"));
        String[] vals = link.split("[&#]");
        for (String s:
                vals) {
            if (s.startsWith("region="))
                region = s.substring(7);
            if (s.startsWith("game_biz="))
                game = s.substring(9);
        }

    }
}
