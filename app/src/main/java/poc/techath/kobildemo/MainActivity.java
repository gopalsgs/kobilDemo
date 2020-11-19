package poc.techath.kobildemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kobil.midapp.ast.api.enums.AstConfirmation;
import com.kobil.midapp.ast.api.enums.AstDeviceType;

import poc.techath.kobildemo.Utils.PrefStorage;
import poc.techath.kobildemo.changePassword.ChangePasswordActivity;
import poc.techath.kobildemo.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MyApplication application;
    private TextView otp_tv;

    interface MainActivityInterface{
        void onGenerateOTP(String otp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = (MyApplication) getApplication();

        otp_tv = findViewById(R.id.otp_tv);


        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            if(application.isOffline){
                actionBar.setTitle("Home (Offline)");
            }
            else {
                actionBar.setTitle("Home");
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(application.isOffline){

        }
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.otp:
                application.generateOtp(new MainActivityInterface() {
                    @Override
                    public void onGenerateOTP(final String otp) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                otp_tv.setText(otp);
                            }
                        });
                    }
                });
                break;

            case R.id.logout:
                Intent i = new Intent(getApplicationContext(), SplashScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;

            case R.id.change_pin:
                Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.delete_user:
                application.getSdk().doDeactivate(PrefStorage.readString(application, "userName", ""));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}