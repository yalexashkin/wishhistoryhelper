package yaroslavalexashkin.wishhistoryhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import yaroslavalexashkin.wishhistoryhelper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }

    public void checkClick(View view) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_LOGS) == PackageManager.PERMISSION_GRANTED)
            ((TextView)findViewById(R.id.testText)).setText("Logs allowed");
        else {
            ((TextView) findViewById(R.id.testText)).setText("No logs");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_LOGS}, 100);

        }
    }
}