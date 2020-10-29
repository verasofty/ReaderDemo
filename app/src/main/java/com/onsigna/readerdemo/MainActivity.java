package com.onsigna.readerdemo;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.sf.upos.reader.HALReaderCallback;
import com.sf.upos.reader.IHALReader;
import com.sf.upos.reader.ReaderMngr;
import com.sf.upos.reader.StatusReader;
import com.sfmex.upos.reader.TransactionData;
import com.sfmex.upos.reader.TransactionDataResult;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static com.sf.upos.reader.dspread.bluetooth.QPOSReader.DEVICE_ID;
import static com.sfmex.utils.StringUtils.EMPTY_STRING;

public class MainActivity extends AppCompatActivity implements HALReaderCallback, SaleActivity.OnCancelTransactionListener {

    private final String TAG = MainActivity.class.getSimpleName();

    public final static String MONTO = "MONTO";
    public final static String PROPINA = "PROPINA";
    public final static String DESCRIPTION = "DESCRIPTION";
    public String m_user = EMPTY_STRING;
    public String m_userName = EMPTY_STRING;


    private static final int PERMISSION_REQUEST_CODE = 200;
    public static IHALReader reader;
    public static ArrayList<String> deviceArray = new ArrayList<>();
    public static ArrayList<String> deviceArrayCustom = new ArrayList<>();
    public static ArrayAdapter<String> adaptador;
    public static ListView lista;
    public static boolean stop;
    public boolean bStopConnected = true;
    public boolean connectedListSync;
    public boolean bConnected;

    public SharedPreferences sharedPreferences;

