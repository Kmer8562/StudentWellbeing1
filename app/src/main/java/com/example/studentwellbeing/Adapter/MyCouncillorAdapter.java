package com.example.studentwellbeing.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.studentwellbeing.Model.Councillor;
import com.example.studentwellbeing.R;

import java.util.ArrayList;
import java.util.List;

public class MyCouncillorAdapter extends RecyclerView.Adapter<MyCouncillorAdapter.MyViewHolder> {

    Context context;
    List<Councillor> councillorList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyCouncillorAdapter(Context context, List<Councillor> councillorList) {
        this.context = context;
        this.councillorList = councillorList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_councillor, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_councillor_name.setText(councillorList.get(i).getName());
        if(!cardViewList.contains(myViewHolder.card_councillor))
            cardViewList.add(myViewHolder.card_councillor);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for(CardView cardView : cardViewList)
                {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));
                }

                myViewHolder.card_councillor.setCardBackgroundColor(
                        context.getResources()
                        .getColor(android.R.color.holo_blue_dark)
                );

                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_COUNCILLOR_SELECTED,councillorList.get(pos));
                intent.putExtra(Common.KEY_STEP, 2);
                localBroadcastManager.sendBroadcast(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return councillorList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_councillor_name;
        CardView card_councillor;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_councillor =(CardView) itemView.findViewById(R.id.card_councillor);
            txt_councillor_name = (TextView)itemView.findViewById((R.id.txt_councillor_name));

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
