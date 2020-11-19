package poc.techath.kobildemo;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.kobil.midapp.ast.api.AstOfflineFunctions;
import com.kobil.midapp.ast.api.AstOfflineFunctionsListener;
import com.kobil.midapp.ast.api.AstSdk;
import com.kobil.midapp.ast.api.AstSdkListener;
import com.kobil.midapp.ast.api.enums.AstCheckPinReason;
import com.kobil.midapp.ast.api.enums.AstConfigParameter;
import com.kobil.midapp.ast.api.enums.AstConfirmation;
import com.kobil.midapp.ast.api.enums.AstConfirmationType;
import com.kobil.midapp.ast.api.enums.AstConnectionState;
import com.kobil.midapp.ast.api.enums.AstContextType;
import com.kobil.midapp.ast.api.enums.AstDeviceType;
import com.kobil.midapp.ast.api.enums.AstInformationKey;
import com.kobil.midapp.ast.api.enums.AstMessageType;
import com.kobil.midapp.ast.api.enums.AstPinReason;
import com.kobil.midapp.ast.api.enums.AstPropertyOwner;
import com.kobil.midapp.ast.api.enums.AstPropertySynchronizationDirection;
import com.kobil.midapp.ast.api.enums.AstPropertyType;
import com.kobil.midapp.ast.api.enums.AstStatus;
import com.kobil.midapp.ast.api.enums.AstUrlBlockedReason;
import com.kobil.midapp.ast.api.messaging.AstMessaging;
import com.kobil.midapp.ast.api.messaging.AstMessagingListener;
import com.kobil.midapp.ast.sdk.AstSdkFactory;

import java.util.List;

import poc.techath.kobildemo.Utils.PrefStorage;
import poc.techath.kobildemo.avtivation.DoActivationActivity;
import poc.techath.kobildemo.login.LoginActivity;
import poc.techath.kobildemo.transaction.TransactionActivity;

public class MyApplication extends Application implements AstSdkListener {

    private static final String TAG = MyApplication.class.getSimpleName();
    private AstSdk sdk;
    private AstDeviceType astDeviceType;
    private AstOfflineFunctions offline;
    private Handler handler;
    private AstMessaging astMessaging;

    private boolean isOfflineUserSet = false;
    private boolean isLoginPageSet = false;
    public boolean isOffline = false;

    public  int timeout = 0;
    public  String transactionText = "";
    private TransactionActivity.TransactionActivityCallBack transactionActivityCallBack;

    public AstSdk getSdk() {
        return sdk;
    }

    public AstDeviceType getAstDeviceType() {
        return astDeviceType;
    }

