package com.onsigna.readerdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.onsigna.readerdemo.utils.Auxiliar;
import com.sf.upos.reader.GenericReader;
import com.sf.upos.reader.IHALReader;
import com.sf.upos.reader.ReaderMngr;
import com.sfmex.upos.reader.TransactionDataRequest;
import com.sfmex.upos.reader.TransactionDataResult;

import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.onsigna.readerdemo.utils.POSSystem.*;


public class POSSignTransaction extends AppCompatActivity implements OnGesturePerformedListener {
    private final static String TAG = POSSignTransaction.class.getSimpleName();

    private String m_user;
    private String userName;
    private String m_authorizedAmount;
    private String m_maskedCard;
    private String m_authenticationType;
    private int invoker;
    private String authorizationNumber;
    private String m_tracingNumber;
    private String transaction_type;
    private String emailPan;
    public static String mailDefault = "sfsystemsmexico@gmail.com";
    private GestureOverlayView gestures;
    private Button btnSend;
    private Auxiliar auxiliar;
    private Bitmap m_bitMapSignature;

    private final Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.d(TAG, "== onCreate() ==");

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_transaction);

        setInputParameters();
        settingUpUIControls();
        settingUpScreen();

        if ( m_authenticationType.equals("PIN") )
            new ExecuteSend().execute();
    }

    private final TimerTask taskCloseDialog = new TimerTask() {
        @Override
        public void run() {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "== TimerTask.run() ==");
                Log.d(TAG, "--> auxiliar.alert!=null : " + (auxiliar.alertMessage != null));
                try {
                    Log.d(TAG, "--> auxiliar.alert.isShowing() : " + (auxiliar.alertMessage.isShowing()));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            if (auxiliar.alertMessage != null && auxiliar.alertMessage.isShowing()) {
                if (BuildConfig.DEBUG) Log.d(TAG, "--> dismising dialog");
                auxiliar.alertMessage.dismiss();
            }

            navigateToNextActivity();
        }
    };

    @Override
    protected void onPause() {
        if (BuildConfig.DEBUG) Log.d(TAG, "== onPause() ==");

        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        if (BuildConfig.DEBUG) Log.d(TAG, "== onGesturePerformed() ==");

        m_bitMapSignature = null;
        if (gesture != null) {
            try {
                m_bitMapSignature = gesture.toBitmap(200, 150, 0, -16777216);

                overlay.clear(true);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        if (m_bitMapSignature != null) {
            try {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                m_bitMapSignature.compress(Bitmap.CompressFormat.PNG, 100, bs);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void settingUpScreen() {
        if (BuildConfig.DEBUG) Log.d(TAG, "== settingUpScreen() ==");
        if (isLayoutSmallOrNormal()) {
            if (BuildConfig.DEBUG) Log.d(TAG, "isLayoutSmallOrNormal ? true");

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            gestures.setGestureStrokeWidth(3);
        } else {
            if (BuildConfig.DEBUG) Log.d(TAG, "isLayoutSmallOrNormal ? false");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            gestures.setGestureStrokeWidth(5);
        }

        TextView tv = (TextView) findViewById(R.id.tvAuthorizedAmountResult);
        tv.setText(m_authorizedAmount);

        tv = (TextView) findViewById(R.id.tvMaskedCard);
        tv.setText(m_maskedCard);
    }

    private Boolean isLayoutSmallOrNormal() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "(getConfiguration().screenLayout ? " + (getResources().getConfiguration().screenLayout));
            Log.d(TAG, "(getConfiguration().screenLayout &  Configuration.SCREENLAYOUT_SIZE_MASK) ? " +
                    ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)));
        }

        return ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL)
                || ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL);
    }

    private void settingUpUIControls() {
        if (BuildConfig.DEBUG) Log.d(TAG, "== setUIControls() ==");
        auxiliar = new Auxiliar(POSSignTransaction.this);
        gestures = (GestureOverlayView) findViewById(R.id.gestures);
        STGestureListener st = new STGestureListener();
        gestures.addOnGestureListener(st);

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setEnabled(false);

    }

    class MainActivityOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
            boolean action = false;
            int stringId = -1;
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEND:
                    stringId = R.string.send;
                    break;
                case EditorInfo.IME_ACTION_SEARCH:
                    stringId = R.string.search;
                    break;
                case EditorInfo.IME_ACTION_DONE:
                case 66:
                    stringId = R.string.welcome;
                    sendMail(new View(getApplicationContext()));
                    break;
                default:
                    break;
            }
            return action;
        }

    }

    private void setInputParameters() {
        if (BuildConfig.DEBUG) Log.d(TAG, "== setInputParameters() ==");
        Intent intent = getIntent();
        if (!intent.hasExtra(PARAM_AUTHORIZED_AMOUNT) ||
                !intent.hasExtra(PARAM_MASKED_CARD))
            throw new RuntimeException(getResources().getString(R.string.message_error_data_voucher));

        m_user = intent.getStringExtra(PARAM_USER);
        userName = intent.getStringExtra(PARAM_USER_NAME);
        m_authorizedAmount = intent.getStringExtra(PARAM_AUTHORIZED_AMOUNT);
        m_maskedCard = intent.getStringExtra(PARAM_MASKED_CARD);
        m_authenticationType = intent.getStringExtra(PARAM_AUTHENTICATION_TYPE);
        authorizationNumber = intent.getStringExtra(PARAM_AUTHORIZATION_NUMBER);
        m_tracingNumber = intent.getStringExtra(PARAM_RRC_EXT);
        invoker = intent.getIntExtra(PARAM_INVOKER, PARAM_VALUE_INVOKER_TX_INFO);
        transaction_type = "V";
        emailPan = intent.getStringExtra(EMAIL_PAN);

        Log.d(TAG, "user --> " + m_user);
        Log.d(TAG, "userName --> " + userName);
        Log.d(TAG, "authorizedAmount --> " + m_authorizedAmount);
        Log.d(TAG, "maskedCard --> " + m_maskedCard);
        Log.d(TAG, "Invoker --> " + invoker);
        Log.d(TAG, "authNumber --> " + authorizationNumber);
        Log.d(TAG, "RRCExt --> " + m_tracingNumber);
        Log.d(TAG, "TransactionType --> " + transaction_type);
        Log.d(TAG, "emailPan --> " + emailPan);
        Log.d(TAG, "authenticationType --> " + m_authenticationType);

        Log.w(TAG, "Aqui se debe capturar si ocurrio algun error para enviar el mensaje al usuario y retornar a la invocaciÃ³n");

    }

    private class STGestureListener implements OnGestureListener {

        @Override
        public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay,
                                       MotionEvent event) {
        }

        @Override
        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            if (BuildConfig.DEBUG) Log.d(TAG, "== STGestureListener.onGestureEnded() ==");
            btnSend.setEnabled(true);
        }

        @Override
        public void onGestureStarted(GestureOverlayView overlay,
                                     MotionEvent event) {
        }

    }

    private class ExecuteSend extends AsyncTask<String, Long, TransactionDataResult> {
        private final ProgressDialog dialog = new ProgressDialog(POSSignTransaction.this);

        protected void onPreExecute() {
            if (BuildConfig.DEBUG) Log.d(TAG, "== ExecuteSend.onPreExecute() ==");

        }

        protected TransactionDataResult doInBackground(final String... args) {
            if (BuildConfig.DEBUG) Log.d(TAG, "== ExecuteSend.doInBackground() ==");
            if (m_bitMapSignature == null && m_authenticationType.equals("SIGN"))
                return null;

            TransactionDataRequest req = new TransactionDataRequest();
            if (emailPan == null || emailPan.equals(EMPTY_STRING)) {
                req.setEmail("sfsystemsmexico@gmail.com");
                emailPan = mailDefault;
            } else
                req.setEmail(emailPan);

            req.setAuthorizationNumber(authorizationNumber);
            req.setTracingNumber(m_tracingNumber);
            if (transaction_type != null)
                req.setTransactionType(transaction_type);
            req.setUser(m_user);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(POSSignTransaction.this);

            IHALReader reader = ReaderMngr.getReader(preferences.getString(ReaderMngr.DEFAULT_READER, ReaderMngr.HW_DSPREAD_QPOS));
            ((GenericReader) reader).getSwitchConnector().setContext(POSSignTransaction.this);

            return ((GenericReader) reader).getSwitchConnector().signTransaction(m_bitMapSignature, req);

        }

        protected void onPostExecute(TransactionDataResult result) {
            if (BuildConfig.DEBUG) Log.d(TAG, "== ExecuteSend.onPostExecute() ==");

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (result == null || result.getResponseCode() != 0)
                if (result == null)
                    auxiliar.alertMessageError(getResources().getString(R.string.message_error_sending_voucher));
                else auxiliar.alertMessageError(result.getResponseCodeDescription());
            else
                 auxiliar.messageOK(getResources().getString(R.string.message_sending_mail));

                try {
                    timer.schedule(taskCloseDialog, 3000, 3000);
                } catch (Exception exep) {
                    Log.e(TAG, exep.toString());
                    navigateToNextActivity();
                }
        }

    }

    private void navigateToNextActivity() {
        Log.d(TAG, "== navigateToNextActivity() ==");
        Intent intent = getIntent();
        intent.putExtra(AUTHORIZATION_NUMBER, "009812");
        setResult(RESULT_OK, intent);
        finish();

    }

    public void cleanSign(View view) {
        if (BuildConfig.DEBUG) Log.d(TAG, "== cleanSign() ==");
        if (gestures == null) {
            Log.e(TAG, "--> gestures = null");
            throw new RuntimeException(getResources().getString(R.string.message_error_clean_sign));
        }

        gestures.cancelClearAnimation();
        gestures.clear(true);

        btnSend.setEnabled(false);
    }

    public void cancelSign(View view) {
        if (BuildConfig.DEBUG) Log.d(TAG, "== cancelSign() ==");
        navigateToNextActivity();
    }

    public void sendMail(View view) {
        if (BuildConfig.DEBUG) Log.d(TAG, "== sendMail() ==");


        if (!emailPan.equals(EMPTY_STRING)) {
            if (!validaMailAddress(emailPan)) {
                Toast.makeText(this, getResources().getString(R.string.message_error_invalid_mail), Toast.LENGTH_SHORT).show();
            }
        }

        onGesturePerformed(gestures, gestures.getGesture());

        if (m_bitMapSignature == null) {
            auxiliar.alertMessageError(getResources().getString(R.string.message_error_sign_voucher));
            return;
        }
        new ExecuteSend().execute();
    }

    public static boolean validaMailAddress(String email){

        Pattern pattern;
        Matcher matcher;

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return  matcher.matches();

    }

}
