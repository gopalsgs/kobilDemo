package poc.techath.kobildemo.transaction;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kobil.midapp.ast.api.enums.AstConfirmation;
import com.kobil.midapp.ast.api.enums.AstConfirmationType;
import com.kobil.midapp.ast.api.enums.AstDeviceType;

import java.util.Timer;
import java.util.TimerTask;

import poc.techath.kobildemo.MyApplication;
import poc.techath.kobildemo.R;

public class TransactionActivity extends AppCompatActivity {

    private MyApplication application;
    private String TAG = TransactionActivity.class.getSimpleName();
    private AstDeviceType astDeviceType;

    private TextView time_tv;
    private int timeOut = 0;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        application = (MyApplication) getApplication();
        astDeviceType = application.getAstDeviceType();
        timer = new Timer();

        time_tv = findViewById(R.id.timer_tv);
        timeOut = application.timeout;

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle("Transaction");

        ((TextView) findViewById(R.id.text_tv)).setText(application.transactionText);

        findViewById(R.id.approve_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+astDeviceType.toString());
                doTransaction(AstConfirmation.OK);
            }
        });


        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTransaction(AstConfirmation.CANCEL);
            }
        });


        setTimer();
    }

    private void setTimer() {
        Log.i(TAG, "setTimer: ");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: "+timeOut);

                if(timeOut == 0){
                    doTransaction(AstConfirmation.TIMEOUT);
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time_tv.setText(timeOut+"");
                        }
                    });
                    timeOut--;
                }
            }
        }, 0, 1000);

    }

    private void doTransaction(AstConfirmation astConfirmation) {
        application.getSdk().doTransaction(AstDeviceType.VIRTUALDEVICE, astConfirmation, application.transactionText);
        timer.cancel();
        time_tv.setText("-");
    }

    @Override
    public void onBackPressed() {
        application.getSdk().doTransaction(astDeviceType, AstConfirmation.CANCEL, "Test Transaction");
        super.onBackPressed();
    }

}