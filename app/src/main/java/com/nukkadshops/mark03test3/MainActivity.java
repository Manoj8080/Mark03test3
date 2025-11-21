//package com.nukkadshops.mark03test3;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.nukkadshops.mark03.models.*;
//import com.nukkadshops.mark03.sdk.PaymentConfig;
//import com.nukkadshops.mark03.viewmodel.PaymentViewModel;
//import com.nukkadshops.mark03.viewmodel.VoidViewModel;
//
//public class MainActivity extends AppCompatActivity {
//
//    EditText amountEt, txnIdEt;
//    private long savedPtr = 0;
//
//    Button uploadBtn, statusBtn, cancelBtn, voidBtn;
//    TextView resultTv;
//
//    PaymentViewModel paymentViewModel;
//    VoidViewModel voidViewModel;
//
//    private static final String TAG = "MARK03_TESTER";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        amountEt = findViewById(R.id.amountEt);
//        txnIdEt = findViewById(R.id.txnIdEt);
//        uploadBtn = findViewById(R.id.uploadBtn);
//        statusBtn = findViewById(R.id.statusBtn);
//        cancelBtn = findViewById(R.id.cancelBtn);
//        voidBtn = findViewById(R.id.voidBtn);
//        resultTv = findViewById(R.id.resultTv);
//
//        PaymentConfig config = new PaymentConfig(
//                "https://342b7f8d460e.ngrok-free.app/",
//                29610,
//                1013483,
//                "1221258",
//                "a4c9741b-2889-47b8-be2f-ba42081a246e",
//                "tony",
//                3
//        );
//
//        PayFactory factory = new PayFactory(config);
//
//        paymentViewModel = new ViewModelProvider(this, factory)
//                .get(PaymentViewModel.class);
//
//        voidViewModel = new ViewModelProvider(this, factory)
//                .get(VoidViewModel.class);
//
//        observeData();
//
//        uploadBtn.setOnClickListener(v -> {
//            Log.d(TAG, "UPLOAD button clicked");
//
//            UploadRequest req = new UploadRequest(
//                    txnIdEt.getText().toString(),
//                    1,
//                    "10",
//                    amountEt.getText().toString(),
//                    config.userId,
//                    config.merchantId,
//                    config.securityToken,
//                    config.storeId,
//                    config.clientId,
//                    3
//            );
//
//            Toast.makeText(this, "Uploading Payment...", Toast.LENGTH_SHORT).show();
//            paymentViewModel.uploadPayment(req);
//        });
//
//        statusBtn.setOnClickListener(v -> {
//            Log.d(TAG, "STATUS button clicked");
//
//            StatusRequest req = new StatusRequest(
//                    config.merchantId,
//                    config.securityToken,
//                    config.storeId,
//                    config.clientId,
//                    savedPtr
//            );
//
//            Toast.makeText(this, "Checking Status...", Toast.LENGTH_SHORT).show();
//            paymentViewModel.checkStatus(req);
//        });
//
//        cancelBtn.setOnClickListener(v -> {
//            Log.d(TAG, "CANCEL button clicked");
//
//            CancelRequest req = new CancelRequest(
//                    config.storeId,
//                    config.clientId,
//                    config.merchantId,
//                    config.securityToken,
//                    savedPtr,
//                    amountEt.getText().toString(),
//                    true
//            );
//
//            Toast.makeText(this, "Cancelling Payment...", Toast.LENGTH_SHORT).show();
//            paymentViewModel.cancelPayment(req);
//        });
//
//        voidBtn.setOnClickListener(v -> {
//            Log.d(TAG, "VOID button clicked");
//
//            VoidRequest req = new VoidRequest(
//                    txnIdEt.getText().toString(),
//                    1,
//                    "10",
//                    config.storeId,
//                    amountEt.getText().toString(),
//                    config.merchantId,
//                    config.securityToken,
//                    config.clientId,
//                    1,
//                    savedPtr,
//                    config.timeout
//            );
//
//            Toast.makeText(this, "Voiding Payment...", Toast.LENGTH_SHORT).show();
//            voidViewModel.voidPayment(req);
//        });
//    }
//
//    private void observeData() {
//
//        paymentViewModel.ptr.observe(this, p -> {
//            savedPtr = p;
//            Log.d(TAG, "PTR updated: " + p);
//            Toast.makeText(this, "PTR Saved: " + p, Toast.LENGTH_SHORT).show();
//        });
//
//        paymentViewModel.statusResponse.observe(this, res -> {
//            Log.d(TAG, "STATUS Response: " + res);
//            resultTv.setText("Status Response:\n" + res);
//            Toast.makeText(this, "Status Received", Toast.LENGTH_SHORT).show();
//        });
//
//        paymentViewModel.cancelResponse.observe(this, res -> {
//            Log.d(TAG, "CANCEL Response: " + res);
//            resultTv.setText("Cancel Response:\n" + res);
//            Toast.makeText(this, "Cancel Completed", Toast.LENGTH_SHORT).show();
//        });
//
//        voidViewModel.res1.observe(this, res -> {
//            Log.d(TAG, "VOID Response: " + res);
//            resultTv.setText("Void Response:\n" + res);
//            Toast.makeText(this, "Void Completed", Toast.LENGTH_SHORT).show();
//        });
//    }
//}
package com.nukkadshops.mark03test3;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nukkadshops.mark03.models.*;
import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.viewmodel.PaymentViewModel;
import com.nukkadshops.mark03.viewmodel.VoidViewModel;

