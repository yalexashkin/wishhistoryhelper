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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPerms()) {
            //startService((View)null);
            //finishAndRemoveTask();
        }
        yaroslavalexashkin.wishhistoryhelper.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        permsUiSwitch();
    }

    void permsUiSwitch() {
        if (checkPerms()) {
            findViewById(R.id.noPermsLayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.StartServiceBtn).setVisibility(View.VISIBLE);
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
            osw.write("pm grant yaroslavalexashkin.wishhistoryhelper android.permission.READ_LOGS\nexit\n");
            osw.flush();
            process.waitFor();
            osw.close();
            if (process.exitValue() != 0)
                throw new RuntimeException();
            permsUiSwitch();
        }
        catch (Exception e) {
            Toast.makeText(this, "No root access", Toast.LENGTH_SHORT).show();
        }
    }

    public void startService(View view) {
        if (checkPerms() && !LinkGetterService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, LinkGetterService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
            Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkPermissionsBtn(View view) {
        permsUiSwitch();
    }
}