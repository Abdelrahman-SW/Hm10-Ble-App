package com.beapps.starmaxtestproject.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beapps.starmaxtestproject.R;
import com.beapps.starmaxtestproject.domain.DataList;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private ArrayList<DataList> dataList;
    Context context;

    public DataAdapter(ArrayList<DataList> msgDtoList, Context context) {
        this.dataList = msgDtoList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final DataViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final DataList data = this.dataList.get(position);
        holder.Name.setText(data.getValue());
        holder.Order.setText(data.getId());
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_data, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateList(DataList data) {
        dataList.add(data);
        notifyItemChanged(dataList.size()-1);
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Order;

        public DataViewHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.Data);
            Order = itemView.findViewById(R.id.DataOrder);
        }
    }

}
