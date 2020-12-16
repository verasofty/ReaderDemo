package com.onsigna.readerdemo.utils;

public class POSSystem {

	public final static String PARAMETER = "PARAMETER";
	public final static String NAME_APP = "Credencial MPOS";
	public final static String DEVICE_ID = "DEVICE_ID";
	public final static String CONNECTED = "CONNECTED";
	public final static String POP_UP_SYNC = "POP_UP_SYNC";
	public static final String PREFS_READER = "reader";
	public final static String INACTIVE_APP = "0";
	public final static String ACTIVE_APP = "1";
	public final static String EMPTY_STRING = "";
	public final static String NEW_LINE = "\n";
	public final static String BLANK_SPACE = " ";	
	public static final String ZEROES = "0.00";
	public static final String ZERO = "0";
	public static final String ONE = "1";
	public static final String COMMA = ",";
	public static final String PERIOD = ".";
	public static final String MONEY_DECIMAL_REG_EXP = "#####0.00";	
	public static final String ARROBA = "@";
	public static final String MONEY_SIGN = "$";
	public static final String READING_METHOD_SWIPE = "0";
	public static final String READING_METHOD_CHIP = "1";	
	public static final String SUPPORTED_CURRENCY_CODE = "484";
	public static final String DEFAULT_LOT = "000001";
	public static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


	public static final String[] numbers = new String[] { "A", "B", "C", "D", "E",
    			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
    			"S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E",
    			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
    			"S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E",
    			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
    			"S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E",
    			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
    			"S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E",
    			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
    			"S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E",
    			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
    			"S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E",
    			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
    			"S", "T", "U", "V", "W", "X", "Y", "Z" };	
    
    
    public static final int IDMENU_PURCHASE = 1;    
    public static final int IDMENU_CANCEL = 2;    
    public static final int IDMENU_TRANSACTION_REPORT = 3;        
    public static final int IDMENU_INFORMATION = 8;    
    public static final int IDMENU_CONFIGURATION= 15;
    public static final int IDMENU_PAYBACK = 11;
    
    
    public static final String PARAM_USER = "USER";
    public static final String PARAM_USER_NAME = "USERNAME";
    public static final String PARAM_RRC_EXT = "RETRIEVAL_REFERENCE_NUMBER_EXT";
    public static final String PARAM_AUTHORIZED_AMOUNT = "AUTHORIZED_AMOUNT";
    public static final String PARAM_MASKED_CARD = "MASKED_CARD";
    public static final String PARAM_AUTHORIZATION_NUMBER = "AUTHORIZATION_NUMBER";
    public static final String PARAM_INVOKER = "INVOKER";
    public static final String PARAM_EMAIL = "EMAIL";
    public static final String PARAM_REF1 = "REF1";
    public static final String PARAM_REF2 = "REF2";
    public static final int PARAM_VALUE_INVOKER_TX_INFO = 1;
    public static final int PARAM_VALUE_INVOKER_POS_SALES = 2;
    public static final String PARAM_VALUE_INVOKER_POS_DEVOLUTION = null;

    public static final String DAILY_AMOUNT = "DAILY_AMOUNT";
    public static final String DAILY_TRANSACTIONS = "DAILY_TRANSACTIONS";

	public final static String AID = "AID";
	public final static String AMOUNT = "AMOUNT";
	public final static String ARQC = "ARQC";
	public final static String AUT = "AUT";
	public final static String BRAND = "BRAND";
	public final static String CARD = "CARD";
	public final static String CARDHOLDER = "CARDHOLDER";
	public final static String EMAIL = "EMAIL";
	public final static String EXPDATE = "EXPDATE";
	public final static String FOLIO = "FOLIO";
	public final static String LATITUDE = "LATITUDE";
	public final static String LONGITUDE = "LONGITUDE";
	public final static String LOTE = "LOTE";
	public final static String MONTO = "MONTO";
	public final static String PRODUCTNAME = "PRODUCTNAME";
	public final static String PROPINA = "PROPINA";
	public final static String DESCRIPTION = "DESCRIPTION";
	public final static String RRC = "RRC";
	public final static String USER = "USER";
	public final static String NUMBER_AUT = "NUMBER_AUT";
	public final static String CARD_NUMBER = "CARD_NUMBER";
	public final static String NAME_USER = "NAME_USER";
	public final static String NAME_MERCHANT = "NAME_MERCHANT";
	public final static String ADDRESS_HOME = "ADDRESS_HOME";
	public final static String MSI = "MSI";
	public final static String NUMERO_NIP = "NUMERO_NIP";
	public final static String TYPE_DEVICE   = "TYPE_DEVICE";
	public final static String TYPE_MERCHANT = "TYPE_MERCHANT";
	public final static String USER_LAST_NAME = "USER_LAST_NAME";
	public final static String NUMERO_GUARDADITO = "NUMERO_GUARDADITO";
	public final static String TYPE_CAPTURE_PHOTO_ADDRESS = "TYPE_CAPTURE_PHOTO_ADDRESS";
	public final static String NAME_IMAGE_ADDRESS_A = "NAME_IMAGE_ADDRESS_A";
	public final static String NAME_IMAGE_ADDRESS_B = "NAME_IMAGE_ADDRESS_B";
	public final static String PHOTO_IMAGE_ADDRESS_A = "PHOTO_IMAGE_ADDRESS_A";
	public final static String PHOTO_IMAGE_ADDRESS_B = "PHOTO_IMAGE_ADDRESS_B";
	public final static String TYPE_CAPTURE_PHOTO_CREDENCIAL = "TYPE_CAPTURE_PHOTO_CREDENCIAL";
	public final static String NAME_IMAGE_CREDENCIAL_FACE_A = "NAME_IMAGE_CREDENCIAL_FACE_A";
	public final static String PHOTO_IMAGE_CREDENCIAL_FACE_A = "PHOTO_IMAGE_CREDENCIAL_FACE_A";
	public final static String NAME_IMAGE_CREDENCIAL_FACE_B = "NAME_IMAGE_CREDENCIAL_FACE_B";
	public final static String PHOTO_IMAGE_CREDENCIAL_FACE_B = "PHOTO_IMAGE_CREDENCIAL_FACE_B";
	public final static String USERNAME = "USERNAME";
	public final static String ISSWIPE = "ISSWIPE";
	public final static String ISSN = "ISSN";
	public final static String EMAIL_PAN = "com.email.pan";
	public final static String RAW_PAN = "RAW_PAN";
	public final static String RAW_CVV = "RAW_CVV";
	public final static String RAW_EXP_DATE = "RAW_EXP_DATE";
	public final static String TOKEN_PROMOTOR = "TOKEN_PROMOTOR";

