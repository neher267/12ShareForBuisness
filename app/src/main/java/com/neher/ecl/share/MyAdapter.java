package com.neher.ecl.share;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private ArrayList<CurrentRequest> mGifts;
    private static final String TAG = "MyAdapter"; //type logt



    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView distance;
        public Button des_distance;
        public Button button;

        LinearLayout parentLayout;



        public ViewHolder(View itemView) {
            super(itemView);
            distance = itemView.findViewById(R.id.distance_id);
            des_distance = itemView.findViewById(R.id.des_distance_id);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Log.d(TAG, "onCreateViewHolder() calling");

        Log.d(TAG, "onCreateViewHolder: calling"); //type logd

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);

        return vh;
    }

    public MyAdapter(ArrayList<CurrentRequest> gifts){
        mGifts = gifts;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder() calling");

        CurrentRequest gift = mGifts.get(position);
        //holder.distance.setText("Distance: "+gift.getLatLngDis());
        //holder.des_distance.setText("Destination Distance: "+gift.getDesLatLngDis());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: is calling");
            }
        });
    }

    @Override
    public int getItemCount() {

        return mGifts.size();
    }
}
