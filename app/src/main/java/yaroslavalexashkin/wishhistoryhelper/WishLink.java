package yaroslavalexashkin.wishhistoryhelper;

import org.jetbrains.annotations.NotNull;

public class WishLink {
    String link;

    static WishLink tryFromLog(@NotNull String entry) {
        if (!entry.contains("https://") || !entry.contains("UnityUtils.sendUnityMessage"))
            return null;
        else
            return new WishLink(entry);
    }

    private WishLink(@NotNull String entry) {
        link = entry.substring(entry.indexOf("https://"));
    }
}
