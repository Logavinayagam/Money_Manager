package com.logavinayagam.moneymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder> {
    private List<StatsItem> statsItemList;

    public StatsAdapter(List<StatsItem> statsItemList) {
        this.statsItemList = statsItemList;
    }

    @NonNull
    @Override
    public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stats, parent, false);
        return new StatsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        StatsItem statsItem = statsItemList.get(position);
        holder.textViewLabel.setText(statsItem.getLabel());
        holder.textViewIncome.setText("Income: $" + String.format("%.2f", statsItem.getTotalIncome()));
        holder.textViewExpense.setText("Expense: $" + String.format("%.2f", statsItem.getTotalExpense()));
    }

    @Override
    public int getItemCount() {
        return statsItemList.size();
    }

    public static class StatsViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewLabel;
        public TextView textViewIncome;
        public TextView textViewExpense;

        public StatsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLabel = itemView.findViewById(R.id.textViewLabel);
            textViewIncome = itemView.findViewById(R.id.textViewIncome);
            textViewExpense = itemView.findViewById(R.id.textViewExpense);
        }
    }
}
