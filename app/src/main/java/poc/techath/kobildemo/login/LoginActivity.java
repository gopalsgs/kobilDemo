package poc.techath.kobildemo.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kobil.midapp.ast.api.enums.AstConfirmation;

import poc.techath.kobildemo.MyApplication;
import poc.techath.kobildemo.R;
import poc.techath.kobildemo.Utils.PrefStorage;

public class LoginActivity extends AppCompatActivity {

    private EditText userName_et;
    private EditText password_et;
    private Button submit_btn;
    private MyApplication application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle("Login");

        userName_et = findViewById(R.id.userName_et);
        password_et = findViewById(R.id.password_et);
        submit_btn = findViewById(R.id.submit_btn);
        application = (MyApplication) getApplication();

        if (application.isOffline) {
            userName_et.setEnabled(false);
            String userName = PrefStorage.readString(getApplicationContext(), "userName", "");
            userName_et.setText(userName);
        }

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefStorage.writeString(LoginActivity.this, "userName", userName_et.getText().toString().trim());
                if (!application.isOffline) {
                    application.getSdk().doLogin(
                            application.getAstDeviceType(),
                            password_et.getText().toString().trim().toCharArray(),
                            userName_et.getText().toString().trim()
                    );
                } else {
                    application.getOfflineUserSet().doProvidePin(AstConfirmation.OK, password_et.getText().toString().trim().getBytes());
                }
            }
        });
    }
}