 package com.example.fingerprintexample;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.gemalto.wsq.WSQEncoder;

import asia.kanopi.fingerscan.Status;
import android.os.Handler;
import asia.kanopi.fingerscan.Fingerprint;


import java.io.ByteArrayOutputStream;

 public class MainActivity extends AppCompatActivity {


    /// ### Declaration of variables ### ///
    ImageView ivFinger;
    TextView tvMessage;
    TextView base64View;
    private TextView tvStatus;
    private TextView tvError;
    byte[] img;
    Bitmap bm;
    String convertBase64 = null;
    private Fingerprint fingerprint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        ivFinger = (ImageView) findViewById(R.id.ivFingerDisplay);
        base64View = (TextView) findViewById(R.id.base64View);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvError = (TextView) findViewById(R.id.tvError);

        fingerprint = new Fingerprint();
    }


    public void startScan(View view) {
        fingerprint.scan(this, printHandler, updateHandler);
    }


     Handler updateHandler = new Handler(Looper.getMainLooper()) {
         @Override
         public void handleMessage(Message msg) {
             int status = msg.getData().getInt("status");
             tvError.setText("Error: ");
             switch (status) {
                 case Status.INITIALISED:
                     tvStatus.setText("Status: " +"Setting up reader");
                     break;
                 case Status.SCANNER_POWERED_ON:
                     tvStatus.setText("Status: " +"Reader powered on");
                     break;
                 case Status.READY_TO_SCAN:
                     tvStatus.setText("Status: " +"Ready to scan finger");
                     break;
                 case Status.FINGER_DETECTED:
                     tvStatus.setText("Status: " +"Finger detected");
                     break;
                 case Status.RECEIVING_IMAGE:
                     tvStatus.setText("Status: " +"Receiving image");
                     break;
                 case Status.FINGER_LIFTED:
                     tvStatus.setText("Status: " +"Finger has been lifted off reader");
                     break;
                 case Status.SCANNER_POWERED_OFF:
                     tvStatus.setText("Status: " +"Reader is off");
                     break;
                 case Status.SUCCESS:
                     tvStatus.setText("Status: " +"Fingerprint successfully captured");
                     break;
                 case Status.ERROR:
                     tvStatus.setText("Status: " +"Error");
                     tvError.setText("Error: " +msg.getData().getString("errorMessage"));
                     break;
                 default:
                     tvStatus.setText("Status: " +String.valueOf(status));
                     tvError.setText("Error: " +msg.getData().getString("errorMessage"));
                     break;
             }
         }
     };


     Handler printHandler = new Handler(Looper.getMainLooper()) {
         @Override
         public void handleMessage(Message msg) {

             String errorMessage = "empty";
             int status = msg.getData().getInt("status");

             if (status == Status.SUCCESS) {
                 tvMessage.setText("Message: " +"Fingerprint captured");

                 img = msg.getData().getByteArray("img");
                 bm = BitmapFactory.decodeByteArray(img, 0, img.length);

                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                 byte[] byteArray = byteArrayOutputStream .toByteArray();

                 /// ### encoded variable contain the base64 of the fingerprint ### ///
                 String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                 /// ### Higher-quality Encode - imageAsBytes is important for display Image of the fingerprint ### ///
                 byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);

                 /// ### wsqData is optional data for codification in WSQ ### ///
                 byte[] wsqData = new WSQEncoder(bm)
                         .setBitrate(WSQEncoder.BITRATE_5_TO_1)
                         .encode();
                 convertBase64 = Base64.encodeToString(wsqData, Base64.DEFAULT);

                 /// ### Display data in layout ### ///
                 base64View.setText("Base64: " + encoded);
                 ivFinger.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

             } else {
                 errorMessage = msg.getData().getString("errorMessage");
                 tvMessage.setText("-- Error: " + errorMessage + " --");
             }
         }
     };
}