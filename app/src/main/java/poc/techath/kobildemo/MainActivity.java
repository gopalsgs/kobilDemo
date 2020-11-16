package poc.techath.kobildemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kobil.midapp.ast.api.enums.AstConfirmation;
import com.kobil.midapp.ast.api.enums.AstDeviceType;

import poc.techath.kobildemo.changePassword.ChangePasswordActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MyApplication application;
    private TextView otp_tv;
    private Button pinChangeButton;

    interface MainActivityInterface{
        void onGenerateOTP(String otp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = (MyApplication) getApplication();

        otp_tv = findViewById(R.id.otp_tv);
        pinChangeButton = findViewById(R.id.pinChange_btn);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            if(application.isOffline){
                actionBar.setTitle("Home (Offline)");
            }
            else {
                actionBar.setTitle("Home");
            }
        }


        findViewById(R.id.generateOtp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        if(application.isOffline){
            pinChangeButton.setVisibility(View.INVISIBLE);
        }

        pinChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });



    }


}