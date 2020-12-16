package com.onsigna.readerdemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.onsigna.readerdemo.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Auxiliar {
    private final static String TAG = Auxiliar.class.getSimpleName();

    private final Activity currentActivity;


    public AlertDialog alertMessage;

    public static String getPastDay() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        String sYesterday = "";
        Date dYesterday = new Date();
        Date date = new Date();
        String sTransDate = formatter.format(date);

        try {

            calendar.setTime(formatter.parse(sTransDate));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            dYesterday = calendar.getTime();
            formatter = new SimpleDateFormat("yyyy/MM/dd");
            sYesterday = formatter.format(dYesterday);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sYesterday;
    }

    public static String getDateAndTime(long date, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf_short = new SimpleDateFormat("yyyy-MM-dd");
        String returDate = "";
        try {
            returDate = sdf.format(date).toString();
        } catch (Exception e) {

        }
        return returDate;
    }

    public static String getAmountFormated(String amount) {
        Float litersOfPetrol = Float.parseFloat(amount);
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        amount = df.format(litersOfPetrol);
        try {

            df.setMaximumFractionDigits(2);
            amount = df.format(litersOfPetrol);
        } catch (Exception e) {
            e.printStackTrace();
            amount = "0.0";
        }
        return amount;

    }

    public static void hideKeyboard(Window myWindow) {
        myWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void hideKeyboardAll(EditText myEdit, InputMethodManager imm) {
        imm.hideSoftInputFromWindow(myEdit.getWindowToken(), 0);
    }

    public Auxiliar(Activity Act) {
        currentActivity = Act;
    }

    public static void setFontType(TextView[] myObjects, Activity myAct) {
        for (int i = 0; i < myObjects.length; i++) {
            myObjects[i].setTypeface(myFont(myAct));
        }
    }

    public static void setFontType(EditText[] myObjects, Activity myAct) {
        for (int i = 0; i < myObjects.length; i++) {
            myObjects[i].setTypeface(myFont(myAct));
        }
    }

    public static void setFontType(Button[] myObjects, Activity myAct) {
        for (int i = 0; i < myObjects.length; i++) {
            myObjects[i].setTypeface(myFont(myAct));
        }
    }

    public static void setFontType(Button myObject, Activity myAct) {
        myObject.setTypeface(myFont(myAct));
    }

    public static void setFontType(EditText myObject, Activity myAct) {
        myObject.setTypeface(myFont(myAct));
    }

    public static void setFontType(TextView myObject, Activity myAct) {
        myObject.setTypeface(myFont(myAct));
    }

    public static Typeface myFont(Activity myAct) {
        Typeface type = Typeface.createFromAsset(myAct.getAssets(),
                "akii_font.ttf");
        return type;
    }

    public static boolean isDateValid(String dateToValidate, String dateFormat) {

        if (dateToValidate == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateToValidate);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    // Rellena una cadena con el caracter indicado (al principio o al final)
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

    // Da formato de moneda mexicana
    public static DecimalFormat formatCurrency(String myAmount) {

        DecimalFormatSymbols myFormat = new DecimalFormatSymbols();
        myFormat.setDecimalSeparator('.');
        myFormat.setGroupingSeparator(',');

        DecimalFormat format = new DecimalFormat(myAmount, myFormat);
        return format;

    }
    public static String formatCurrency(String myAmount, Locale locale) {

        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        String currency = format.format(Double.parseDouble(myAmount));
        return currency;

    }

    // Encripcion local del PAN
    public String encLocalePAN(String card) {
        String cadenaPAN = "";
        int iPos = card.length();
        String extract = "";
        try {
            for (int i = 0; i < iPos; i++) {
                extract = card.substring(0, 1);
                card = card.substring(1, card.length());
                cadenaPAN = cadenaPAN
                        + encPANaux((i + 1), Integer.parseInt(extract));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cadenaPAN;
    }

    public String encLocaleTrack(String track, int Longitud) {
        String cadenaTrack = "";
        String auxiliar = "";
        UUID uuid;
        int iPos = track.length();
        String extract = "";

        try {
            for (int i = 0; i < iPos; i++) {
                extract = track.substring(0, 1);
                track = track.substring(1, track.length());
                cadenaTrack = cadenaTrack
                        + encTrackAux((i + 1), Integer.parseInt(extract));
            }
            for (int i = 0; i < 8; i++) {
                uuid = UUID.randomUUID();
                auxiliar = auxiliar
                        + uuid.toString().replaceAll("-", "").toUpperCase();
            }
            cadenaTrack = cadenaTrack
                    + auxiliar.substring(0, 254 - cadenaTrack.length());

            cadenaTrack = cadenaTrack + String.valueOf(Longitud);
        } catch (Exception e) {
            e.printStackTrace();
            // Log.e(sSrc, e.getMessage());
        }

        return cadenaTrack;
    }

    private String encPANaux(int iPos, int iValor) {
        String respuesta = "";
        String sValor = "";

        switch (iValor) {
            case 0:
                sValor = "R";
                break;
            case 1:
                sValor = "G";
                break;
            case 2:
                sValor = "B";
                break;
            case 3:
                sValor = "H";
                break;
            case 4:
                sValor = "X";
                break;
            case 5:
                sValor = "8";
                break;
            case 6:
                sValor = "L";
                break;
            case 7:
                sValor = "Z";
                break;
            case 8:
                sValor = "K";
                break;
            case 9:
                sValor = "I";
                break;
            default:
                sValor = "";
                break;
        }

        UUID uuid = UUID.randomUUID();
        respuesta = uuid.toString().replaceAll("-", "").toUpperCase();
        respuesta = respuesta.substring(0, iPos + 1);
        respuesta = sValor + respuesta;

        return respuesta;
    }

    private String encTrackAux(int iPos, int iValor) {
        String respuesta = "";
        String sValor = "";

        switch (iValor) {
            case 0:
                sValor = "M";
                break;
            case 1:
                sValor = "F";
                break;
            case 2:
                sValor = "O";
                break;
            case 3:
                sValor = "J";
                break;
            case 4:
                sValor = "P";
                break;
            case 5:
                sValor = "N";
                break;
            case 6:
                sValor = "T";
                break;
            case 7:
                sValor = "I";
                break;
            case 8:
                sValor = "Y";
                break;
            case 9:
                sValor = "Z";
                break;
            default:
                sValor = "";
                break;
        }

        UUID uuid = UUID.randomUUID();
        respuesta = uuid.toString().replaceAll("-", "").toUpperCase();
        respuesta = respuesta.substring(0, iPos + 1);
        respuesta = sValor + respuesta;

        return respuesta;
    }

    // Recupera la MAC Adress del Movil
    public String macAddress(Context context, boolean plane) {
        WifiManager wifiMan = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String macAddr = wifiInf.getMacAddress();

        if (plane) {
            macAddr = macAddr.replace(":", "");
        }

        return macAddr;
    }

    public void messageOK(String sMessage) {
        Log.d(TAG, "== messageOK() ==");
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        LayoutInflater inflater = currentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.error_dialog_custom, null);
        TextView tv_message_info = view.findViewById(R.id.tv_message_info);
        Button btn_dismiss = view.findViewById(R.id.btn_dismiss);
        ImageView iv_notification = view.findViewById(R.id.iv_notification);

        iv_notification.setImageResource(R.drawable.ic_success);

        tv_message_info.setText(sMessage);
        btn_dismiss.setOnClickListener(view1 -> alertMessage.dismiss());

        builder.setView(view);
        alertMessage = builder.create();

        alertMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertMessage.show();
        return;
    }

    public void xmessageOKTimer(String sMessage, int waitForDismiss) {
        Log.d(TAG, "== messageOKTimer() ==");
        String infoTitle = currentActivity.getResources().getString(R.string.msg_titleInform);
        String textPositiveButton = currentActivity.getResources().getString(R.string.msg_btnOK);

        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setMessage(sMessage).setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info).setTitle(infoTitle)
                .setPositiveButton(textPositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertMessage = builder.create();
        alertMessage.show();

        return;
    }

    public void alertMessageError(String sMessage) {
        Log.d(TAG, "== messageErr() ==");
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        LayoutInflater inflater = currentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.error_dialog_custom, null);
        TextView tv_message_info = view.findViewById(R.id.tv_message_info);
        Button btn_dismiss = view.findViewById(R.id.btn_dismiss);
        ImageView iv_notification = view.findViewById(R.id.iv_notification);

        iv_notification.setImageResource(R.drawable.ic_error);

        tv_message_info.setText(sMessage);
        btn_dismiss.setOnClickListener(view1 -> alertMessage.dismiss());

        builder.setView(view);
        alertMessage = builder.create();

        alertMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertMessage.show();
        return;
    }


    public void messageErrTimer(String sMessage, int waitForDismiss) {
        Log.d(TAG, "== messageErrTimer() ==");
        String errorTitle = currentActivity.getResources().getString(R.string.msg_titleError);
        String textPositiveButton = currentActivity.getResources().getString(R.string.msg_btnOK);

        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setMessage(sMessage).setCancelable(false)
                .setIcon(android.R.drawable.ic_delete).setTitle(errorTitle)
                .setPositiveButton(textPositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertMessage = builder.create();
        alertMessage.show();


        return;
    }

    /*public static Dialog launcherPopupProgressDialog(Context context, String message) {
        Log.d(TAG, "== launcherPopupValidateToken() ==");

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View v = inflater.inflate(R.layout.activity_progress_dialog, null);



        final TextInputEditText et_token_received_confirm = (TextInputEditText) v.findViewById(R.id.et_token_received_confirm);

        builder.setView(v);

        return  builder.create();
    }*/

    public static boolean isPasswordValid(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=\\w*\\d)(?=\\w*[A-Z])(?=\\w*[a-z])\\S{6,16}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

}

