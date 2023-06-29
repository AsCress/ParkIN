package com.example.parkin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.MyViewHolder> {

    Context context;
    ArrayList<ParkingModel> arrayList;

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameView;
        TextView distanceView;

        public MyViewHolder(final View itemView) {
            super(itemView);

            this.nameView = (TextView)itemView.findViewById(R.id.parking_name);
            this.distanceView = (TextView) itemView.findViewById(R.id.parking_distance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public ParkingAdapter(Context context, ArrayList<ParkingModel> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TextView name;
        TextView distance;

        name = holder.nameView;
        distance = holder.distanceView;

        name.setText(arrayList.get(position).getPlaceName());
        distance.setText(arrayList.get(position).getPlaceDistance()+"m");

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
