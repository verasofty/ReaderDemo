package com.onsigna.readerdemo;

import static com.onsigna.readerdemo.utils.POSSystem.*;
import static com.sf.utils.StringUtils.ZERO;
import static com.sfmex.utils.StringUtils.EMPTY_STRING;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.onsigna.domain.AuthenticateData;
import com.onsigna.domain.TransactionData2;
import com.onsigna.readerdspreadlib.QPOSHALReaderImpl;
import com.sf.connectors.ConnectorMngr;
import com.sf.connectors.ISwitchConnector;
import com.sf.upos.reader.GenericReader;
import com.sf.upos.reader.HALReaderCallback;
import com.sf.upos.reader.IHALReader;
import com.sf.upos.reader.StatusReader;
import com.sfmex.upos.reader.TransactionData;
import com.sfmex.upos.reader.TransactionDataRequest;
import com.sfmex.upos.reader.TransactionDataResult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;

import sfsystems.mobile.messaging.MobileResponse;
import sfsystems.mobile.messaging.PrintingInfo;

public class SaleActivity extends AppCompatActivity implements HALReaderCallback {

    private final String TAG = SaleActivity.class.getSimpleName();

    private final String GREEN_COLOR = "#008000";
    private final String RED_COLOR = "#FF0000";
    private final String WHITE_COLOR = "#000000";
    private final int CODE_ERROR = 1;
    private final int CODE_SUCESSFUL = 2;
    private final int CODE_NORMAL = 0;
    private final int REQUEST_CODE_SIGNATURE = 9009;

    private static final String EMAIL = "juda.escalera@gmail.com";

    private final String description = EMPTY_STRING;
    private double dTotal = 0;
    private TransactionData m_swipedCardTD;
    public static SharedPreferences sharedPreferences;
    private OnCancelTransactionListener onCancelTransactionListener;
    private DecimalFormat format;
    private final String m_user = EMPTY_STRING;
    private final String m_userName = EMPTY_STRING;
    private final static String NOT_IMPLEMENTED = "Funcionality not implemented";
    private Boolean isConnectedDevice = false;

