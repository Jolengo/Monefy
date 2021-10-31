package com.holarin.monefy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class FlowAdapter extends RecyclerView.Adapter<FlowAdapter.FlowViewHolder> {
    private ArrayList<Flow> mFlows;

    public FlowAdapter(ArrayList<Flow> flows) {
        mFlows = flows;
    }


    @NonNull
    @Override
    public FlowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_item, parent, false);
        FlowViewHolder flowViewHolder = new FlowViewHolder(v);
        return flowViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull FlowViewHolder holder, int position) {
        Flow flow = mFlows.get(position);
        holder.mDescriptionItem.setText(flow.description);
        holder.mDateItem.setText(
                (flow.calendar.get(Calendar.DAY_OF_MONTH) >= 10 ? flow.calendar.get(Calendar.DAY_OF_MONTH) : ("0" + flow.calendar.get(Calendar.DAY_OF_MONTH))) + "."
                + (flow.calendar.get(Calendar.MONTH) >= 10 ? flow.calendar.get(Calendar.MONTH) : "0" + flow.calendar.get(Calendar.MONTH)) + "."
                + flow.calendar.get(Calendar.YEAR));
        holder.mMoneyItem.setText(flow.money + "₽");
    }


    @Override
    public int getItemCount() {
        return mFlows.size();
    }

    public static class FlowViewHolder extends RecyclerView.ViewHolder {
        public TextView mMoneyItem;
        public TextView mDateItem;
        public TextView mDescriptionItem;
        public FlowViewHolder(View itemView) {
            super(itemView);
            mMoneyItem = itemView.findViewById(R.id.moneyItem);
            mDateItem = itemView.findViewById(R.id.dateItem);
            mDescriptionItem = itemView.findViewById(R.id.descriptionItem);
        }
    }
}
