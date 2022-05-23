# ReaderDemo

----

# KashPay Guía del programador

- TOC
{:toc}


# Change List

Version | Autor               | Fecha      | Descripcion
--------|---------------------|------------|----------------
1.0     | Emilio Betancourt   | 2018-05-01 | Version inicial
1.1     | Judá Escalera       | 2020-10-19 | UX

# Introducción

La solución KashPay posibilita realizar cobros con tarjeta bancarias. Esto se realiza a través de conectar un dispositivo lector de tarjetas, a continuación se describe cómo hacer la integración 
en la aplicación del cliente. El presente documento, tienen como finalidad mostrar cómo puede hacerse esta integración. 


# Modelo de programación

Todos los métodos provistos por el SDK KashPay se pueden considerar en una de las siguientes categorias:
1. Métodos de inicialización；
2. Métodos de interacción；
3. Métodos de notificación (listeners).

# Interface de programación

## Configuración

Procedimiento

Declarar el token de uso del lector en el archivo [gradle.properties](/gradle.properties) 

```java
    kashPayToken=[Este valor será entregado por KashPay vía correo]
```

Agregar el repositorio donde se encuentra el componente AAR de KashPay, esto debe realizarse en el archivo
[build.gradle](/build.gradle)

```java
        maven { url "https://jitpack.io"
            credentials { username kashPayToken }
        }
```

se deberá agregar la referencia en el archivo [build.gradle](/app/build.gradle) de la **aplicación**

```java
   implementation ('com.github.verasofty:readercore:v1.0.7')
    implementation 'com.github.verasofty:ReaderDSpread:v0.9.03'
```

Estos serían los pasos de configuración que deben llevarse a cabo para el correcto funcionamiento del componente.


## Inicialización

Lo primero a realizar, debe ser la inicialización del lector. Para ello, se deberá ejecutar un código como el siguiente: 


```java
import com.sf.upos.reader.IHALReader;
import com.sf.upos.reader.ReaderMngr;
```

```java
    public static IHALReader reader;
    
    ...
    
    private void initReader() {
        if (reader == null) {
            Log.d(TAG, "reader ==> init---");
            reader = ReaderMngr.getReader(ReaderMngr.HW_DSPREAD_QPOS);
        }
    }
```

## Conexión a un lector

Debido a que, la conexión con el lector es a través de bluetooth. Después de la inicialización, el lector y el smartphone deben
emparejarse, para ello se deberán realizar los siguientes pasos:

Iniciar el escaneo para obtener los lectores compatibles alrededor:

Nota: Este método solicitará una instancia que implemente la interfaz **HALReaderCallback**

```java
        reader.scan(this, this);
```
Una vez iniciado el proceso de escaneo, el sdk notificará a través del método onReaderDetected() los lectores que ha ido encontrado alrededor

```java
        public void onReaderDetected(String readerName) {
           // your code goes here
        }
```
Tipicamente, tras haber recibido esta notificación las aplicaciones muestran en alguna lista los lectores detectados para permitir al usuario que elija el lector que va a usar

Una vez que se ha elegido el lector a utilizar, se deberá invocar el método para conectarse al mismo

```java
    reader.connect(MainActivity.this, this);
```
y el sdk notificará a través del método onReaderConnected que la conexión fue exitosa

```java
    @Override
    public void onReaderConnected() {
          // your code goes here
    }
```

## Leer una tarjeta

Para leer una tarjeta se deberá contar con una instancia en estado conectado. Así como, contar con el monto a cobrar.

Paso 1. Configurar un objeto de la clase **TransactionDataRequest**

```java
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
```

Paso 2. Invocar la ejecución de la transacción

```java
         reader.startTransaction(this, request, 30000, this);
```

Paso 3. Esperar la notificación a través del **callback**
Nota: Se sugiere, si el contexto lo amerita, para fines de UX poner tras esta invocación un dialogo que indique al usuario que se está llevando a cabo una lecura

```java
    @Override
    public void onFinishedTransaction(final TransactionDataResult result) {        
        Log.d(TAG, "== onFinishedTransaction() ==");
        
        if (result.getResponseCode() == 0) {  
            processSuccessTransaction(result);
        } else {
            processError(result);
        }
    }
```