public class MainActivity extends AppCompatActivity {

    EditText amountEt, txnIdEt;
    private long savedPtr = 0;

    Button uploadBtn, statusBtn, cancelBtn, voidBtn;
    TextView resultTv;

    PaymentViewModel paymentViewModel;
    VoidViewModel voidViewModel;

    private static final String TAG = "MARK03_TESTER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountEt = findViewById(R.id.amountEt);
        txnIdEt = findViewById(R.id.txnIdEt);
        uploadBtn = findViewById(R.id.uploadBtn);
        statusBtn = findViewById(R.id.statusBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        voidBtn = findViewById(R.id.voidBtn);
        resultTv = findViewById(R.id.resultTv);

        PaymentConfig config = new PaymentConfig(
                "https://342b7f8d460e.ngrok-free.app/",
                29610,
                1013483,
                "1221258",
                "a4c9741b-2889-47b8-be2f-ba42081a246e",
                "tony",
                3
        );

        PayFactory factory = new PayFactory(config);

        paymentViewModel = new ViewModelProvider(this, factory)
                .get(PaymentViewModel.class);

        voidViewModel = new ViewModelProvider(this, factory)
                .get(VoidViewModel.class);

        observeData();
        setupButtons(config);
    }

    private void setupButtons(PaymentConfig config) {

        uploadBtn.setOnClickListener(v -> {
            Log.d(TAG, "UPLOAD button clicked");

            UploadRequest req = new UploadRequest(
                    txnIdEt.getText().toString(),
                    1,
                    "1",
                    amountEt.getText().toString(),
                    config.userId,
                    config.merchantId,
                    config.securityToken,
                    config.storeId,
                    config.clientId,
                    config.timeout   // AutoCancelDurationInMinutes
            );

            Toast.makeText(this, "Uploading Payment...", Toast.LENGTH_SHORT).show();
            paymentViewModel.uploadPayment(req);
        });

        statusBtn.setOnClickListener(v -> {
            Log.d(TAG, "STATUS button clicked");

            StatusRequest req = new StatusRequest(
                    config.merchantId,
                    config.securityToken,
                    config.storeId,
                    config.clientId,
                    savedPtr
            );

            Toast.makeText(this, "Checking Status...", Toast.LENGTH_SHORT).show();
            paymentViewModel.checkStatus(req);
        });

        cancelBtn.setOnClickListener(v -> {
            Log.d(TAG, "CANCEL button clicked");

            CancelRequest req = new CancelRequest(
                    config.storeId,
                    config.clientId,
                    config.merchantId,
                    config.securityToken,
                    savedPtr,
                    amountEt.getText().toString(),
                    true
            );

            Toast.makeText(this, "Cancelling Payment...", Toast.LENGTH_SHORT).show();
            paymentViewModel.cancelPayment(req);
        });

        voidBtn.setOnClickListener(v -> {
            Log.d(TAG, "VOID button clicked");

            VoidRequest req = new VoidRequest(
                    txnIdEt.getText().toString(),   // TransactionNumber
                    1,                              // SequenceNumber
                    "1",                            // AllowedPaymentMode
                    amountEt.getText().toString(),  // Amount
                    config.userId,                  // UserID
                    config.merchantId,              // MerchantID
                    config.securityToken,           // SecurityToken
                    config.storeId,                 // StoreID
                    config.clientId,                // Clientid
                    1,                              // TxnType
                    savedPtr                        // Original PTR
            );


            Toast.makeText(this, "Voiding Payment...", Toast.LENGTH_SHORT).show();
            voidViewModel.voidPayment(req);
        });
    }

    private void observeData() {

        paymentViewModel.ptr.observe(this, p -> {
            savedPtr = p;
            Log.d(TAG, "PTR updated: " + p);
            Toast.makeText(this, "PTR Saved: " + p, Toast.LENGTH_SHORT).show();
        });

        paymentViewModel.uploadResponse.observe(this, res -> {
            Log.d(TAG, "UPLOAD Response: " + res);
            resultTv.setText("Upload Response:\n" + res);
        });

        paymentViewModel.statusResponse.observe(this, res -> {
            Log.d(TAG, "STATUS Response: " + res);
            resultTv.setText("Status Response:\n" + res);
            Toast.makeText(this, "Status Received", Toast.LENGTH_SHORT).show();
        });

        paymentViewModel.cancelResponse.observe(this, res -> {
            Log.d(TAG, "CANCEL Response: " + res);
            resultTv.setText("Cancel Response:\n" + res);
            Toast.makeText(this, "Cancel Completed", Toast.LENGTH_SHORT).show();
        });

        voidViewModel.res1.observe(this, res -> {
            Log.d(TAG, "VOID Response: " + res);
            resultTv.setText("Void Response:\n" + res);
            Toast.makeText(this, "Void Completed", Toast.LENGTH_SHORT).show();
        });
    }
}
