package com.dtech.tenakatainterview.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
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

    private Button btnSave;
    final ArrayList<User_Pojo> userPojoArrayList1 = new ArrayList<User_Pojo>();
    Bitmap logo, scaleBitmap;
    int pageWidth = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admitted_students);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logo = BitmapFactory.decodeResource(getResources(), R.drawable.tenakata1);
        scaleBitmap = Bitmap.createScaledBitmap(logo, 1200, 518, false);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userPojoArrayList1.size() > 0){

                    startActivity(new Intent(getApplicationContext(), FinalAdmission.class));

                }else {

                    Toast.makeText(AdmittedStudents.this, "Please wait until the data loads", Toast.LENGTH_SHORT).show();

                }


            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tenakata_db");
//        fetchData();
        getData();
    }



    private ArrayList<User_Pojo> getData(){

        userPojoArrayList1.clear();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){

                    User_Pojo user_pojo = new User_Pojo();

                    final int Iq = Integer.parseInt(childSnapshot.child("iq_rating").getValue().toString());
                    final double longitude = (double) childSnapshot.child("longitude").getValue();

                    String name = childSnapshot.child("name").getValue().toString();
                    String age = childSnapshot.child("age").getValue().toString();
                    String photo_url = childSnapshot.child("photo_url").getValue().toString();
                    String iq_rating = childSnapshot.child("iq_rating").getValue().toString();
                    String gender = childSnapshot.child("gender").getValue().toString();

                    if (longitude < 5.33 && longitude > -4.76) {

                        //Check if longitudes are within the Kenyan geographical borders

                        if (Iq > 100) {

                            //Only Display persons with IQ more than 100

                            user_pojo.setName(name);
                            user_pojo.setAge(age);
                            user_pojo.setPhoto_url(photo_url);
                            user_pojo.setGender(gender);
                            user_pojo.setIq_rating(iq_rating);

//                                Collections.sort(userPojoArrayList1, new Comparator<User_Pojo>() {
//                                    @Override
//
//                                    public int compare(User_Pojo o1, User_Pojo o2) {
//
//                                        return o1.getIq_rating().compareTo(o2.getIq_rating());
//                                    }
//                                })
                            userPojoArrayList1.add(user_pojo);

                            dataRecyclerAdapter = new DataRecyclerAdapter(getApplicationContext(), userPojoArrayList1);
                            recyclerView.setAdapter(dataRecyclerAdapter);

                        }

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return userPojoArrayList1;

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
