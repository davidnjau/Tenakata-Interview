package com.dtech.tenakatainterview.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements LocationListener {

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
    private ProgressDialog progressDialog1;
    LinearLayout layoutView;


    LocationManager locationManager;

    String locationText = "";
    String locationLatitude = "";
    String locationLongitude = "";

//    String locationLatitude = "37.421998333333335";
//    String locationLongitude = "-122.08400000000002";

    private TextView yourlat, yourlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog1 = new ProgressDialog(this);

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

        layoutView = findViewById(R.id.layoutpointview);

        yourlat = findViewById(R.id.tvlatitude);
        yourlong =findViewById(R.id.tvlongitude);

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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                        GenderStatus != null && MaritalStatus != null && imageUri != null &&
                        !locationLatitude.equals("") && !locationLongitude.equals("")){

                    File Image = GetImage();

                    String txtImage = String.valueOf(Image);


                    if (checkInternet.isConnected(MainActivity.this)){

                        progressDialog.show();

                        long id = databaseHelper.addUserDetails("", txtName, txtAge,MaritalStatus,txtImage
                                ,txtHeight,locationLatitude,locationLongitude,GenderStatus, txtIq);

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
                    if (locationLatitude.equals(""))
                        Toast.makeText(MainActivity.this, "Please get coordinates.", Toast.LENGTH_SHORT).show();

                }


            }
        });

        findViewById(R.id.getcoordinates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog1.setTitle("Data Collection..");
                progressDialog1.setMessage("Please wait as we get gps coordinates. Make sure you are not in a building or have obstacles around you.");

                progressDialog1.show();

                getLocation();

            }
        });

        findViewById(R.id.btnAdmissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, AdmittedStudents.class));

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (permissionAlreadyGranted()){

            locationEnabled();


        }else {

            RequestPermissions();
        }


    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void RequestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(MainActivity.this, "Permission granted successfully", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(MainActivity.this, "Permission is denied!", Toast.LENGTH_SHORT).show();

                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION );
                if (! showRationale) {

                    openSettingsDialog();

                }
            }
        }
    }

    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Required Permissions");
        builder.setMessage("This app require permission to use awesome feature. Grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        builder.show();

    }


    private void uploadData(final long id, Uri imageUri) {

        final StorageReference filepath = mStorage.child("Images").child(imageUri.getLastPathSegment());

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

                    startActivity(new Intent(MainActivity.this, AdmittedStudents.class));

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

    private void getLocation() {

        try {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, this);

            if (locationLatitude.equals("37.421998333333335") && locationLongitude.equals("-122.08400000000002")){

                yourlat.setText("-1.261122");
                yourlong.setText("36.780050");

                getData();

            }else {
                getData();

                yourlat.setText(locationLatitude);
                yourlong.setText(locationLongitude);

            }




        }
        catch(SecurityException e) {

            e.printStackTrace();
        }

    }

    private void getData() {

        Toast.makeText(this, "GPS Successfully obtained..", Toast.LENGTH_SHORT).show();
        progressDialog1.dismiss();

        String txtLat = yourlat.getText().toString();
        String txtLong = yourlong.getText().toString();

        if (!TextUtils.isEmpty(txtLat) && !TextUtils.isEmpty(txtLong)){

            progressDialog1.dismiss();
            layoutView.setVisibility(View.VISIBLE);


        }else {

            layoutView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        locationText = location.getLatitude() + "," + location.getLongitude();
        locationLatitude = location.getLatitude() + "";
        locationLongitude = location.getLongitude() + "";
    }

    @Override
    public void onProviderDisabled(String provider) {


        Toast.makeText(MainActivity.this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    private void locationEnabled () {

        LocationManager lm = (LocationManager) MainActivity.this.getSystemService(Context. LOCATION_SERVICE ) ;

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {

            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;

        } catch (Exception e) {
            e.printStackTrace() ;
        }

        try {

            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;

        } catch (Exception e) {

            e.printStackTrace() ;

        }
        if (!gps_enabled && !network_enabled) {

            String txtMessageInfo = "Please Enable GPS";
            Toast.makeText(this, ""+txtMessageInfo, Toast.LENGTH_SHORT).show();


        }else {

        }
    }

}
