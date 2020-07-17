package com.dtech.tenakatainterview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextGetText = new EditTextGetText();
        databaseHelper = new DatabaseHelper(this);

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

                String txtName = editTextGetText.getText(etName);
                String txtAge = editTextGetText.getText(etAge);
                String txtHeight = editTextGetText.getText(etHeight);
                String txtIq = editTextGetText.getText(etIQ);

                if (!TextUtils.isEmpty(txtName) && !TextUtils.isEmpty(txtAge) &&
                        !TextUtils.isEmpty(txtHeight) && !TextUtils.isEmpty(txtIq) &&
                        GenderStatus != null && MaritalStatus != null && imageUri != null){

                    File Image = GetImage();

                    String txtImage = String.valueOf(Image);

                    databaseHelper.addUserDetails(txtName, txtAge,MaritalStatus,txtImage,txtHeight,"",GenderStatus);
                    Toast.makeText(MainActivity.this, "Data Saved Successfully. We will upload the data once there is a network connection.", Toast.LENGTH_SHORT).show();

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
