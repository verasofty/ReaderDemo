package com.onsigna.readerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sf.upos.reader.GenericReader;
import com.sf.upos.reader.HALReaderCallback;
import com.sf.upos.reader.StatusReader;
import com.sfmex.upos.reader.TransactionData;
import com.sfmex.upos.reader.TransactionDataRequest;
import com.sfmex.upos.reader.TransactionDataResult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;

import sfsystems.mobile.messaging.MobileResponse;
import sfsystems.mobile.messaging.PrintingInfo;

import static com.onsigna.readerdemo.MainActivity.DESCRIPTION;
import static com.onsigna.readerdemo.MainActivity.MONTO;
import static com.onsigna.readerdemo.MainActivity.PROPINA;
import static com.sf.utils.StringUtils.ZERO;
import static com.sfmex.utils.StringUtils.EMPTY_STRING;

public class SaleActivity extends AppCompatActivity implements HALReaderCallback {

    private final String TAG = SaleActivity.class.getSimpleName();

    private String m_amount = "0";
    private String m_feeAmount = "0";
    private String description = EMPTY_STRING;
    private double dTotal = 0;
    private TransactionData m_swipedCardTD;
    private boolean bChangeActivityReadCard;
    private OnCancelTransactionListener onCancelTransactionListener;
    private DecimalFormat format;
    private String m_user = EMPTY_STRING;
    private String m_userName = EMPTY_STRING;
    private final static String NOT_IMPLEMENTED = "Funcionality not implemented";

    private MainActivity mainActivity;
    private GPSLocator gpsLocator;
    private DBHelper dbHelper;

