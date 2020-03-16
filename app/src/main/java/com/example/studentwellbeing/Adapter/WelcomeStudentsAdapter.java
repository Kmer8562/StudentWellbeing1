package com.example.studentwellbeing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentwellbeing.Model.Banner;
import com.example.studentwellbeing.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WelcomeStudentsAdapter extends RecyclerView.Adapter<WelcomeStudentsAdapter.MyViewHolder> {


    Context context;
    List<Banner> welcomeStudents;

    public WelcomeStudentsAdapter(Context context, List<Banner> welcomeStudents) {
        this.context = context;
        this.welcomeStudents = welcomeStudents;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_welcome_students, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Picasso.get().load(welcomeStudents.get(i).getImage()).into(myViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return welcomeStudents.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById((R.id.image_welcome_students));
        }
    }
}