	public final static String NUMBER_COUNT_BANK = "NUMBER_COUNT_BANK";
	public final static String NUMBER_COUNT_BANK_CLABE = "NUMBER_COUNT_BANK_CLABE";
	public final static String NUMBER_TELEPHONE = "NUMBER_TELEPHONE";

	//CONSTANTES NUEVO REGISTRO ENTURA
	public final static String BUSINESS_NAME = "com.entura.mpos.android.activity.BUSINESS_NAME";
	public final static String COMMERCIAL_NAME = "com.entura.mpos.android.activity.COMMERCIAL_NAME";
	public final static String TYPE_COMMERCE = "com.entura.mpos.android.activity.COMMERCIAL_NAME";
	public final static String DISCOUNT_RATE = "com.entura.mpos.android.activity.DISCOUNT_RATE";
	public final static String DEPOSIT_ACCOUNT = "com.entura.mpos.android.activity.DEPOSIT_ACCOUNT";

	public final static String MEMBERSHIP = "com.entura.mpos.android.activity.MEMBERSHIP";
	public final static String ID_BRANCH_OFFICE = "com.entura.mpos.android.activity.ID_BRANCH_OFFICE";
	public final static String BRANCH_NAME = "com.entura.mpos.android.activity.BRANCH_NAME";
	public final static String ADDRESS = "com.entura.mpos.android.activity.ADDRESS";
	public final static String COLONY = "com.entura.mpos.android.activity.COLONY";
	public final static String CITY = "com.entura.mpos.android.activity.CITY";
	public final static String STATE = "com.entura.mpos.android.activity.STATE";
	public final static String POSTAL_CODE = "com.entura.mpos.android.activity.POSTAL_CODE";

	public final static String SERIAL_NUMBER_READER = "com.entura.mpos.android.activity.SERIAL_NUMBER_READER";
	public final static String FIRST_NAME = "com.entura.mpos.android.activity.FIRST_NAME";
	public final static String LAST_NAME_1 = "com.entura.mpos.android.activity.LAST_NAME_1";
	public final static String LAST_NAME_2 = "com.entura.mpos.android.activity.LAST_NAME_2";

	public final static String ACCESS_TYPE = "ACCESS_TYPE";
	public final static String CLIENT_CREDENCIAL = "CLIENT_CREDENCIAL";
	public final static String NEW_CLIENT = "NEW_CLIENT";
	public final static String LOGIN = "LOGIN";

    
    public static final int PLAN_ID_DEFAULT = 0;
    public static final int QTY_PAY_DEFAULT = 0;
    public static final int GRACE_NUMBER_DEFAULT = 0;

    //Constantes a usar
    public static final String MERCHANT_RETAIL = "Retail";
    public static final String MERCHANT_PROPINA = "Propina";
    public static final String DEVICE_LECTOR = "Lector";
    public static final String DEVICE_SCAN_CARD = "Scan-Card";
    public static final String DEVICE_SCAN_CARD_LECTOR = "lector/Scan-Card";
    public static final String LADA_NUMBER = "+521";
    public static final String TRANSACTION_SALE = "Venta";
    public static final String TRANSACTION_SALES = "Venta";
    public static final String TRANSACTION_CANCEL = "Cancelación";
    public static final String TRANSACTION_REFUND = "Devolución";
    public static final String TRANSACTION_FAILED = "DECLINADA";
    public static final String TRANSACTION_PROCCESS = "PROCESANDO";
    public static final String MESSAGE_ERROR_GPS = "Señal de GPS no encontrada. Compruebe el estado del GPS";
	public static final String TYPE_OPERATION_POS = "TYPE_OPERATION_POS";
	public static final String B_SIGN = "B_SIGN";
	public static final String JSON_FROM_PUSH = "JSON_FROM_PUSH";
	public static final String AUTHORIZATION_NUMBER = "AUTHORIZATION_NUMBER";

}
