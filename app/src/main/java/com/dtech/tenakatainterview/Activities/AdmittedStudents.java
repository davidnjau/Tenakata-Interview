package com.dtech.tenakatainterview.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtech.tenakatainterview.Adapter.DataRecyclerAdapter;
import com.dtech.tenakatainterview.DatabaseHelper.DatabaseHelper;
import com.dtech.tenakatainterview.HelperClass.User_Pojo;
import com.dtech.tenakatainterview.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdmittedStudents extends AppCompatActivity {

    RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference mDatabase;

    private ArrayList<User_Pojo> user_pojo_items;
    private DataRecyclerAdapter dataRecyclerAdapter;
    ArrayList<User_Pojo> user_pojoArrayList = new ArrayList<User_Pojo>();

    private Button btnSave;


    private DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    public static final String TABLE_USER_DETAILS = "user_details";
    private static final String KEY_FIREBASE_ID = "key_id";
    Bitmap logo, scaleBitmap;
    int pageWidth = 1200;

    int pageSize = 860;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admitted_students);

        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Please wait. ");
        progressDialog.setMessage("We are loading data from our database");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tenakata_db");
        
        logo = BitmapFactory.decodeResource(getResources(), R.drawable.tenakata1);
        scaleBitmap = Bitmap.createScaledBitmap(logo, 1200, 518, false);

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getReadableDatabase();

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (permissionAlreadyGranted()){

                    GeneratePdf();

                }else {

                    RequestPermissions();
                }


            }
        });


        getData();
    }

    private void getData(){

        final ArrayList<User_Pojo> userPojoArrayList = new ArrayList<User_Pojo>();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){

                    User_Pojo user_pojo = new User_Pojo();


                    final int Iq = Integer.parseInt(childSnapshot.child("iq_rating").getValue().toString());

                    final double longitude = (double) childSnapshot.child("longitude").getValue();
                    double latitude = (double) childSnapshot.child("latitude").getValue();

                    String name = childSnapshot.child("name").getValue().toString();
                    String age = childSnapshot.child("age").getValue().toString();
                    String photo_url = childSnapshot.child("photo_url").getValue().toString();
                    String iq_rating = childSnapshot.child("iq_rating").getValue().toString();
                    String gender = childSnapshot.child("gender").getValue().toString();

                    String height = childSnapshot.child("height").getValue().toString();
                    String marital_status = childSnapshot.child("marital_status").getValue().toString();
                    String key = childSnapshot.getKey();

                    if (longitude < 5.33 && longitude > -4.76) {

                        //Check if longitudes are within the Kenyan geographical borders

                        if (Iq > 100) {

                            //Only Display persons with IQ more than 100

                            user_pojo.setName(name);
                            user_pojo.setAge(age);
                            user_pojo.setPhoto_url(photo_url);
                            user_pojo.setGender(gender);
                            user_pojo.setIq_rating(iq_rating);

                            user_pojo.setLatitude(latitude);
                            user_pojo.setLongitude(longitude);
                            user_pojo.setHeight(height);
                            user_pojo.setMarital_status(marital_status);

                            user_pojo.setKeyId(key);

                            userPojoArrayList.add(user_pojo);

                            AddData(userPojoArrayList);

                        }

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void GeneratePdf() {

        ArrayList<User_Pojo> userPojoArrayList1 = databaseHelper.getAdmittedList();

        if (userPojoArrayList1.size() > 0){

            PdfDocument myPdfDocument = new PdfDocument();
            Paint myPaint = new Paint();
            Paint titlePaint = new Paint();
            Paint contentPaint = new Paint();

            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1200, 2010,1).create();
            PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
            Canvas canvas = myPage.getCanvas();

            canvas.drawBitmap(scaleBitmap,0,0,myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(70);

            canvas.drawText("Tenakata University", pageWidth/2, 600, titlePaint);

            myPaint.setColor(Color.rgb(0,113,180));
            myPaint.setTextSize(30f);
            myPaint.setTextAlign(Paint.Align.RIGHT);

            contentPaint.setColor(Color.WHITE);
            contentPaint.setTextSize(30f);
            contentPaint.setTextAlign(Paint.Align.RIGHT);

            canvas.drawText("Email: jobs@tenakata.com", 1160,40, contentPaint);
            canvas.drawText("mobile@tenakata.com", 1160,80, contentPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTextSize(70);
            canvas.drawText("Admitted students", pageWidth/2, 670, titlePaint);

            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(2);
            canvas.drawRect(20, 780, pageWidth-20, 860, myPaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.FILL);

            canvas.drawText("S.No", 40, 830, myPaint);
            canvas.drawText("Name", 120, 830, myPaint);

            canvas.drawText("Marital Status", 480, 830, myPaint);
            canvas.drawText("Gender", 700, 830, myPaint);
            canvas.drawText("IQ rating", 900, 830, myPaint);
            canvas.drawText("Age", 1050, 830, myPaint);

            canvas.drawLine(110, 790,110,840, myPaint);
            canvas.drawLine(460, 790,460,840, myPaint);

            canvas.drawLine(680, 790,680,840, myPaint);
            canvas.drawLine(880, 790,880,840, myPaint);
            canvas.drawLine(1030, 790,1030,840, myPaint);


            for (int i = 0; i< userPojoArrayList1.size(); i++){

                pageSize = pageSize + 40;

                String name = userPojoArrayList1.get(i).getName();
                String age = userPojoArrayList1.get(i).getAge();
                String maritalStatus = userPojoArrayList1.get(i).getMarital_status();
                String iqRating = userPojoArrayList1.get(i).getIq_rating();
                String gender = userPojoArrayList1.get(i).getGender();

                canvas.drawText((i+1) +".", 40, pageSize, myPaint);
                canvas.drawText(name, 120, pageSize, myPaint);
                canvas.drawText(maritalStatus, 480,pageSize, myPaint);
                canvas.drawText(gender, 700,pageSize, myPaint);
                canvas.drawText(iqRating, 900,pageSize, myPaint);
                canvas.drawText(age, 1050,pageSize, myPaint);

            }
            myPdfDocument.finishPage(myPage);

            File file = new File(Environment.getExternalStorageDirectory(), "/Applicants.pdf");
            try {

                myPdfDocument.writeTo(new FileOutputStream(file));

            } catch (IOException e) {
                e.printStackTrace();
            }

            myPdfDocument.close();

            Toast.makeText(AdmittedStudents.this, "Pdf has been generated and saved as applicants.pdf in your phone.", Toast.LENGTH_LONG).show();


        }else {

            Toast.makeText(AdmittedStudents.this, "You cannot generate a pdf without any data", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        RequestPermissions();

        if (permissionAlreadyGranted()){


        }else {

            RequestPermissions();
        }


    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(AdmittedStudents.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void RequestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(AdmittedStudents.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(AdmittedStudents.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(AdmittedStudents.this, "Permission granted successfully", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(AdmittedStudents.this, "Permission is denied!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void AddData(ArrayList<User_Pojo> userPojoArrayList1) {

        for (int i = 0; i < userPojoArrayList1.size(); i++){

            String name = userPojoArrayList1.get(i).getName();
            String age = userPojoArrayList1.get(i).getAge();
            String maritalStatus = userPojoArrayList1.get(i).getMarital_status();
            String photoUrl = userPojoArrayList1.get(i).getPhoto_url();
            String height = userPojoArrayList1.get(i).getHeight();

            double latitude = userPojoArrayList1.get(i).getLatitude();
            double longitude = userPojoArrayList1.get(i).getLongitude();

            String iqRating = userPojoArrayList1.get(i).getIq_rating();
            String gender = userPojoArrayList1.get(i).getGender();
            String key = userPojoArrayList1.get(i).getKeyId();

            String Lat = String.valueOf(latitude);
            String Long = String.valueOf(longitude);

            Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_USER_DETAILS, null);
            if (cursor != null && cursor.moveToFirst()){

                do {

                    String query = "Select * From "+TABLE_USER_DETAILS +" WHERE "
                            + KEY_FIREBASE_ID + " = '"+key+"'";

                    Cursor cursor1 = db.rawQuery(query, null);

                    try{

                        if(cursor1.getCount() > 0){

                            //No need to add data as all are in SQLite


                        }else {

                            //No data in SQLite but adds
                            databaseHelper.addUserDetails(key, name, age,maritalStatus,photoUrl
                                    ,height,Long,Lat,gender, iqRating);

                        }

                    }catch (Exception e){

                        Log.e("-*-*-* ",e.toString());

                    }finally {
                        if (cursor1 != null){
                            cursor1.close();
                        }
                    }

                }while (cursor.moveToNext());


            }else {

                databaseHelper.addUserDetails(key, name, age,maritalStatus,photoUrl
                        ,height,Long,Lat,gender, iqRating);

            }

            PopulateRecyclerView();


        }



    }

    private void PopulateRecyclerView(){



        user_pojoArrayList = databaseHelper.getAdmittedList();
        if (user_pojoArrayList.size() < 1){

            progressDialog.show();

        }else {

            progressDialog.dismiss();

        }
        dataRecyclerAdapter = new DataRecyclerAdapter(getApplicationContext(), user_pojoArrayList);
        recyclerView.setAdapter(dataRecyclerAdapter);
    }


    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
