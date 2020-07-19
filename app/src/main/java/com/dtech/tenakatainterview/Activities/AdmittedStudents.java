package com.dtech.tenakatainterview.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
                    canvas.drawText("Tenakata Recruitment", pageWidth/2, 270, titlePaint);

                    myPaint.setColor(Color.rgb(0,113,180));
                    myPaint.setTextSize(30f);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText("Email: jobs@tenakata.com", 1160,40, myPaint);
                    canvas.drawText("mobile@tenakata.com", 1160,80, myPaint);

                    titlePaint.setTextAlign(Paint.Align.CENTER);
                    titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                    titlePaint.setTextSize(70);
                    canvas.drawText("Admitted students", pageWidth/2, 500, titlePaint);

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
                    canvas.drawLine(680, 790,680,840, myPaint);
                    canvas.drawLine(880, 790,880,840, myPaint);
                    canvas.drawLine(1030, 790,10300,840, myPaint);

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

                    Toast.makeText(AdmittedStudents.this, "Done", Toast.LENGTH_SHORT).show();


                }else {

                    Toast.makeText(AdmittedStudents.this, "You cannot generate a pdf without any data", Toast.LENGTH_SHORT).show();

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

    private void fetchData() {

//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("Tenakata_db");
//
//        FirebaseRecyclerOptions<User_Pojo> options = new FirebaseRecyclerOptions.Builder<User_Pojo>().setQuery(query, new SnapshotParser<User_Pojo>() {
//            @NonNull
//            @Override
//            public User_Pojo parseSnapshot(@NonNull DataSnapshot snapshot) {
//                return new User_Pojo(
//
//                        snapshot.child("name").getValue().toString(),
//                        snapshot.child("age").getValue().toString(),
//                        snapshot.child("marital_status").getValue().toString(),
//                        snapshot.child("photo_url").getValue().toString(),
//                        snapshot.child("height").getValue().toString(),
//                        snapshot.child("latitude").getValue().toString(),
//                        snapshot.child("longitude").getValue().toString(),
//                        snapshot.child("iq_rating").getValue().toString(),
//                        snapshot.child("gender").getValue().toString()
//                );
//            }
//        }).build();
//
//        adapter = new FirebaseRecyclerAdapter<User_Pojo, ViewHolder>(options) {
//            @Override
//            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.user_pojo_items, parent, false);
//
//                return new ViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(ViewHolder holder, final int position, User_Pojo model) {
//                holder.setName(model.getName());
//                holder.setGender(model.getGender());
//                holder.setPhoto(model.getPhoto_url());
//
//                final String listPostKey = getRef(position).getKey();
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//
//
//                    }
//                });
//            }
//
//        };
//
//
//        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView tvName;
        public TextView tvGender;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView1);
            tvName = itemView.findViewById(R.id.tvName);
            tvGender = itemView.findViewById(R.id.tvGender);
        }

        public void setName(String string) {
            tvName.setText(string);
        }


        public void setGender(String string) {
            tvGender.setText(string);
        }

        public void setPhoto(final String uri) {

            Picasso.get().load(uri)
                    .placeholder(R.drawable.ic_action_image)
                    .transform(new CircleTransform())
                    .error(R.drawable.ic_action_descr).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                    Picasso.get().load(uri).into(imageView);
                }
            });


        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }


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
