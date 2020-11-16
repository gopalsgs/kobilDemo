package poc.techath.kobildemo.avtivation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kobil.midapp.ast.api.enums.AstDeviceType;

import poc.techath.kobildemo.MyApplication;
import poc.techath.kobildemo.R;

public class DoActivationActivity extends AppCompatActivity {

    private EditText userName_et;
    private EditText activationCode_et;
    private EditText password_et;
    private Button submit_btn;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_activation);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        actionBar.setTitle("Activation");

        userName_et =  findViewById(R.id.userName_et);
        password_et =  findViewById(R.id.password_et);
        activationCode_et =  findViewById(R.id.activationCode_et);
        submit_btn =  findViewById(R.id.submit_btn);
        application = (MyApplication) getApplication();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                application.getSdk().doActivation(
                        application.getAstDeviceType(),
                        password_et.getText().toString().trim().toCharArray(),
                        userName_et.getText().toString().trim(),
                        activationCode_et.getText().toString().trim().toCharArray()
                );
            }
        });

    }

}