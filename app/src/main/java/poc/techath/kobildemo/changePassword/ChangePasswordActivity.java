package poc.techath.kobildemo.changePassword;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kobil.midapp.ast.api.enums.AstConfirmation;
import com.kobil.midapp.ast.api.enums.AstDeviceType;

import poc.techath.kobildemo.MyApplication;
import poc.techath.kobildemo.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPassword_et;
    private EditText newPassword_et;
    private Button submit_btn;
    private MyApplication application;
    private String TAG = ChangePasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        application = (MyApplication) getApplication();

        oldPassword_et = findViewById(R.id.oldPassword_et);
        newPassword_et = findViewById(R.id.newPassword_et);
        submit_btn = findViewById(R.id.submit_btn);

        AstDeviceType astDeviceType = application.getAstDeviceType();

        application.getSdk().doPinChangeRequest(astDeviceType);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AstDeviceType astDeviceType = application.getAstDeviceType();
                Log.d(TAG, "onClick: "+astDeviceType.toString());

                application.getSdk().doPinChange(
                        astDeviceType,
                        AstConfirmation.OK,
                        oldPassword_et.getText().toString().trim().toCharArray(),
                        newPassword_et.getText().toString().trim().toCharArray()
                );
            }
        });

    }
}