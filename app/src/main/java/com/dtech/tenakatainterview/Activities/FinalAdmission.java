package com.dtech.tenakatainterview.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dtech.tenakatainterview.Adapter.DataRecyclerAdapter;
import com.dtech.tenakatainterview.DatabaseHelper.DatabaseHelper;
import com.dtech.tenakatainterview.HelperClass.User_Pojo;
import com.dtech.tenakatainterview.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FinalAdmission extends AppCompatActivity {

    RecyclerView recyclerView;

    private DataRecyclerAdapter dataRecyclerAdapter;
    ArrayList<User_Pojo> userPojoArrayList1 = new ArrayList<User_Pojo>();

    private Button btnSave;
    Bitmap logo, scaleBitmap;
    int pageWidth = 1200;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_admission);

        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logo = BitmapFactory.decodeResource(getResources(), R.drawable.tenakata1);
        scaleBitmap = Bitmap.createScaledBitmap(logo, 1200, 518, false);

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
    }

    private void GeneratePdf() {

        if (userPojoArrayList1.size() > 0){

            PdfDocument myPdfDocument = new PdfDocument();
            Paint myPaint = new Paint();
            Paint titlePaint = new Paint();

            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1200, 2010,1).create();
            PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
            Canvas canvas = myPage.getCanvas();

            canvas.drawBitmap(scaleBitmap,0,0,myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(70);
            canvas.drawText("Tenakata Recruitment", pageWidth/2, 600, titlePaint);

            myPaint.setColor(Color.rgb(0,113,180));
            myPaint.setTextSize(30f);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Email: jobs@tenakata.com", 1160,40, myPaint);
            canvas.drawText("mobile@tenakata.com", 1160,80, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
            titlePaint.setTextSize(70);
            canvas.drawText("Admitted students", pageWidth/2, 670, titlePaint);

            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(2);
            canvas.drawRect(20, 780, pageWidth-20, 860, myPaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.FILL);

            canvas.drawText("Name", 40, 830, myPaint);
            canvas.drawText("Age", 200, 830, myPaint);
            canvas.drawText("Gender", 700, 830, myPaint);
            canvas.drawText("IQ rating", 900, 830, myPaint);
            canvas.drawText("Marital Status", 1050, 830, myPaint);

            canvas.drawLine(180, 790,180,840, myPaint);
            canvas.drawLine(280, 790,280,840, myPaint);
            canvas.drawLine(480, 790,480,840, myPaint);
            canvas.drawLine(680, 790,680,840, myPaint);

            //Retrieve Data
            canvas.drawText("Dave", 40, 950, myPaint);
            canvas.drawText("20", 200,950, myPaint);
            canvas.drawText("Male", 700,950, myPaint);
            canvas.drawText("120", 900,950, myPaint);
//                    canvas.drawText("Single", 1050,950, myPaint);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Single", pageWidth-40, 950, myPaint);
            myPaint.setTextAlign(Paint.Align.LEFT);

            myPdfDocument.finishPage(myPage);

            File file = new File(Environment.getExternalStorageDirectory(), "/Hello.pdf");
            try {

                myPdfDocument.writeTo(new FileOutputStream(file));

            } catch (IOException e) {
                e.printStackTrace();
            }

            myPdfDocument.close();

            Toast.makeText(FinalAdmission.this, "Done", Toast.LENGTH_SHORT).show();


        }else {

            Toast.makeText(FinalAdmission.this, "You cannot generate a pdf without any data", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        userPojoArrayList1 = databaseHelper.getAdmittedList();

        dataRecyclerAdapter = new DataRecyclerAdapter(getApplicationContext(), userPojoArrayList1);
        recyclerView.setAdapter(dataRecyclerAdapter);

        if (permissionAlreadyGranted()){


        }else {

            RequestPermissions();
        }


    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(FinalAdmission.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void RequestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(FinalAdmission.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(FinalAdmission.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(FinalAdmission.this, "Permission granted successfully", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(FinalAdmission.this, "Permission is denied!", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