    private Button btnConnect;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpView();
        actions();
    }

    public void deviceConnected() {
        Log.d(TAG, "== Device is connected ==");
        btnConnect.setText("MPos CONNECTED!!!");
        btnConnect.setTextColor(Color.GREEN);
        bConnected = true;
    }

    public void deviceDisconectedUi() {
        Log.d(TAG, " == deviceDisconectedUi ()==");
        btnConnect.setText("MPos NOT CONNECTED!!!");
        btnConnect.setTextColor(Color.RED);
        bConnected = false;
    }


    private void setUpView() {
        lista = (ListView) findViewById(R.id.list_device);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnNext = (Button) findViewById(R.id.btnNext);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    }

    private void actions() {

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SaleActivity.class);
                startActivity(intent);
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initConnectBT();
            }
        });

        final SwipeRefreshLayout pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefreshList);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Actualizando dispositivos..");
                lista = (ListView) findViewById(R.id.list_device);
                deviceArrayCustom = new ArrayList<>();
                deviceArray = new ArrayList<>();
                adaptador = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, deviceArrayCustom);
                lista.setAdapter(adaptador);
                Toast.makeText(getBaseContext(), getResources().getString(R.string.message_refresh_devices), Toast.LENGTH_SHORT).show();
                reader.stopScan(MainActivity.this, MainActivity.this);
                reader.scan(MainActivity.this, MainActivity.this);
                pullToRefresh.setRefreshing(false);
            }
        });


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                String nameArray = deviceArray.get(position);
                Log.d(TAG, "Dispositivo seleccionado  name==> " + name);
                Log.d(TAG, "Dispositivo seleccionado  nameArray==> " + nameArray);
                saveIdDevice(null, nameArray);
                bStopConnected = true;
            }
        });

    }

    private void initConnectBT() {
        if (verifyStateBluetooth()) {
                initializesPermission();
                initReader();
                connectBT();
                //PosScanDeviceDialog.display(getFragmentManager(), this, new Bundle());
                return;
        } else {
            activeBluetooth();
        }
    }

    private void connectBT() {

        deviceArrayCustom = new ArrayList<>();
        deviceArray = new ArrayList<>();
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceArrayCustom);
        lista.setAdapter(adaptador);
        if (stop) {
            //fragment.deviceDisconectedUi();
            reader.stop(this, this);
        }
        reader.scan(this, this);

    }

    private void initReader() {
        if (reader == null) {
            Log.d(TAG, "reader ==> init---");
            reader = ReaderMngr.getReader(ReaderMngr.HW_DSPREAD_QPOS);
        }
    }

    private void saveIdDevice(DialogInterface dialog, String nameDevice) {
        Log.d(TAG, "== saveIdDevice () ==");
        MainActivity.this.sharedPreferences.edit().putString(DEVICE_ID, nameDevice).commit();
        // dialog.dismiss();
        reader.stopScan(MainActivity.this, this);
        reader.connect(MainActivity.this, this);
        //visibleMenuDeviceConnected(true);
        connectedListSync = true;

    }

    private void initializesPermission() {
        Log.d(TAG, "== initializesPermission() ==");
        if (!checkPermission()) {
            requestPermission();
        } else {
            //Snackbar.make(view, "Permission already granted.", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Permission already granted ", Toast.LENGTH_SHORT).show();

        }
    }

    private void requestPermission() {
        Log.d(TAG, "== requestPermission() ==");

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, BLUETOOTH, BLUETOOTH_ADMIN}, PERMISSION_REQUEST_CODE);

    }

    private boolean checkPermission() {
        Log.d(TAG, "== checkPermission() ==");

        int result1 = ContextCompat.checkSelfPermission(getBaseContext(), ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getBaseContext(), BLUETOOTH);
        int result3 = ContextCompat.checkSelfPermission(getBaseContext(), BLUETOOTH_ADMIN);

        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED;

    }

    protected void activeBluetooth() {
        Log.d(TAG, "== activeBluetooth() ==");
        final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setMessage(getResources().getString(R.string.dlg_message_bluetooth_off))
                .setPositiveButton(getResources().getString(R.string.btn_bluetooth_on), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bluetooth.enable();
                    }
                });
        builder.create();
        builder.show();

    }

    private boolean verifyStateBluetooth() {
        Log.d(TAG, "== verifyStateBluetooth() ==");
        final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        return bluetooth.isEnabled();
    }


    @Override
    public void onFinishedTransaction(TransactionDataResult transactionDataResult) {
        Log.d(TAG, "== onFinishedTransaction() ==");
        Log.d(TAG, "<-- responseCode : " + transactionDataResult.getResponseCode());
    }

    @Override
    public TransactionDataResult onBrokenTransaction(String s) {

        Log.d(TAG, "== HALReaderCallback.onBrokenTransaction() ==");
        Log.d(TAG, "<-- transactionID : " + s);
        return null;
    }

    @Override
    public void onSwipedCard(TransactionData transactionData) {
        Log.d(TAG, "== onSwipedCard() ==");
        Log.d(TAG, "<-- getMaskedPAN :" + transactionData.getMaskedPAN());
        Log.d(TAG, "<-- ExpirationDate : " + transactionData.getExpirationDateEx());
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
        if (readerInfo != null && !readerInfo.equals("")) {
            String nameCustom;
            String split[] = readerInfo.split("\\|");
            nameCustom = " SN : " + readerInfo.replace("|", "\nMC : ");
            if (deviceArray.size() == 0) {
                deviceArray.add(readerInfo);
                deviceArrayCustom.add(nameCustom);

            } else {
                boolean bExist = false;
                for (int x = 0; x < deviceArray.size(); x++) {

                    if (deviceArray.get(x).equals(readerInfo)) {
                        bExist = true;
                    }
                }
                if (!bExist) {
                    deviceArray.add(readerInfo);
                    deviceArrayCustom.add(nameCustom);
                }
            }


        }
        Log.d(TAG, "deviceArray.length ==>" + deviceArray.size());
        Log.d(TAG, "deviceArray.length ==>" + deviceArray.toString());
        adaptador = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, deviceArrayCustom);
        //saveIdDevice(null, "MPOS0000000050|A4:11:63:8F:FE:7B");
        bStopConnected = true;
        lista.setAdapter(adaptador);
    }

    @Override
    public void onReaderConnected() {
        Log.d(TAG, "== onReaderConnected() ==");
        deviceConnected();
        Log.d(TAG, "connectedListSync -> " + connectedListSync);
        if (connectedListSync){

            return;
        }
    }

    @Override
    public void onReaderNotConnected() {

        Log.d(TAG, " == onReaderNotConnected ()==");
        deviceDisconectedUi();
        Log.d(TAG, "bStopConnected ==" + bStopConnected);
        /*if (bStopConnected) {
            reader.connect(this, this);
        }*/
    }

    @Override
    public void onStopReader() {
        Log.d(TAG, "== onStopReader() ==");
        deviceDisconectedUi();
    }

    @Override
    public void onStopScan() {
        Log.d(TAG, "== onStopScan() ==");
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
    }

    @Override
    public void onFinishTrn() {
        Log.d(TAG, " == onFinishTrn ()==");
        reader.connect(this, this);
    }
}