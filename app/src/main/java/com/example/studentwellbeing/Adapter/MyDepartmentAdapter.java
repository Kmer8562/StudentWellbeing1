package com.example.studentwellbeing.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentwellbeing.Common.Common;
import com.example.studentwellbeing.Interface.IRecyclerItemSelectedListener;
import com.example.studentwellbeing.Model.Department;
import com.example.studentwellbeing.R;

import java.util.ArrayList;
import java.util.List;


public class MyDepartmentAdapter extends RecyclerView.Adapter<MyDepartmentAdapter.MyViewHolder> {

    Context context;
    List<Department> departmentList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyDepartmentAdapter(Context context, List<Department> departmentList) {
        this.context = context;
        this.departmentList = departmentList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_department, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_department_name.setText(departmentList.get(i).getName());
        myViewHolder.txt_department_address.setText(departmentList.get(i).getAddress());
        if(!cardViewList.contains(myViewHolder.card_department))
            cardViewList.add(myViewHolder.card_department);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //Set all card backgrounds to white
                for(CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                myViewHolder.card_department.setCardBackgroundColor(context.getResources()
                .getColor(android.R.color.holo_blue_dark));

                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_DEPARTMENT_SERVICE,departmentList.get(pos));
                intent.putExtra(Common.KEY_STEP, 1);
                localBroadcastManager.sendBroadcast(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_department_name, txt_department_address;
        CardView card_department;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_department = (CardView) itemView.findViewById(R.id.card_department);
            txt_department_address = (TextView)itemView.findViewById(R.id.txt_department_address);
            txt_department_name = (TextView)itemView.findViewById(R.id.txt_department_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
