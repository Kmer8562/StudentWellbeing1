package com.example.studentwellbeing.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentwellbeing.Adapter.MyCouncillorAdapter;
import com.example.studentwellbeing.Common.Common;
import com.example.studentwellbeing.Common.SpacesItemDecoration;
import com.example.studentwellbeing.Model.Councillor;
import com.example.studentwellbeing.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BookingStep2Fragment extends Fragment {

    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.recycler_councillor)
    RecyclerView recycler_councillor;

    private BroadcastReceiver councillorDoneReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Councillor> councillorArrayList = intent.getParcelableArrayListExtra(Common.KEY_COUNCILLOR_LOAD_DONE);

            MyCouncillorAdapter adapter = new MyCouncillorAdapter(getContext(),councillorArrayList);
            recycler_councillor.setAdapter(adapter);
            }
    };


    static BookingStep2Fragment instance;

    public static BookingStep2Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep2Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(councillorDoneReciever, new IntentFilter(Common.KEY_COUNCILLOR_LOAD_DONE));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(councillorDoneReciever);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_two,container, false);

        unbinder = ButterKnife.bind(this,itemView);

        initView();

        return itemView;

    }

    private void initView() {
        recycler_councillor.setHasFixedSize(true);
        recycler_councillor.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_councillor.addItemDecoration(new SpacesItemDecoration(4));
    }
}
