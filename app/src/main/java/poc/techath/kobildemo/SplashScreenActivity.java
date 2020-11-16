package poc.techath.kobildemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        MyApplication application = (MyApplication) getApplication();

        if(application.getSdk() != null){
            Log.e("SplashScreenActivity", "onCreate: " );
            application.reInitSdk();
        }
        application.initSdk();

    }
}