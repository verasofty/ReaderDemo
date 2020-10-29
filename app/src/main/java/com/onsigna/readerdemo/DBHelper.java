package com.onsigna.readerdemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.sfmex.utils.StringUtils.EMPTY_STRING;

public class DBHelper extends SQLiteOpenHelper {
	private final static String TAG = DBHelper.class.getSimpleName();
	
	private final String sTablaMenu = "AK_MENU";
	private final static String USERMENU_TABLE = "AK_USERMENU";
	private final String sTablaMsgs = "AK_MESSAGES";
	private final String sTablaParams = "AK_PARAMS";
	private final String sTablaPOSInfo = "AK_POSINFO";
	private final String sTablaUsers = "AK_USERS";
	public final static String INACTIVE_APP = "0";
	public final static String ACTIVE_APP = "1";
	private int errorNumber;
	private String sError;

	private String sPOSId;
	private String sPOSAlias;
	private String sPOSMerchant;
	private String sPOSRFC;
	private String sPOSAddress;

	private final String CREATE_AK_MENU_TABLE = "CREATE TABLE ["
			+ sTablaMenu
			+ "] ([IDMENU] INTEGER  NOT NULL PRIMARY KEY,[MNUNAME] TEXT  NOT NULL,[IDSCR] INTEGER  NOT NULL)";

	private final String CREATE_AK_MESSAGES_TABLE = "CREATE TABLE ["
			+ sTablaMsgs
			+ "] ([MSGFOLIO] TEXT  UNIQUE NOT NULL,[MSGDATE] DATE DEFAULT CURRENT_DATE NOT NULL,[MSGCONTENT] TEXT  NOT NULL)";

	private final String CREATE_AK_PARAMS_TABLE = "CREATE TABLE ["
			+ sTablaParams
			+ "] ([PRMTYPE] TEXT  UNIQUE NULL,[PRMVALUE] TEXT  NOT NULL,[PRMDATE] DATE DEFAULT CURRENT_DATE NOT NULL)";

	private final String CREATE_AK_POSINFO_TABLE = "CREATE TABLE ["
			+ sTablaPOSInfo
			+ "] ([IDPOS] TEXT  UNIQUE NOT NULL PRIMARY KEY,[POSALIAS] TEXT  NOT NULL,[POSMERCHANT] TEXT  NOT NULL,[POSRFC] TEXT  NOT NULL,[POSADDRESS] TEXT  NOT NULL)";

	private final String CREATE_AK_USERS_TABLE = "CREATE TABLE ["
			+ sTablaUsers
			+ "] ([USRID] TEXT  UNIQUE NOT NULL PRIMARY KEY, [USRNAME] TEXT  NOT NULL, [USREMAIL] TEXT  NOT NULL, [USRDATE] DATE DEFAULT CURRENT_DATE NOT NULL)";

	private final String CREATE_AK_USERMENU_TABLE = "CREATE TABLE ["
			+ USERMENU_TABLE
			+ "] ([USRID] TEXT  NOT NULL,[IDMENU] INTEGER  NOT NULL,PRIMARY KEY ([USRID],[IDMENU]))";

	private SQLiteDatabase systemDB;

	// private Locale locale;

	/**
	 * Constructor Toma referencia hacia el contexto de la aplicaciÃ³n que lo
	 * invoca para poder acceder a los 'assets' y 'resources' de la aplicaciÃ³n.
	 * Crea un objeto DBOpenHelper que nos permitirÃ¡ controlar la apertura de la
	 * base de datos.
	 * 
	 * @param context
	 */

	public DBHelper(Context context, String name, CursorFactory factory,
                    int version) {
		super(context, name, factory, version);
	}