    public AstOfflineFunctions getOfflineUserSet() {
        return offline;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void initSdk() {
        sdk = AstSdkFactory.getSdk(getApplicationContext(), this);

        final String localization = "EN";
        final byte[] appVersionNumber = new byte[]{1, 0, 0, 0, 0, 0};
        final String appName = "kobilDemo";

        handler = new Handler();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sdk.init(localization, appVersionNumber, appName);
            }
        });

        thread.start();
    }


    private void goToActivationPage(){
        Intent intent = new Intent(getApplicationContext(), DoActivationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToLoginPage(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToTransactionPage(){
        Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToHomePage(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public void onActivationBegin(AstDeviceType astDeviceType) {
        Log.e(TAG, "onActivationBegin: ");
        // Called only when the app is not activated
        this.astDeviceType = astDeviceType;

        handler.post(new Runnable() {
            @Override
            public void run() {
                goToActivationPage();
            }
        });
    }

    @Override
    public void onActivationEnd(AstDeviceType astDeviceType, AstStatus astStatus) {
        Log.e(TAG, "onActivationEnd: "+astStatus.toString());
        // Todo: silent login
    }

    @Override
    public void onLoginBegin(AstDeviceType astDeviceType, List<String> list) {
        Log.e(TAG, "onLoginBegin: ");
        // Called only when the app is activated and not loggedIn
        this.astDeviceType = astDeviceType;
        sdk.doCheckServerReachable();

//        sdk.doDeactivate(PrefStorage.readString(getApplicationContext(), "userName", ""));

    }

    @Override
    public void onLoginEnd(AstDeviceType astDeviceType, final AstStatus astStatus, String s, String s1, int i, int i1) {
        Log.e(TAG, "onLoginEnd: "+ astStatus.toString());
        if(astStatus.toString().equalsIgnoreCase("OK")){
            registerOfflineFunctions();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    goToHomePage();
                }
            });

            astMessaging = sdk.doRegisterMessaging(new AstMessagingListener() {
                @Override
                public void onCreateIdentityEnd(AstStatus astStatus, byte[] bytes, byte[] bytes1) {
                    Log.e(TAG, "onCreateIdentityEnd: "+new String(bytes) );
                    Log.e(TAG, "onCreateIdentityEnd1: "+new String(bytes1) );
                }

                @Override
                public void onExportIdentityEnd(AstStatus astStatus, byte[] bytes) {
                    Log.i(TAG, "onExportIdentityEnd: ");
                }

                @Override
                public void onGetCertificatesEnd(AstStatus astStatus, byte[] bytes, byte[] bytes1, byte[] bytes2) {
                    Log.i(TAG, "onGetCertificatesEnd: ");
                }

                @Override
                public void onImportEnd(AstStatus astStatus) {
                    Log.i(TAG, "onImportEnd: ");
                }

                @Override
                public void onDeleteIdentityEnd(AstStatus astStatus) {
                    Log.i(TAG, "onDeleteIdentityEnd: ");
                }
            });


        }else{
            displayToast(astStatus.toString());
        }
    }


    private MainActivity.MainActivityInterface mainActivityInterface;
    public void generateOtp(MainActivity.MainActivityInterface mainActivityInterface){
        this.mainActivityInterface = mainActivityInterface;
        if(offline != null) {
            offline.doGenerateOtp(null);
        }else{
            Toast.makeText(getApplicationContext(), "Register for offline functions", Toast.LENGTH_SHORT).show();
        }
    }

    public void registerOfflineFunctions() {

        Log.d(TAG, "registerOfflineFunctions: ");

        offline = sdk.doRegisterOfflineFunctions(new AstOfflineFunctionsListener() {
            @Override
            public void onProvidePinBegin() {
                Log.e(TAG, "onProvidePinBegin: " );
//                offline.doProvidePin(AstConfirmation.OK, "11223344".getBytes());
                goToLoginPage();
            }

            @Override
            public void onProvidePinEnd(AstStatus astStatus, int i, int i1) {
                if(astStatus == AstStatus.OK){

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            goToHomePage();
                        }
                    });

                }
                else{
                    displayToast(astStatus.toString());
                }

            }

            @Override
            public void onGenerateOtpEnd(AstStatus astStatus, String s) {
                mainActivityInterface.onGenerateOTP(s);
            }

            @Override
            public void onGenerateSecureSequenceEnd(AstStatus astStatus, byte[] bytes) {

            }

            @Override
            public void onGetReSynchronizationDataEnd(AstStatus astStatus, String s, int i) {

            }
        });

    }

    @Override
    public void onReActivationEnd(AstStatus astStatus) {

    }

    @Override
    public void onPinChangeBegin(AstDeviceType astDeviceType, AstStatus astStatus) {
        Log.e(TAG, "onPinChangeBegin: "+astStatus.toString());
    }

    @Override
    public void onPinChangeEnd(AstDeviceType astDeviceType, final AstStatus astStatus, int i) {
        Log.e(TAG, "onPinChangeEnd: "+astStatus.toString() );
        displayToast(astStatus.toString());
    }

    @Override
    public void onServerConnection(AstDeviceType astDeviceType, AstConnectionState astConnectionState) {
        Log.e(TAG, "onServerConnection: "+astConnectionState.toString());
        if(astConnectionState == AstConnectionState.REACHABLE){
            isOffline = false;

            if(!isLoginPageSet){
                isLoginPageSet = true;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        goToLoginPage();
                    }
                });
            }
        }

        if(astConnectionState == AstConnectionState.NOT_REACHABLE){
            isOffline = true;

            if(!isOfflineUserSet){
                String userName = PrefStorage.readString(getApplicationContext(), "userName", "");
                sdk.doSetUserId(userName);
                isOfflineUserSet = true;

            }
        }

    }

    @Override
    public void onDeviceConnection(AstDeviceType astDeviceType, AstConnectionState astConnectionState) {
        Log.e(TAG, "onDeviceConnection: " );
    }

    @Override
    public void onTransactionBlockBegin(AstDeviceType astDeviceType, int i) {
        Log.e(TAG, "onTransactionBlockBegin: "+ i );
        timeout = i;
        goToTransactionPage();
    }

    @Override
    public void onTransactionBlockEnd(AstDeviceType astDeviceType) {
        Log.e(TAG, "onTransactionBlockEnd: " );
    }

    @Override
    public void onTransactionBegin(AstDeviceType astDeviceType, final String s, AstConfirmationType astConfirmationType) {
        Log.e(TAG, "onTransactionBegin: "+s );
        transactionText = s;
        displayToast(s);
    }

    private void displayToast(final String s) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTransactionEnd(AstDeviceType astDeviceType, AstStatus astStatus) {
        final String status = astStatus.toString();
        Log.e(TAG, "onTransactionEnd: "+ status);
        if(transactionActivityCallBack != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    transactionActivityCallBack.onTransactionEnd(status);
                }
            });
        }
    }

    @Override
    public void onDisplayMessage(AstDeviceType astDeviceType, String s, AstMessageType astMessageType) {
    }

    @Override
    public void onInfoHardwareDisplayMessageBegin() {

    }

    @Override
    public void onInfoHardwareDisplayMessageEnd() {

    }

    @Override
    public void onInfoHardwareTransactionBegin() {

    }

    @Override
    public void onInfoHardwareTransactionEnd() {

    }

    @Override
    public void onPinRequiredBegin(AstDeviceType astDeviceType, AstPinReason astPinReason) {

    }

    @Override
    public void onPinRequiredEnd(AstDeviceType astDeviceType, AstStatus astStatus, int i) {

    }

    @Override
    public void onAlert(AstDeviceType astDeviceType, int i, int i1) {
        Log.i("AstSDKCallback", "onAlert(Subsystem " + i + ", ErrorCode " + i1 + ")"   );
    }

    @Override
    public Object getAppConfigParameter(AstConfigParameter astConfigParameter) {
        try {
            switch (astConfigParameter) {
                case USE_DEVICE_NAME_SOFTWARE:
                    return false;
                case USE_DEVICE_NAME_HARDWARE:
                    return false;
                case WHITELIST:
                    return "";
                case CONNECTION_RETRY_COUNTER:
                    return 5;
                case CONNECTION_RETRY_INTERVAL:
                    return 5;
                case SERVER_BUSY_TIMEOUT:
                    return 30;
                case CONFIG_BUNDLE:
                    return "";
                case CERTIFICATE_POLICY:
                    return "software";
                case WEB_VIEW_ERROR_PAGE:
                    return "Kobil Portal is not available. Please restart the app and try again";
                case ALLOWED_HOST_DEVICES:
                    return ".*";
                case ALLOW_OFFLINE_PIN_VERIFICATION:
                    return true;
                case BLUETOOTH_DISABLE_TIMEOUT:
                    return 30;
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onCertificateDataAvailable(AstDeviceType astDeviceType, byte[] bytes) {
        Log.e(TAG, "onCertificateDataAvailable: " );
        Log.d(TAG, "onCertificateDataAvailable: " + astDeviceType.toString() );
        Log.d(TAG, "onCertificateDataAvailable: " + new String(bytes));
    }

    @Override
    public void onUrlBlocked(String s, AstUrlBlockedReason astUrlBlockedReason) {
        Log.e(TAG, "onUrlBlocked: " );
    }

    @Override
    public void onTransportPinBegin(AstCheckPinReason astCheckPinReason) {
        Log.e(TAG, "onTransportPinBegin: " );
    }

    @Override
    public void onTransportPinEnd(AstStatus astStatus) {
        Log.e(TAG, "onTransportPinEnd: " );
    }

    @Override
    public void onPinUnblockBegin() {
        Log.e(TAG, "onPinUnblockBegin: " );
    }

    @Override
    public void onPinUnblockEnd(AstStatus astStatus, int i) {
        Log.e(TAG, "onPinUnblockEnd: " );
    }

    @Override
    public void onSetPropertyBegin(AstDeviceType astDeviceType, AstStatus astStatus) {
        Log.e(TAG, "onSetPropertyBegin: " );
    }

    @Override
    public void onSetPropertyEnd(AstDeviceType astDeviceType, AstStatus astStatus, int i, int i1) {
        Log.e(TAG, "onSetPropertyEnd: " );
    }

    @Override
    public void onGetPropertyBegin(AstDeviceType astDeviceType, AstStatus astStatus) {
        Log.e(TAG, "onGetPropertyBegin: " );
    }

    @Override
    public void onGetPropertyEnd(AstDeviceType astDeviceType, AstStatus astStatus, byte[] bytes, AstPropertyType astPropertyType, int i, int i1) {
        Log.e(TAG, "onGetPropertyEnd: " );
    }

    @Override
    public void onInformationAvailable(AstContextType astContextType, AstInformationKey astInformationKey, Object o) {
        Log.e(TAG, "onInformationAvailable: " );
        Log.d(TAG, "onInformationAvailable: "+astInformationKey.toString());
        Log.d(TAG, "onInformationAvailable: "+astInformationKey.name() + "  " + astContextType.getKey());
    }

    @Override
    public void onReport(AstDeviceType astDeviceType, int i) {
        Log.e("AstSDKCallback", "onReport(ErrorCode " + i + ") called.");
    }

    @Override
    public void onDeactivateEnd(AstStatus astStatus, List<String> list) {
        Log.e(TAG, "onDeactivateEnd: " );
    }

    @Override
    public void onDetectHwDevicesEnd(AstStatus astStatus, List<String> list) {
        Log.e(TAG, "onDetectHwDevicesEnd: " );
    }

    @Override
    public void onConnectHwDeviceEnd(AstStatus astStatus) {
        Log.e(TAG, "onConnectHwDeviceEnd: " );
    }

    @Override
    public void onDisconnectHwDeviceEnd(AstStatus astStatus) {
        Log.e(TAG, "onDisconnectHwDeviceEnd: " );
    }

    @Override
    public void appExit(int i) {
        Log.e(TAG, "appExit: " );

        if(reInitSdk){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    initSdk();
                    reInitSdk = false;
                }
            });
        }

    }

    @Override
    public void onRegisterOfflineFunctionsEnd(AstStatus astStatus) {
        Log.e(TAG, "onRegisterOfflineFunctionsEnd: "+astStatus.toString());

//        goToLoginPage();
    }

    @Override
    public void onPropertySynchronization(String s, AstPropertyOwner astPropertyOwner, AstPropertySynchronizationDirection astPropertySynchronizationDirection, AstStatus astStatus) {
        Log.e(TAG, "onPropertySynchronization: " );
    }

    @Override
    public void onBusyBegin() {
        Log.e(TAG, "onBusyBegin: " );
    }

    @Override
    public void onBusyEnd() {
        Log.e(TAG, "onBusyEnd: " );
    }

    @Override
    public void onSetUserIdEnd(AstStatus astStatus) {
        Log.e(TAG, "onSetUserIdEnd: " + astStatus.toString());
        registerOfflineFunctions();
    }

    @Override
    public void onRegisterMessagingEnd(AstStatus astStatus) {
        Log.e(TAG, "onRegisterMessagingEnd: " );
        astMessaging.doCreateIdentity();
    }

    private boolean reInitSdk = false;
    public void reInitSdk(){
        Log.e(TAG, "reInitSdk: ");
        reInitSdk = true;
        isOfflineUserSet = false;
        isLoginPageSet = false;
        isOffline = false;
        sdk.exit(0);
    }


    @Override
    public void onTerminate() {
        Log.e(TAG, "onTerminate: ");
        isOfflineUserSet = false;
        isLoginPageSet = false;
        isOffline = false;
        sdk.exit(8000);
        super.onTerminate();
    }

    public void setTransactionActivityCallBack(TransactionActivity.TransactionActivityCallBack transactionActivityCallBack) {
        this.transactionActivityCallBack = transactionActivityCallBack;
    }
}
