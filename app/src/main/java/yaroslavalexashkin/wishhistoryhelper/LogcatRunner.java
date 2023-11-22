package yaroslavalexashkin.wishhistoryhelper;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogcatRunner {
    Process process;
    BufferedReader data;
    final Handler handler;
    LinkReciever lr;
    LogcatRunner(@NotNull Context c, LinkReciever lr) {
        handler = new Handler(c.getMainLooper());
        this.lr = lr;
        try {
            process = Runtime.getRuntime().exec("logcat");
            data = new BufferedReader(new InputStreamReader(process.getInputStream()));
            pollLog();
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot start process", e);
        }
    }

    void pollLog() {
        try {
            String line;
            WishLink wl = null;
            while (data.ready()) {
                if ((line = data.readLine()) == null)
                    break;
                if (wl != null)
                    continue;
                wl = WishLink.tryFromLog(line);
                if (wl == null)
                    continue;
                Log.e("hgh", "jgb");
               lr.processLink(wl);
            }
            handler.postDelayed(this::pollLog, 250);
        } catch (IOException ignored) {
            throw new RuntimeException("hj");
        }
    }

    void destroy() {
        process.destroy();
    }
}