	// Elimina un mensaje de reverso
	public boolean insertPOSDeatils(String[] sCampos, String[] sValores) {

		boolean bResultado = false;
		int iResultado = 0;
		// Limpia lso atributos de la clase
		cleanErrorAttributes();

		// Intenta hacer el borrado de la tabla de Parametros
		try {
			// Abre la base de datos
			open();
			// Borrar el registro
			iResultado = deleteReg(sTablaPOSInfo, null, null);
			System.out.println("borrado de datos pos: " + iResultado);
			// Inserta los nuevos valores
			bResultado = insertReg(sTablaPOSInfo, sCampos, sValores);
			// Valida que pueda insertar la solicitd de autorizacion
			if (!bResultado) {
				setErrorCode(-1);
				setErrorDescription("Error del sistema... \nNo se puede insertar el detalle del POS en la BD local");
				return false;
			}

			// Si hubo algÃºn error
		} catch (SQLiteException e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			setErrorDescription(e.getLocalizedMessage());
			iResultado = -1;
			bResultado = false;
		} finally {
			// Cierra la base de datos
			close();
		}
		// Devuelve el resultado
		return bResultado;

	}

	// Envia el reverso de una transacciÃ³n
	public int getPOSDetails(Resources gR) {
		// Setea los valores iniciales...
		Cursor mycursor;
		String[] campos = new String[] { "IDPOS, POSALIAS, POSMERCHANT, POSRFC, POSADDRESS" };
		cleanErrorAttributes();

		int iDetails = 0;

		try {
			// Abre la base de datos
			open();

			// Arma el cursor
			mycursor = queryBD(sTablaPOSInfo, campos, "", null, null);
			iDetails = mycursor.getCount();
			if (iDetails == 0) {
				setErrorCode(1);
				setErrorDescription("No hay datos de la terminal");
				return 0;
			}
			// Nos aseguramos de que existe al menos un registro
			if (mycursor.moveToFirst()) {
				// Setea los valores recuperados a la variables

				setsPOSId(mycursor.getString(0));
				setsPOSAlias(mycursor.getString(1));
				setsPOSMerchant(mycursor.getString(2));
				setsPOSRFC(mycursor.getString(3));
				setsPOSAddress(mycursor.getString(4));
			}
			// Cierra el cursor
			mycursor.close();

		} catch (Exception e) {
			// Recupera y setea el error
			e.printStackTrace();
			setErrorDescription(e.getMessage());
			setErrorCode(1);
		} finally {
			// Cierra la base de datos
			close();
		}
		// Devuelve el resultado
		return iDetails;
	}