    public static IHALReader readerSale;
    private GPSLocator gpsLocator;
    private DBHelper dbHelper;
    private Button btnSale;
    private TextView tvData;
    private EditText etMonto;
    //private final String user_terminal = "gfy_test1@gmail.com";
    //private final String user_terminal = "PL9182BOMBA1Y2@gmail.com";
    //private final String user_terminal = "comisio1n@comision.com";
    //private final String user_terminal = "caja1comer@san.com";
    private final String user_terminal = "tomtom2@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        initReader();
        setUpView();
        actions();
        setServiceURL();
    }

    private void setServiceURL() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putString(ISwitchConnector.SHARED_PREFERENCES_URL, getResources().getString(R.string.DEFAULT_URL_SDBX))
                .apply();
    }

    public interface OnCancelTransactionListener {
        void onFinishTrn();
    }

    private void changeDialog(String description) {

        if (description.equals("SendTransactionOnline")) {
            nextLine();
            writeConsole(CODE_NORMAL, getResources().getString(R.string.dlg_send_transaction));

        } else if (description.equals(getResources().getString(R.string.dlg_card_chip)) || description.equals(getResources().getString(R.string.dlg_card_swipe))) {
            nextLine();
            writeConsole(CODE_NORMAL, description);

        } else {
            nextLine();
            writeConsole(CODE_NORMAL, description);
        }
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
    }

    private void startTransaction() {
        Log.d(TAG, "== startTransaction() ==");
        TransactionDataRequest request = new TransactionDataRequest();

        request.setAuthorizationNumber("");
        request.setUser(user_terminal);
        request.setTypeOperation("V");
        request.setLatitud(gpsLocator.getLatitud());
        request.setLongitud(gpsLocator.getLongitud());
        request.setAmount(etMonto.getText().toString());
        request.setFeeAmount("0");
        request.setMesero(description);
        request.setReference1(description);
        request.setReference2(description);
        request.setTransactionID(formatString(dbHelper.getNewRRC(), ZERO, 6, true));
        request.setB_purchaseAndRecurringCharge("F");
        request.setOperation(EMPTY_STRING);

        request.setAuthorizationNumber("431528");
        request.setRetrivalReferenceCode("433758674835");
        request.setSequenceNumber("000001");

        Thread thread = new Thread(() -> {
            try  {
                readerSale.startTransaction(SaleActivity.this, request, 30000, SaleActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    private void setUpView () {
        tvData = findViewById(R.id.tvData);
        format = formatCurrency(getResources().getString(R.string.val_FormatCurrency));
        etMonto = findViewById(R.id.etMonto);
        tvData.setText(EMPTY_STRING);
        tvData.setMovementMethod(new ScrollingMovementMethod());
        btnSale = findViewById(R.id.btnSale);
        gpsLocator = new GPSLocator(this);
        gpsLocator.writeSignalGPS(this);
        dbHelper = new DBHelper(getBaseContext(), getResources().getString(R.string.db_Name),
                null, getResources().getInteger(R.integer.db_Version));
    }

    private void setData(TransactionDataResult result) {
        Log.d(TAG, "== setData() ==");

        nextLine();
        writeConsole(CODE_SUCESSFUL, "== setData() ==");
        writeConsole(CODE_SUCESSFUL, "Transacción aprobada!");
        writeConsole(CODE_SUCESSFUL, "AuthNumber -> " + result.getAuthorizationNumber());
        writeConsole(CODE_SUCESSFUL, "Tarjeta -> " + result.getMaskedPAN());
        writeConsole(CODE_SUCESSFUL, "ARQC -> " + result.getARQC() + ", AID -> " + result.getAID());
        writeConsole(CODE_SUCESSFUL, "tlv -> " + result.getTlvResponse());
        writeConsole(CODE_SUCESSFUL, "Tipo de Firma --> " + result.getAuthenticationType());
        writeConsole(CODE_SUCESSFUL, "Comisión--> " + result.getSeattleAmount());

    }

    private void actions() {

        btnSale.setOnClickListener(view -> initConnectBT());

    }

    private void initConnectBT() {
        if ( verifyBluetoothState() ) {

            if (isConnectedDevice) {
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etMonto.getWindowToken(), 0);
                startTransaction();
            } else {
                connectBT();
            }

        } else {
            activeBluetooth();
        }
    }

    protected void activeBluetooth() {
        Log.d(TAG, "== activeBluetooth() ==");

        Toast.makeText(getBaseContext(),"Active el bluetooth", Toast.LENGTH_LONG).show();

    }

    private boolean verifyBluetoothState() {
        Log.d(TAG, "== verifyStateBluetooth() ==");
        final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        return bluetooth.isEnabled();
    }

    private void connectBT() {

        readerSale.scan(this, this);

    }

    private void initReader() {
        Log.d(TAG, "== initReader() ==");

        /*AuthenticateData.applicationSecret = "[SOLCITAR]";
        AuthenticateData.applicationKey = "[SOLCITAR]";
        AuthenticateData.applicationBundle = "[SOLCITAR]";*/

        AuthenticateData.applicationSecret = "qs4qa1ralmgb4cna";
        AuthenticateData.applicationKey = "8z00pj9qxh3vaaggo7lfyw2xkj3rv80c7o1u";
        AuthenticateData.applicationBundle = "test.api.service";

        if ( readerSale == null ) {
            Log.d(TAG, "instancing reader (getReader)");
            readerSale = new QPOSHALReaderImpl();
            ((GenericReader)readerSale).setSwitchConnector( ConnectorMngr.getConnectorByID(ConnectorMngr.REST_CONNECTOR) );
        }
    }

    private void processError(TransactionDataResult result) {
        Log.d(TAG, "== processError() ==");

        nextLine();
        writeConsole(CODE_ERROR, "== processError() ==");
        Log.d(TAG, "result? -> " + result.toString());
        Log.d(TAG, "ResponseCodeDescription() -> " + result.getResponseCodeDescription());
        writeConsole(CODE_ERROR, "ResponseCode ->" + result.getResponseCode());
        writeConsole(CODE_ERROR, "ResponseCodeDescription() ->" + result.getResponseCodeDescription());
    }

    @Override
    public void onFinishedTransaction(final TransactionDataResult result) {
        Log.d(TAG, "== onFinishedTransaction() ==");
        Log.d(TAG, "<-- responseCode : " + result.getResponseCode());
        Log.d(TAG, "<-- result : " + result.getRawPAN());
        Log.d(TAG, "<-- getARQC : " + result.getARQC());
        Log.d(TAG, "<-- getAID : " + result.getAID());

        Log.d(TAG, "<-- getActivity()  : " + this);

        this.runOnUiThread(() -> {
            Log.d(TAG, "== runOnUiThread.run() ==");

            if (result.getResponseCode() == 0) {
                setData(result);
                if ( !(result.getAuthenticationType().equals("PIN")) ) {
                    navigateToSignActivity(result);
                }
            } else {
                processError(result);
            }
        });
    }

    private void navigateToSignActivity(TransactionDataResult result) {
        Intent nextActivity = new Intent(this, POSSignTransaction.class);
        nextActivity.putExtra(USER, user_terminal);
        nextActivity.putExtra(PARAM_USER, user_terminal);
        nextActivity.putExtra(CARDHOLDER, result.getCardHolderName());
        nextActivity.putExtra(EXPDATE, result.getExpirationDate());
        nextActivity.putExtra(USERNAME, EMAIL);
        nextActivity.putExtra(PARAM_USER_NAME, EMAIL);
        nextActivity.putExtra(LOTE, result.getBatNumberInternal());
        nextActivity.putExtra(FOLIO, result.getTracingNumber());
        nextActivity.putExtra(PARAM_RRC_EXT, result.getTracingNumber());
        nextActivity.putExtra(BRAND, result.getProductName());
        nextActivity.putExtra(RRC, result.getTransactionID());
        nextActivity.putExtra(AUT, result.getAuthorizationNumber());
        nextActivity.putExtra(PARAM_AUTHORIZATION_NUMBER, result.getAuthorizationNumber());
        nextActivity.putExtra(CARD, result.getMaskedPAN());
        nextActivity.putExtra(PARAM_MASKED_CARD, result.getMaskedPAN());
        nextActivity.putExtra(AMOUNT, result.getAmount());
        nextActivity.putExtra(PARAM_AUTHORIZED_AMOUNT, result.getAmount());
        nextActivity.putExtra(PARAM_INVOKER, PARAM_VALUE_INVOKER_POS_SALES);
        nextActivity.putExtra(AID, result.getAID());
        nextActivity.putExtra(ARQC, result.getARQC());
        nextActivity.putExtra(PRODUCTNAME, result.getProductName());
        nextActivity.putExtra(ISSWIPE, getReadingMethod(result.isSwiped()));
        nextActivity.putExtra(ISSN, result.getIssuerName());
        nextActivity.putExtra(EMAIL_PAN, EMAIL);
        nextActivity.putExtra(PARAM_AUTHENTICATION_TYPE, result.getAuthenticationType());

        startActivityForResult(nextActivity, REQUEST_CODE_SIGNATURE);
    }

    private String getReadingMethod(boolean isSwiped) {
        {
            Log.d(TAG, "== getReadingMethod() ==");
            Log.d(TAG, "--> " + (isSwiped ? READING_METHOD_SWIPE : READING_METHOD_CHIP));
        }

        return isSwiped ? READING_METHOD_SWIPE : READING_METHOD_CHIP;
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
        Log.d(TAG, "== onSwipedCard() ==");
        Log.d(TAG, "getMaskedPAN --> " + td.getMaskedPAN());
        Log.d(TAG, " ExpirationDate --> " + td.getExpirationDateEx());

        m_swipedCardTD = td;

        this.runOnUiThread(() -> {
            new ExecuteSendSwipedCard().execute();
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
        sharedPreferences.edit().putString(DEVICE_ID, readerInfo).commit();
        readerSale.stopScan(this, this);
    }

    @Override
    public void onReaderConnected() {
        Log.d(TAG, "== onReaderConnected() ==");
        isConnectedDevice = true;
        nextLine();
        writeConsole(CODE_SUCESSFUL, "== Lector conectado ==");
    }

    @Override
    public void onReaderNotConnected()
    {
        Log.d(TAG, " == onReaderNotConnected ()==");
        isConnectedDevice = false;
        nextLine();
        writeConsole(CODE_ERROR, "== Lector desconectado ==");
    }

    @Override
    public void onStopReader() {

    }

    @Override
    public void onStopScan() {
        Log.d(TAG, "== onStopScan() ==");
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMonto.getWindowToken(), 0);
        startTransaction();
    }

    @Override
    public void onBatteryInfo(String batteryPercentage) {
        Log.d(TAG, "== onBatteryInfo() ==");
        Log.d(TAG, "batteryPercentage --> " + batteryPercentage);
    }

    @Override
    public void updateDialog(String text) {
        Log.d(TAG, "== updateDialog() ==");
        Log.d(TAG, "text ==> " + text);
        changeDialog(text);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "== onStop () ==");
        super.onStop();
        // if (bChangeActivityReadCard) {
        if (readerSale != null) {
            readerSale.stop(this, this);
            //MainActivity.deviceDisconectedUi();
            if (onCancelTransactionListener != null) {
                onCancelTransactionListener.onFinishTrn();

            }
        }
        //}
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    private void nextLine() {

        this.runOnUiThread(() ->
                tvData.append("\n")
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE_SIGNATURE  && resultCode  == RESULT_OK)
                writeConsole(CODE_SUCESSFUL, "== Venta con firma autógrafa ==");

        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void writeConsole(int code, String message) {

        nextLine();

        this.runOnUiThread(() -> {
            switch (code){
                case CODE_ERROR:
                    tvData.append(Html.fromHtml(getColoredSpanned(message,RED_COLOR)));
                    break;
                case CODE_SUCESSFUL:
                    tvData.append(Html.fromHtml(getColoredSpanned(message,GREEN_COLOR)));
                    break;
                case CODE_NORMAL:
                    tvData.append(Html.fromHtml(getColoredSpanned(message,WHITE_COLOR)));
                    break;
                default:
                    tvData.append(Html.fromHtml(getColoredSpanned(message,WHITE_COLOR)));
                    throw new IllegalStateException("Unexpected value: " + code);
            }
        });

    }

    private class ExecuteSendSwipedCard extends AsyncTask<String, Void, MobileResponse> {
        private final ProgressDialog dialog = new ProgressDialog(SaleActivity.this);

        protected void onPreExecute() {
            Log.d(TAG, "== ExecuteSendSwipedCard.onPreExecute() ==");

            this.dialog.setMessage(getResources().getString(R.string.dlg_execCardCharge));
            this.dialog.show();
        }

        protected MobileResponse doInBackground(final String... args) {
            Log.d(TAG, "== ExecuteSendSwipedCard.onPreExecute() ==");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SaleActivity.this);

            ((GenericReader) readerSale).getSwitchConnector().setContext(SaleActivity.this, "b");
            m_swipedCardTD.setOperationType(EMPTY_STRING);
            m_swipedCardTD.setB_purchaseAndRecurringCharge("F");

            m_swipedCardTD.setTlv("");
            return ((GenericReader) readerSale).getSwitchConnector().doPurchase((TransactionData2) m_swipedCardTD);
        }

        protected void onPostExecute(MobileResponse result) {
            Log.d(TAG, "== ExecuteSendSwipedCard.onPostExecute() ==");

            if (dialog.isShowing()) dialog.dismiss();

            Log.d(TAG, "result.getResponseCode() --> " + result.getResponseCode());
            if (result.getResponseCode() == 0) {
                Intent nextActivity = new Intent(SaleActivity.this, POSSignTransaction.class);

                nextActivity.putExtra(USER, m_user);
                nextActivity.putExtra(USERNAME, m_userName);
                nextActivity.putExtra(CARDHOLDER, m_swipedCardTD.getCardHolderName());
                nextActivity.putExtra(CARD, m_swipedCardTD.getMaskedPAN());
                nextActivity.putExtra(EXPDATE, m_swipedCardTD.getExpirationDateEx());
                nextActivity.putExtra(RRC, m_swipedCardTD.getTransactionID());
                nextActivity.putExtra(AUT, result.getAuthorizationNumber());
                nextActivity.putExtra(AMOUNT, format.format(dTotal));
                nextActivity.putExtra(ISSWIPE, getReadingMethod(true));
                nextActivity.putExtra(EMAIL_PAN, result.getEmail());

                PrintingInfo info = new PrintingInfo();
                info.fromJSON(result.getDescription());

                nextActivity.putExtra(LOTE, info.getBatNumberInternal());
                nextActivity.putExtra(FOLIO, info.getTracingNumber());
                nextActivity.putExtra(BRAND, info.getProductName());
                nextActivity.putExtra(PRODUCTNAME, info.getProductName());

                startActivity(nextActivity);
                SaleActivity.this.finish();
            } else
                Log.e(TAG, "result.getDescription() --> " + result.getDescription());
				/*finish();
				startActivity(getIntent());*/
        }
    }

}