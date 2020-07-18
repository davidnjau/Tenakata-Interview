package com.dtech.tenakatainterview.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dtech.tenakatainterview.DatabaseHelper.DatabaseHelper;
import com.dtech.tenakatainterview.HelperClass.CheckInternet;
import com.dtech.tenakatainterview.HelperClass.EditTextGetText;
import com.dtech.tenakatainterview.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etAge,etHeight, etIQ;
    private RadioGroup radioGroupStatus, radioGroupGender;
    private Button btnSave;
    private EditTextGetText editTextGetText;
    private String MaritalStatus, GenderStatus;
    private DatabaseHelper databaseHelper;
    private ImageButton imgBtn1;
    private ImageView imageView1;
    private static final int SELECT_PHOTO = 1;
    private Uri imageUri;
    private File fileImage;
    private StorageReference mStorage;
    private CheckInternet checkInternet;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        editTextGetText = new EditTextGetText();
        databaseHelper = new DatabaseHelper(this);
        checkInternet = new CheckInternet();

        imgBtn1 = findViewById(R.id.imgBtn1);
        imageView1 = findViewById(R.id.imageView1);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etIQ = findViewById(R.id.etIQ);

        btnSave = findViewById(R.id.btnSave);

        imgBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);

            }
        });

        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        radioGroupGender = findViewById(R.id.radioGroupGender);

        radioGroupStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked){

                    MaritalStatus =  String.valueOf(checkedRadioButton.getText());

                }

            }
        });
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked){

                    GenderStatus =  String.valueOf(checkedRadioButton.getText());

                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Data Saving..");
                progressDialog.setMessage("Please wait as we upload your data.");
                progressDialog.setCanceledOnTouchOutside(false);

                String txtName = editTextGetText.getText(etName);
                String txtAge = editTextGetText.getText(etAge);
                String txtHeight = editTextGetText.getText(etHeight);
                String txtIq = editTextGetText.getText(etIQ);

                if (!TextUtils.isEmpty(txtName) && !TextUtils.isEmpty(txtAge) &&
                        !TextUtils.isEmpty(txtHeight) && !TextUtils.isEmpty(txtIq) &&
                        GenderStatus != null && MaritalStatus != null && imageUri != null){

                    File Image = GetImage();

                    String txtImage = String.valueOf(Image);


                    if (checkInternet.isConnected(MainActivity.this)){

                        progressDialog.show();

                        long id = databaseHelper.addUserDetails(txtName, txtAge,MaritalStatus,txtImage
                                ,txtHeight,"","",GenderStatus);

                        Toast.makeText(MainActivity.this, "Data Saved Successfully. We will upload the data once there is a network connection.", Toast.LENGTH_SHORT).show();

                        uploadData(id, imageUri);

                    }else
                        Toast.makeText(MainActivity.this, "Please Make sure you have a network connection.", Toast.LENGTH_SHORT).show();


                    ClearData(etName);
                    ClearData(etAge);
                    ClearData(etHeight);
                    ClearData(etIQ);


                }else {

                    if (GenderStatus == null)
                        Toast.makeText(MainActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    if (MaritalStatus == null)
                        Toast.makeText(MainActivity.this, "Please select your marital status", Toast.LENGTH_SHORT).show();
                    if (imageUri == null)
                        Toast.makeText(MainActivity.this, "Please select an image before proceeding.", Toast.LENGTH_SHORT).show();

                }


            }
        });

    }

    private void uploadData(final long id, Uri imageUri) {

        final StorageReference filepath = mStorage.child("Buy254_Products_Images").child(imageUri.getLastPathSegment());

        UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("-*-*-", "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("-*-*-", e.toString());
            }
        });

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return filepath.getDownloadUrl();
            }

        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()){


                    String downloadUri = String.valueOf(task.getResult());
                    databaseHelper.updateImageUrl(id, downloadUri);

                    progressDialog.dismiss();

                    Toast.makeText(MainActivity.this, "Data uploaded Successfully.", Toast.LENGTH_SHORT).show();


                }else{

                    Toast.makeText(MainActivity.this, "Upload failed. Please try again..", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private File GetImage() {

        if (imageView1.getDrawable() != null){

            Bitmap bitmap = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
            if (bitmap != null){

                fileImage = new File(Objects.requireNonNull(imageUri.getPath()));

            }

        }else {

            Toast.makeText(this, "Please select an image before proceeding..", Toast.LENGTH_SHORT).show();
        }

        return fileImage;

    }


    private void ClearData(EditText editText){

        editText.setText("");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK) {

                imageUri = data.getData();
                imageView1.setImageURI(imageUri);

            }

        }
    }


}
