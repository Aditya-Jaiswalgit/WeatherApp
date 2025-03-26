package com.weatherapp.android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.weatherapp.android.Domains.Hourly;
import com.weatherapp.android.R;

import java.util.ArrayList;

public class HourlyAdapters extends RecyclerView.Adapter<HourlyAdapters.ViewHolder> {

    private ArrayList<Hourly> items;

    public HourlyAdapters(ArrayList<Hourly> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_hourly, parent, false); // Replace with your layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hourly hourly = items.get(position);

        holder.hourTextView.setText(hourly.getHour());
        holder.tempTextView.setText(hourly.getTemp() + "°C");
//        holder.conditionTextView.setText(hourly.getCondition());

        // Load the icon using Glide
        Glide.with(holder.itemView.getContext())
                .load("https:" + hourly.getIconUrl()) // Prepend "https:" to the URL
                .into(holder.weatherIcon);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView hourTextView, tempTextView, conditionTextView;
        ImageView weatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hourTextView = itemView.findViewById(R.id.HourTxt); // Replace with your IDs
            tempTextView = itemView.findViewById(R.id.tempTxt);
//            conditionTextView = itemView.findViewById(R.id.tv_currentstate);
            weatherIcon = itemView.findViewById(R.id.pic);
        }
    }
}