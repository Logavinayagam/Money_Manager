package com.logavinayagam.moneymanager;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryEntry> {

    private Context context;
    private int resource;

    public HistoryAdapter(Context context, int resource, List<HistoryEntry> historyList) {
        super(context, resource, historyList);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, null);
        }

        HistoryEntry historyEntry = getItem(position);

        if (historyEntry != null) {
            TextView textViewReason = convertView.findViewById(R.id.textViewReason);
            TextView textViewAmount = convertView.findViewById(R.id.textViewAmount);
            TextView textViewDate = convertView.findViewById(R.id.textViewDate);
            TextView textViewTime = convertView.findViewById(R.id.textViewTime);

            textViewReason.setText("Reason: " + historyEntry.getReason());
            textViewAmount.setText("Amount: $" + historyEntry.getAmount());
            textViewDate.setText("Date: " + historyEntry.getDate());
            textViewTime.setText("Time: " + historyEntry.getTime());
        }

        return convertView;
    }
}
