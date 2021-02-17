package com.example.fingerprintexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import asia.kanopi.fingerscan.Status;
import com.gemalto.wsq.WSQEncoder;

public class MainActivity extends AppCompatActivity {

    ImageView ivFinger;
    TextView tvMessage;
    TextView base64View;
    byte[] img;
    Bitmap bm;
    String convertBase64 = null;
    private static final int SCAN_FINGER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        ivFinger = (ImageView) findViewById(R.id.ivFingerDisplay);
        base64View = (TextView) findViewById(R.id.base64View);
    }

    public void startScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_FINGER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int status;
        String errorMesssage;

        switch (requestCode) {
            case (SCAN_FINGER): {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        tvMessage.setText("Fingerprint captured");
                        img = data.getByteArrayExtra("img");
                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);

//higher-quality encode
                      byte[] wsqData = new WSQEncoder(bm)
                                .setBitrate(WSQEncoder.BITRATE_5_TO_1)
                                .encode();
                        convertBase64 = Base64.encodeToString(wsqData, Base64.DEFAULT);

                        base64View.setText(convertBase64); //Data Import
                        ivFinger.setImageBitmap(bm); //Data Import
                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        tvMessage.setText("-- Error: " + errorMesssage + " --");
                    }
                }
                break;
            }
        }
    }
}