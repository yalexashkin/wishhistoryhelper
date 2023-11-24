package yaroslavalexashkin.wishhistoryhelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import yaroslavalexashkin.wishhistoryhelper.databinding.ActivityMainBinding;

import java.io.*;

public class MainActivity extends AppCompatActivity {
    static int permsRecieved = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPerms()) {
            if (permsRecieved == 0) {//workaround for when app was exited but not killed
                Toast.makeText(this, R.string.re_launch_app_toast, Toast.LENGTH_SHORT).show();
                permsRecieved = 1;
                System.exit(0);//hack bc permissions are somewhat buggy (logcat doesnt show other apps logs)
            }
            startService((View)null);
            finishAndRemoveTask();
        } else {
            permsRecieved = 0;
            yaroslavalexashkin.wishhistoryhelper.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            permsUiSwitch();
        }
    }

    void permsUiSwitch() {
        if (checkPerms()) {
            findViewById(R.id.noPermsLayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.StartServiceBtn).setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.re_launch_app_toast, Toast.LENGTH_SHORT).show();
            permsRecieved = 1;
            System.exit(0);
        } else {
            findViewById(R.id.noPermsLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.StartServiceBtn).setVisibility(View.INVISIBLE);
        }
    }

    boolean checkPerms() {
        return (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_LOGS) == PackageManager.PERMISSION_GRANTED);
    }

    public void rootGrant(View view) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
            osw.write(getString(R.string.root_grant_cmd));
            osw.flush();
            process.waitFor();
            osw.close();
            if (process.exitValue() != 0)
                throw new RuntimeException();
            permsUiSwitch();
        }
        catch (Exception e) {
            Toast.makeText(this, R.string.no_root_access_text, Toast.LENGTH_SHORT).show();
        }
    }

    public void startService(View view) {
        if (checkPerms() && !LinkGetterService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, LinkGetterService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
            Toast.makeText(this, R.string.service_started_text, Toast.LENGTH_SHORT).show();
        }
    }

    public void checkPermissionsBtn(View view) {
        permsUiSwitch();
    }
}