    private Button btnSale;
    private TextView tvData;
    private EditText etMonto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        setUpView();
        actions();
        parseIntentParameters();
    }

    public interface OnCancelTransactionListener {
        void onFinishTrn();
    }

    private void setOnCancelTransactionListener(OnCancelTransactionListener onCancelTransactionListener) {
        this.onCancelTransactionListener = onCancelTransactionListener;
    }

    private void changeDialog(String description) {

        if (description.equals("SendTransactionOnline")) {
            //tvData.setText(getResources().getString(R.string.dlg_send_transaction));
            tvData.append(getResources().getString(R.string.dlg_send_transaction));
        } else if (description.equals(getResources().getString(R.string.dlg_card_chip)) || description.equals(getResources().getString(R.string.dlg_card_swipe))) {
            tvData.append(description);

        }

    }

    private void parseIntentParameters() {
        Log.d(TAG, "== parseIntentParameters() ==");

        if (getIntent().hasExtra(MONTO)) {
            m_amount = getIntent().getStringExtra(MONTO);
        }
        if(getIntent().hasExtra(PROPINA)) {
            m_feeAmount = getIntent().getStringExtra(PROPINA);
        }
        if(getIntent().hasExtra(DESCRIPTION)) {
            m_feeAmount = getIntent().getStringExtra(DESCRIPTION);
        }

        dTotal = Double.parseDouble(m_amount) + Double.parseDouble(m_feeAmount);
        format = formatCurrency(getResources().getString(R.string.val_FormatCurrency));

        Log.d(TAG, "<-- user: " + m_user);
        Log.d(TAG, "<-- userName: " + m_userName);
        Log.d(TAG, "<-- amount: " + m_amount);
        Log.d(TAG, "<-- feeAmount: " + m_feeAmount);
        Log.d(TAG, "<-- description: " + description);


    }

    public DecimalFormat formatCurrency(String myAmount) {

        DecimalFormatSymbols myFormat = new DecimalFormatSymbols();
        myFormat.setDecimalSeparator('.');
        myFormat.setGroupingSeparator(',');

        DecimalFormat format = new DecimalFormat(myAmount, myFormat);
        return format;

    }

    public String formatString(String myString, String format, int iPosiciones,
                               boolean addPaddindAtFirst) {
        for (int iLength = myString.length(); iLength < iPosiciones; iLength++) {
            if (addPaddindAtFirst) {
                myString = format + myString;
            } else {
                myString = myString + format;
            }
        }

        return myString;
    }

    @Override
    protected void onResume() {
        super.onResume();

        startTransaction();
    }

    private void startTransaction() {
        Log.d(TAG, "== startTransaction() ==");
        TransactionDataRequest request = new TransactionDataRequest();
        request.setUser(m_user);
        request.setLatitud(gpsLocator.getLatitud());
        request.setLongitud(gpsLocator.getLongitud());

        request.setAmount(etMonto.getText().toString());

        request.setFeeAmount(m_feeAmount);
        request.setMesero(description);
        request.setReference1(description);
        request.setReference2(description);
        request.setTransactionID(formatString(dbHelper.getNewRRC(), ZERO, 6, true));
        request.setB_purchaseAndRecurringCharge("F");
        request.setOperation(EMPTY_STRING);

        MainActivity.reader.startTransaction(this, request, 30000, this);
    }

    private void setUpView () {
        btnSale = (Button) findViewById(R.id.btnSale);
        tvData = (TextView) findViewById(R.id.tvData);
        etMonto = (EditText) findViewById(R.id.etMonto);

        gpsLocator = new GPSLocator(this);
        gpsLocator.writeSignalGPS(this);
        dbHelper = new DBHelper(getBaseContext(), getResources().getString(R.string.db_Name),
                null, getResources().getInteger(R.integer.db_Version));

        mainActivity = new MainActivity();
    }

    private void setData(TransactionDataResult result) {
        tvData.append(result.getCardHolderName());
        tvData.append(result.getExpirationDate() + "\n" + result.getARQC());
    }

    private void actions() {
        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void processError(TransactionDataResult result) {
        Log.d(TAG, "== processError() ==");
        Log.d(TAG, "tdr.responseCode --> " + String.valueOf(result.getResponseCode()));

        switch (result.getResponseCode()) {
            case TransactionDataResult.RESP_CODE_CARD_NO_PRESENT:
            case TransactionDataResult.RESP_CODE_UNPLUGGED_READER:
                //dismiss();
                //activity.auxiliar.alertMessageError(result.getResponseCodeDescription());
                tvData.append(result.getResponseCodeDescription());
                break;

            case TransactionDataResult.RESP_CODE_CONEXION_ERROR:
            case TransactionDataResult.RESP_CODE_READER_REVERSE:
            case TransactionDataResult.RESP_CODE_NOT_OK:
            default:
                //dismiss();
                //activity.auxiliar.alertMessageError(result.getResponseCodeDescription());
                tvData.append(result.getResponseCodeDescription());

        }

    }


    @Override
    public void onFinishedTransaction(final TransactionDataResult result) {
        {
            Log.d(TAG, "== onFinishedTransaction() ==");
            Log.d(TAG, "<-- responseCode : " + result.getResponseCode());
            Log.d(TAG, "<-- result : " + result.toString());
            Log.d(TAG, "<-- getActivity()  : " + this);
        }
        if (this != null) {

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "== runOnUiThread.run() ==");

                    if (result.getResponseCode() == 0) {
                        bChangeActivityReadCard = true;
                        setData(result);
                    } else {
                        processError(result);
                    }
                }
            });

        } else {
            Log.d(TAG, " == Error en el OnFinishTransaction");

        }
    }

    @Override
    public TransactionDataResult onBrokenTransaction(String transactionID) {
        {
            Log.d(TAG, "== HALReaderCallback.onBrokenTransaction() ==");
            Log.d(TAG, "<-- transactionID : " + transactionID);
        }

        TransactionDataResult result = new TransactionDataResult();
        result.setResponseCodeDescription(NOT_IMPLEMENTED);

        return result;
    }

    @Override
    public void onSwipedCard(TransactionData td) {
        {
            Log.d(TAG, "== onSwipedCard() ==");
            Log.d(TAG, "<-- getMaskedPAN :" + td.getMaskedPAN());
            Log.d(TAG, "<-- ExpirationDate : " + td.getExpirationDateEx());
        }

        m_swipedCardTD = td;

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "== runInto() ==");
                m_swipedCardTD.setCVV("123");
                changeDialog(getResources().getString(R.string.dlg_card_swipe));
                //new ExecuteSendSwipedCard().execute();
            }
        });
    }

    @Override
    public void onTestResult(boolean b) {

    }

    @Override
    public void onFinishedInit(Map<String, String> map) {

    }

    @Override
    public void onStatusReader(StatusReader statusReader) {

    }

    @Override
    public void onReaderDetected(String readerInfo) {
        Log.d(TAG, "== onReaderDetected() ==");
        Log.d(TAG, "readerInfo --> " + readerInfo);
    }

    @Override
    public void onReaderConnected() {
        Log.d(TAG, "== onReaderConnected() ==");
    }

    @Override
    public void onReaderNotConnected()
    {
        Log.d(TAG, " == onReaderNotConnected ()==");
        mainActivity.bConnected = false;

        Log.d(TAG, "bStopConnected ==" + mainActivity.bStopConnected);
        if (mainActivity.bStopConnected) {
            mainActivity.reader.connect(SaleActivity.this, this);
        }
    }

    @Override
    public void onStopReader() {

    }

    @Override
    public void onStopScan() {

    }

    @Override
    public void onBatteryInfo(String batteryPercentage) {
        Log.d(TAG, "== onBatteryInfo() ==");
        Log.d(TAG, "batteryPercentage --> " + batteryPercentage);
    }

    @Override
    public void updateDialog(String text) {
        Log.d(TAG, "== onBatteryInfo() ==");
        Log.d(TAG, "text ==>" + text);
        changeDialog(text);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "== onStop () ==");
        super.onStop();
        // if (bChangeActivityReadCard) {
        if (MainActivity.reader != null) {
            MainActivity.reader.stop(this, this);
            //MainActivity.deviceDisconectedUi();
            if (onCancelTransactionListener != null) {
                onCancelTransactionListener.onFinishTrn();

            }
        }
        //}
    }
}