	@Override
	// Intenta crear la base de datos (solo la primera vez que se instancia)
	public void onCreate(SQLiteDatabase db) {

		Log.d(TAG, "********ESTOY ENTRANDO EN EL CREATE");

		try {
			// Se ejecuta la sentencia SQL de creaciÃ³n de la tabla
			db.execSQL(CREATE_AK_MENU_TABLE);
			db.execSQL(CREATE_AK_MESSAGES_TABLE);
			db.execSQL(CREATE_AK_PARAMS_TABLE);
			db.execSQL(CREATE_AK_POSINFO_TABLE);
			db.execSQL(CREATE_AK_USERS_TABLE);
			db.execSQL(CREATE_AK_USERMENU_TABLE);

			Log.d(TAG, "Ya se crearon las tablas de la base de datos");

			// Inserta los valores iniciales
			db.execSQL("INSERT INTO "
					+ sTablaParams
					+ "(PRMTYPE, PRMVALUE) VALUES ('AUXK', 'aK9fG3E7ac34Ad8113ccfbdnad22a4e35b745473db237d7101db5f32592a48M4e175dd2092643cb51b379755aa8946f1934c0a866f15c3844448a54h8dfb49db45194f44babe7f3c3d1ca2fd7474ed691209b057ff61fbc718934224ba93d582fb271a6d6fe4c8d489f9665615a1292177796be7cf874b25ab2b1e1a51c71c1c6f325e681724223b0bf3bb1ed8d9740r0fcb51d8391d48c79aa6e9aa')");
			db.execSQL("INSERT INTO " + sTablaParams
					+ "(PRMTYPE, PRMVALUE) VALUES ('RRC', '1')");
			db.execSQL("INSERT INTO " + sTablaParams
					+ "(PRMTYPE, PRMVALUE) VALUES ('POS_ACTIVE', '0')");
			db.execSQL("INSERT INTO " + sTablaParams
					+ "(PRMTYPE, PRMVALUE) VALUES ('DEVICE_ID', '')");
			// Inserta las opciones del menÃº
			db.execSQL("INSERT INTO " + sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (0, 'Post propina', 0)");
			db.execSQL("INSERT INTO " + sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (1, 'Venta', 1)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (2, 'Cancelaciones', 2)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (3, 'Consulta Transacciones', 4)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (4, 'Resumen Transacciones', 5)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (5, 'Ajustes Post-Venta', 3)");
			db.execSQL("INSERT INTO " + sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (6, 'Check In', 6)");
			db.execSQL("INSERT INTO " + sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (7, 'Check Out', 7)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (8, 'Informacion POS', 8)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (9, 'Cancel Check In', 9)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (10, 'Ajuste Check Out', 10)");
			db.execSQL("INSERT INTO " + sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (11, 'Devolución', 11)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (12, 'Reautorización', 12)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (13, 'Reimpresión', 13)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (14, 'Check Out Express', 14)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (15, 'Inicializacion de LLaves', 15)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (16, 'ABM o Referencias', 16)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (17, 'ABM Propinas', 17)");
			db.execSQL("INSERT INTO "
					+ sTablaMenu
					+ " (IDMENU, MNUNAME, IDSCR) VALUES (18, 'Venta Restaurante', 18)");
			// Inserta los datos bases del POS
			db.execSQL("INSERT INTO "
					+ sTablaPOSInfo
					+ "(IDPOS, POSALIAS, POSMERCHANT, POSRFC, POSADDRESS) VALUES ('000000', 'POS MOVIL', 'MI COMERCIO', 'XXXX000000XXX', 'MI DOMICILIO')");
		} catch (SQLiteException e) {
			setErrorCode(-1);
			setErrorDescription("Error creando la Base de Datos");
			e.printStackTrace();
			// throw new Error("Error copiando Base de Datos");
		} catch (Exception e) {
			setErrorCode(-1);
			setErrorDescription("Error creando la Base de Datos");
			e.printStackTrace();
			// throw new Error("Error copiando Base de Datos");
		}

		Log.d(TAG, "Ya se insertaron los valores inciales");

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "********ESTOY ENTRANDO EN EL UPGRADE");
	}

	// Elimina un mensaje de reverso
	public int deleteReverseMsg(String sFolio, String sFecha) {
		String table = "AK_MESSAGES";
		String condiciones = "MSGFOLIO=? AND MSGDATE=?";
		String[] args = new String[] { sFolio, sFecha };
		int iResultado = 1;

		// Intenta hacer el borrado en la BD
		try {
			// Abre la base de datos
			open();
			// Borrar el registro
			iResultado = deleteReg(table, condiciones, args);
			// Si hubo algÃºn error
		} catch (SQLiteException e) {
			e.printStackTrace();
			// Log.e(sSrc, e.getLocalizedMessage());
			setErrorDescription(e.getLocalizedMessage());
			iResultado = -1;
		} finally {
			// Cierra la base de datos
			close();
		}
		// Devuelve el resultado
		return iResultado;

	}

	public boolean insertReverseMsg(String sFolio, String sFecha,
			String sMessage) {

		cleanErrorAttributes();

		// Abre la base de datos
		open();

		// Setea los valores necesarios para el Insert
		String[] campos = new String[] { "MSGFOLIO", "MSGDATE", "MSGCONTENT" };
		String[] valores = new String[] { sFolio, sFecha, sMessage };
		// Hace la inserciÃ³n en la tabla AUT
		boolean insert = insertReg(sTablaMsgs, campos, valores);
		// Valida que pueda insertar la solicitd de autorizacion
		if (!insert) {
			setErrorCode(-1);
			setErrorDescription("Error del sistema... \nNo se puede insertar la autorizacion en la BD local");
			return false;
		}
		// Cierra la base de datos
		close();

		// Log.d(sSrc, "Ya inserte el mensaje de reverso");
		return insert;
	}

	// Recupera el CipherKey de Enc
	public String getAuxK_Val() {
		Cursor mycursor;
		String[] campos = new String[] { "PRMVALUE" };
		String[] args = new String[] { "AUXK" };
		String sResult = "";
		String sGetValue = "";
		String sAuxiliar = "";
		int iPos = 0;
		int iSpaces = 0;

		try {
			open();
			mycursor = queryBD(sTablaParams, campos, "PRMTYPE=?", args, null);

			// Nos aseguramos de que existe al menos un registro
			if (mycursor.moveToFirst()) {
				// Recorremos el cursor hasta que no haya mÃ¡s registros
				do {
					sResult = mycursor.getString(0);
				} while (mycursor.moveToNext());
			}
			mycursor.close();
			close();
			sAuxiliar = sResult;
			// Mientras siga estando mas grande la cadena
			while (sAuxiliar.length() >= iPos) {
				iPos = iPos + 2;
				iSpaces = iPos + 1;
				sGetValue += sAuxiliar.substring(iPos, iPos + 1);
				sAuxiliar = sAuxiliar.substring(iSpaces);
			}

			// Log.d(sSrc, "Mi llave es: " + sGetValue);
			return sGetValue;
		} catch (Exception e) {
			setErrorCode(-1);
			setErrorDescription(e.getMessage());
			e.printStackTrace();
			close();
			return "";
		}

	}

	// Genera un nuevo RRC
	public String getNewRRC() {

		// Inicializa variables
		String consecutive = "";
		int nextConsecutive = 0;
		int updateOK = 0;

		// Limpia valores
		cleanErrorAttributes();

		// Inicia el proceso
		try {
			// Recupera el Ãºltimo consecutivo para generar referencia
			String[] campos = new String[] { "PRMVALUE" };
			String[] valores = new String[] { "" };
			String[] arguments = new String[] { "RRC" };
			// Abre la base de datos
			open();
			// Arma el cursor de consulta
			Cursor mycursor = queryBD(sTablaParams, campos, "PRMTYPE=?",
					arguments, null);
			// Nos aseguramos de que existe al menos un registro
			if (mycursor.moveToFirst()) {
				// Recorremos el cursor hasta que no haya mÃ¡s registros
				do {
					consecutive = mycursor.getString(0);
					nextConsecutive = Integer.parseInt(consecutive);
					nextConsecutive++;
				} while (mycursor.moveToNext());
			} else {
				setErrorCode(-1);
				setErrorDescription("Error del sistema... \nNo se puede recuperar el Retrieval Reference Code");
				mycursor.close();
				close();
				return "";
			}

			// Cierra el cursor
			mycursor.close();

			// Actualiza el nÃºmero de referencia
			valores = new String[] { String.valueOf(nextConsecutive) };
			updateOK = updateRegs(sTablaParams, campos, valores, "PRMTYPE=?",
					arguments);

			// Si no puede actualizar el nÃºmero de referencia...
			if (updateOK != 1) {
				setErrorCode(-1);
				setErrorDescription("Error del sistema... \nNo se puede actualizar el Retrieval Reference Code");
				close();
				return EMPTY_STRING;
			}
			
		} catch (Exception e) {
			setErrorCode(-1);
			setErrorDescription(e.getMessage());
			consecutive = EMPTY_STRING;

		} finally {
			close();
		}

		return consecutive+1120;
	}

	public String recoverStatusPOS() {
		 Log.d(TAG, "== recoverStatusPOS() ==");

		Cursor mycursor;
		String[] campos = new String[] { "PRMVALUE" };
		String[] args = new String[] { "POS_ACTIVE" };
		String appStatus = INACTIVE_APP;

		try {
			open();
			mycursor = queryBD(sTablaParams, campos, "PRMTYPE=?", args, null);

			if (mycursor.moveToFirst()) {
				appStatus = mycursor.getString(0);
				 Log.d(TAG, "Application Status : " + appStatus);
			}

			mycursor.close();
		} catch (Exception e) {
			setErrorCode(-1);
			setErrorDescription(e.getMessage());
			e.printStackTrace();
		} finally {
			close();
		}

		return appStatus;

	}

	public Cursor queryBDraw(String query, String[] condiciones) {
		 Log.d(TAG, "== queryBDraw() ==");
		
		Cursor mycursor;
		try {
			 Log.d(TAG, "<-- query : " + query); 
			
			mycursor = systemDB.rawQuery(query, condiciones);
			
			return mycursor;
		} catch (SQLiteException e) {
			Log.e(TAG, e.getLocalizedMessage());
			return null;
		}

	}

	// Recupera el CipherKey de Enc
	public String getCipherKey() {
		Cursor mycursor;
		String[] campos = new String[] { "PRMVALUE" };
		String[] args = new String[] { "AUXK" };
		String sResult = "";
		String sGetValue = "";
		String sAuxiliar = "";
		int iPos = 0;
		int iSpaces = 0;

		try {
			open();
			mycursor = queryBD(sTablaParams, campos, "PRMTYPE=?", args, null);

			// Nos aseguramos de que existe al menos un registro
			if (mycursor.moveToFirst()) {
				// Recorremos el cursor hasta que no haya mÃ¡s registros
				do {
					sResult = mycursor.getString(0);
				} while (mycursor.moveToNext());
			}
			mycursor.close();
			close();
			sAuxiliar = sResult;
			// Mientras siga estando mas grande la cadena
			while (sAuxiliar.length() >= iPos) {
				iPos = iPos + 2;
				iSpaces = iPos + 1;
				sGetValue += sAuxiliar.substring(iPos, iPos + 1);
				sAuxiliar = sAuxiliar.substring(iSpaces);
			}

			// Log.d(sSrc, "Mi llave es: " + sGetValue);
			return sGetValue;
		} catch (Exception e) {
			setErrorCode(-1);
			setErrorDescription(e.getMessage());
			e.printStackTrace();
			close();
			return "";
		}

	}

	/******************************************************************************
	 * @author Eduardo Garcia
	 * @param user
	 * @param name
	 * @param email
	 * @descripcion: Marca como activa la terminal en la BD y almacena el
	 *               usuario
	 * @return false si hubo algun error
	 *****************************************************************************/
	public boolean registerTerminal(String user, String name, String email) {
		 Log.d(TAG, "== registerTerminal() ==");
		cleanErrorAttributes();

		String[] campos = new String[] { "USRID", "USRNAME", "USREMAIL" };
		String[] valores = new String[] { user, name, email };

		boolean result = false;

		try {
			if (insertReg(sTablaUsers, campos, valores)) {
				if (updateStatus(ACTIVE_APP) == 1) {
					result = true;
				} else {
					setErrorCode(-1);
					setErrorDescription(EMPTY_STRING);
					result = false;
				}
			} else {
				setErrorCode(-1);
				setErrorDescription(EMPTY_STRING);
				result = false;
			}
		} catch (Exception e) {
			setErrorCode(-1);
			setErrorDescription(e.getMessage());
			result = false;
		} finally {
			close();
		}
		
		 Log.d(TAG, "--> result : " + result);
		
		return result;
	}

	public int insertMenuOptions(String[] menuOptions, String user) {
		String[] args = new String[] { user };
		String[] campos = new String[] { "USRID", "IDMENU" };

		cleanErrorAttributes();
		
		 {
			System.out.println("insertMenuOptions()");
			System.out.println("El usuario es: " + user);
		}
		
		try {
			 System.out.println("== borrando MenuOptions existentes");
			deleteReg(USERMENU_TABLE, "USRID=?", args);
			
			for (String menuOption : menuOptions){
				args = new String[] {user, menuOption};
				
				if (!insertReg(USERMENU_TABLE, campos, args)) {
					setErrorCode(1);
					setErrorDescription("No inserto el registro " + args.toString());
				}
				
				 Log.d(TAG, "Insertado: " + args.toString());				
			}
		} catch (Exception e) {
			setErrorDescription(e.getMessage());
			setErrorCode(2);
		}

		return getiError();

	}

	private int updateStatus(String status) {
		String[] campos = new String[] { "PRMVALUE" };
		String[] valores = new String[] { status };
		String[] arguments = new String[] { "POS_ACTIVE" };
		int resultado;

		resultado = updateRegs(sTablaParams, campos, valores, "PRMTYPE=?", arguments);
		return resultado;
	}

	private void cleanErrorAttributes() {
		setErrorCode(0);
		setErrorDescription(EMPTY_STRING);
	}

	// Intenta abrir la base de datos
	public void open() throws SQLException {
		try {
			// Intenta abrir la base de datos
			systemDB = getWritableDatabase();
			Log.d(TAG, "Abre la base de datos");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}

	}

	public void beginTran() throws SQLException {
		try {
			systemDB.beginTransaction();
			Log.d(TAG, "Inicia una transacciÃ³n en la BD");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	public void succesfulTran() throws SQLException {
		try {
			systemDB.setTransactionSuccessful();
			Log.d(TAG, "Marca como exitosa la transaccion en la base de datos");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	public void endTran() throws SQLException {
		try {
			systemDB.endTransaction();
			Log.d(TAG, "finaliza la transaccion en la base de datos");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void close() {
		if (systemDB != null)
			systemDB.close();

		Log.d(TAG, "Cierra la base de datos");
		super.close();
		// Log.d(sSrc, "Ya cerrÃ© la base de datos");
	}

	private Cursor queryBD(String table, String[] campos, String condiciones,
			String[] args, String orderBy) {

		Cursor mycursor;

		try {
			mycursor = systemDB.query(table, campos, condiciones, args, null, null, orderBy);
			return mycursor;
		} catch (SQLiteException e) {
			Log.e(TAG, e.getLocalizedMessage());
		 	return null;
		}

	}

	private int deleteReg(String table, String condiciones, String[] args) {
		try {
			 {
				System.out.println("currentThread locked : " + systemDB.isDbLockedByCurrentThread() );
				System.out.println("otherThread locked : " + systemDB.isDbLockedByOtherThreads() );					
				System.out.println("deleteReg()");							
			}
			
			
			return systemDB.delete(table, condiciones, args);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return -1;
		}
	}

	// Inserta registros en la BD
	private boolean insertReg(String table, String[] myFields, String[] myValues) {

		ContentValues values = new ContentValues();

		try {
			for (int i = 0; i < myFields.length; i++) {
				values.put(myFields[i], myValues[i]);
			}

			return (systemDB.insert(table, null, values) > 0);

		} catch (SQLiteException e) {
			// Log.e(sSrc, e.getLocalizedMessage());
			setErrorCode(-10);
			setErrorDescription(e.getMessage());
			return false;
		}

	}

	public int updateRegs(String table, String[] myFields, String[] myValues,
			String conditions, String[] args) {
		 Log.d(TAG, "== updateRegs() ==");
		
		ContentValues values = new ContentValues();

		try {
			for (int i = 0; i < myFields.length; i++) 
				values.put(myFields[i], myValues[i]);			

			return (systemDB.update(table, values, conditions, args));
		} catch (SQLiteException e) {
			Log.e(TAG, e.getLocalizedMessage());
			
			return -1;
		}
	}

	public int getiError() {
		return errorNumber;
	}

	private void setErrorCode(int errorNumber) {
		this.errorNumber = errorNumber;
	}

	public String getErrorDescription() {
		return sError;
	}

	private void setErrorDescription(String sError) {
		this.sError = sError;
	}

	public String getsPOSId() {
		return sPOSId;
	}

	private void setsPOSId(String sPOSId) {
		this.sPOSId = sPOSId;
	}

	public String getsPOSAlias() {
		return sPOSAlias;
	}

	private void setsPOSAlias(String sPOSAlias) {
		this.sPOSAlias = sPOSAlias;
	}

	public String getsPOSMerchant() {
		return sPOSMerchant;
	}

	private void setsPOSMerchant(String sPOSMerchant) {
		this.sPOSMerchant = sPOSMerchant;
	}

	public String getsPOSRFC() {
		return sPOSRFC;
	}

	private void setsPOSRFC(String sPOSRFC) {
		this.sPOSRFC = sPOSRFC;
	}

	public String getsPOSAddress() {
		return sPOSAddress;
	}

	private void setsPOSAddress(String sPOSAddress) {
		this.sPOSAddress = sPOSAddress;
	}

}