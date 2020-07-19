package com.dtech.tenakatainterview.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtech.tenakatainterview.Activities.AdmittedStudents;
import com.dtech.tenakatainterview.HelperClass.User_Pojo;
import com.dtech.tenakatainterview.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataRecyclerAdapter extends RecyclerView.Adapter<DataRecyclerAdapter.ViewHolder>{

    private Context context;
    private ArrayList<User_Pojo> userPojoArrayList;

    public DataRecyclerAdapter(Context context, ArrayList<User_Pojo> AddedInformationArrayList) {
        this.context = context;
        this.userPojoArrayList = AddedInformationArrayList;

    }


    @NonNull
    @Override
    public DataRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_pojo_items, parent, false);
        DataRecyclerAdapter.ViewHolder holder = new DataRecyclerAdapter.ViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final DataRecyclerAdapter.ViewHolder holder, final int position) {

        final int Iq = Integer.parseInt(userPojoArrayList.get(position).getIq_rating());

        if (Iq > 100){

            final String name = userPojoArrayList.get(position).getName();
            final String gender = userPojoArrayList.get(position).getGender();
            final String uri = userPojoArrayList.get(position).getPhoto_url();

            Picasso.get().load(uri)
                    .placeholder(R.drawable.ic_action_image)
                    .transform(new AdmittedStudents.CircleTransform())
                    .error(R.drawable.ic_action_descr).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                    Picasso.get().load(uri).into(holder.imageView);
                }
            });

            holder.tvName.setText(name);
            holder.tvGender.setText(gender);

        }




    }

    @Override
    public int getItemCount() {
        return userPojoArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvName;
        TextView tvGender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView1);
            tvName = itemView.findViewById(R.id.tvName);
            tvGender = itemView.findViewById(R.id.tvGender);

        }
    }


}
