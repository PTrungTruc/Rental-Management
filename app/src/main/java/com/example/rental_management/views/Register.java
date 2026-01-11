package com.example.rental_management.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.rental_management.R;
import com.example.rental_management.databases.AccountFirestore;
import com.example.rental_management.models.Account;
import com.example.rental_management.others.CloudinaryConfig;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    private ImageView profile_img;
    private EditText txtName, txtAge, txtPhone, txtJob, txtHabit, txtHobby, txtPass, txtOtp;
    private Button btnCamera, btnSend, btnRegister;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private AccountFirestore accountHelper;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        profile_img = findViewById(R.id.profile_img);
        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtPhone = findViewById(R.id.txtPhone);
        txtJob = findViewById(R.id.txtJob);
        txtHabit = findViewById(R.id.txtHabit);
        txtHobby = findViewById(R.id.txtHobby);
        txtPass = findViewById(R.id.txtPass);
        txtOtp = findViewById(R.id.txtOtp);
        btnCamera = findViewById(R.id.btnCamera);

        accountHelper = new AccountFirestore(Register.this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    txtOtp.setText(code);
                }
                Log.d("TESTING_VERIFICATION", "SUCCESS");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("TESTING_VERIFICATION", e.getMessage());
            }
        };

        txtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("VN"));

        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(sendOtp);

        btnRegister = findViewById(R.id.btnEdit);
        btnRegister.setOnClickListener(register);

        btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(v -> {
            capturePicture();
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profile_img.setImageBitmap(imageBitmap);
                }
            }
        });
    }

    private View.OnClickListener sendOtp = view -> {
        String phone = txtPhone.getText().toString();
        txtOtp.setEnabled(true);

        if (phone.isEmpty()) {
            txtPhone.setError("Phone number cannot be empty");
        }
        else try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phone, "VN");
            phone = phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            Log.d("PHONE_NUMBER", phone);

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(mCallbacks)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
        catch (Exception e) {
            Log.d("PHONE_NUMBER", e.getMessage());
        }
    };

    private View.OnClickListener register = view -> {
        EditText[] etGrp = {txtName, txtAge, txtPhone, txtJob, txtHabit, txtHobby, txtPass, txtOtp};
        String[] txtGrp = new String[etGrp.length];
        boolean filled = true;

        for (int i = 0; i < etGrp.length; i++) {
            String txt = etGrp[i].getText().toString();
            if (txt.isEmpty()) {
                etGrp[i].setError("Cannot be left blank");
                filled = false;
            }
            else {
                txtGrp[i] = txt;
            }
        }

        if (txtPhone.length() != 10) {
            txtPhone.setError("Invalid phone number");
        }

        if (filled) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, txtOtp.getText().toString());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            uploadImageToCloudinary(txtGrp);
//                            accountHelper.createAccount(new Account(
//                                    txtGrp[0],
//                                    txtGrp[1],
//                                    txtGrp[2],
//                                    txtGrp[3],
//                                    txtGrp[4],
//                                    txtGrp[5],
//                                    txtGrp[6]
//                            ));
                        }
                        else {
                            Log.d("TESTING_VERIFICATION", "FAILED");
                        }
                    });
        }
    };

    private void capturePicture(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            //TODO: Do somethings
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        } else {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA },
                    200);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePicture();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToCloudinary(String[] txtGrp) {
        profile_img.setDrawingCacheEnabled(true);
        profile_img.buildDrawingCache();
        Bitmap bitmap = profile_img.getDrawingCache();

        // Chuyển Bitmap thành ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        // Ensure we recycle the Bitmap to avoid memory issues
        bitmap.recycle();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cloudinary cloudinary = CloudinaryConfig.getCloudinaryInstance();

                    // Upload hình ảnh lên Cloudinary
                    Map<String, String> uploadResult = cloudinary.uploader().upload(byteArray, ObjectUtils.emptyMap());

                    // Lấy URL hình ảnh đã upload
                    String imageUrl = uploadResult.get("secure_url");

                    // Xử lý URL (hiển thị, lưu vào Firestore, v.v)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Cloudinary", "Image URL: " + imageUrl);
                            accountHelper.createAccount(new Account(
                                    txtGrp[0],
                                    txtGrp[1],
                                    txtGrp[2],
                                    txtGrp[3],
                                    txtGrp[4],
                                    txtGrp[5],
                                    txtGrp[6],
                                    imageUrl
                            ));
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}