package com.logavinayagam.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private Spinner spinnerTimePeriod;
    private RecyclerView recyclerViewStats;

    private DatabaseReference userRef;
    private DatabaseReference incomeRef;
    private DatabaseReference expenseRef;

    private StatsAdapter statsAdapter;
    private List<StatsItem> statsItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Initialize Firebase Database reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            incomeRef = userRef.child("income");
            expenseRef = userRef.child("expense");
        }

        textViewTitle = findViewById(R.id.textViewTitle);
        spinnerTimePeriod = findViewById(R.id.spinnerTimePeriod);
        recyclerViewStats = findViewById(R.id.recyclerViewStats);

        // Set the title
        textViewTitle.setText("Stats");

        // Set up the Spinner with options (Year, Month, Week, Day)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimePeriod.setAdapter(adapter);

        // Initialize RecyclerView and Adapter
        recyclerViewStats.setLayoutManager(new LinearLayoutManager(this));
        statsItemList = new ArrayList<>();
        statsAdapter = new StatsAdapter(statsItemList);
        recyclerViewStats.setAdapter(statsAdapter);

        // Set a listener for the Spinner to handle time period selection
        spinnerTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedTimePeriod = adapterView.getItemAtPosition(position).toString();

                // Fetch data and calculate statistics based on the selected time period
                calculateAndDisplayStats(selectedTimePeriod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case when nothing is selected
            }
        });
    }

    // Implement your logic to calculate and display statistics
    private void calculateAndDisplayStats(String selectedTimePeriod) {
        // Clear existing data in the RecyclerView
        statsItemList.clear();

        // Implement your logic to calculate and display statistics
        // Retrieve income and expense data from Firebase based on the selected time period
        // Calculate and display the statistics in the RecyclerView

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve income and expense data from Firebase
                DataSnapshot incomeSnapshot = dataSnapshot.child("income");
                DataSnapshot expenseSnapshot = dataSnapshot.child("expense");

                switch (selectedTimePeriod) {
                    case "Year":
                        calculateYearlyStats(incomeSnapshot, expenseSnapshot);
                        break;
                    case "Month":
                        calculateMonthlyStats(incomeSnapshot, expenseSnapshot);
                        break;
                    case "Week":
                        calculateWeeklyStats(incomeSnapshot, expenseSnapshot);
                        break;
                    case "Day":
                        calculateDailyStats(incomeSnapshot, expenseSnapshot);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors when retrieving data
                // You can show an error message or log the error using databaseError.getMessage()
            }
        };

        userRef.addListenerForSingleValueEvent(valueEventListener);
    }

    // Implement logic to calculate and display yearly statistics
    private void calculateYearlyStats(DataSnapshot incomeSnapshot, DataSnapshot expenseSnapshot) {
        // Iterate through income and expense snapshots to calculate yearly income and expense
        // For example, you can create HashMaps to store income and expense amounts for each year
        // Calculate total income and expense for each year
        // Then, add stats items to the RecyclerView using addStatsItem method

        // Sample code (modify as per your data structure):
        HashMap<String, Double> yearlyIncomeMap = new HashMap<>();
        HashMap<String, Double> yearlyExpenseMap = new HashMap<>();

        for (DataSnapshot snapshot : incomeSnapshot.getChildren()) {
            double income = snapshot.getValue(Double.class);
            long timestamp = Long.parseLong(snapshot.getKey());
            String year = getYearFromTimestamp(timestamp); // Implement getYearFromTimestamp method
            yearlyIncomeMap.put(year, yearlyIncomeMap.getOrDefault(year, 0.0) + income);
        }

        for (DataSnapshot snapshot : expenseSnapshot.getChildren()) {
            double expense = snapshot.getValue(Double.class);
            long timestamp = Long.parseLong(snapshot.getKey());
            String year = getYearFromTimestamp(timestamp); // Implement getYearFromTimestamp method
            yearlyExpenseMap.put(year, yearlyExpenseMap.getOrDefault(year, 0.0) + expense);
        }

        for (Map.Entry<String, Double> entry : yearlyIncomeMap.entrySet()) {
            String year = entry.getKey();
            double income = entry.getValue();
            double expense = yearlyExpenseMap.getOrDefault(year, 0.0);
            addStatsItem(year, income, expense);
        }

        // Sort the statsItemList in ascending order based on year
        Collections.sort(statsItemList, new Comparator<StatsItem>() {
            @Override
            public int compare(StatsItem item1, StatsItem item2) {
                return item1.getLabel().compareTo(item2.getLabel());
            }
        });

        // Notify the adapter to update the RecyclerView
        statsAdapter.notifyDataSetChanged();
    }

    // Implement logic to calculate and display monthly statistics
    private void calculateMonthlyStats(DataSnapshot incomeSnapshot, DataSnapshot expenseSnapshot) {
        // Similar to yearly statistics, but calculate and display monthly income and expense
        // You can create HashMaps to store income and expense amounts for each month
        // Calculate total income and expense for each month
        // Then, add stats items to the RecyclerView using addStatsItem method

        // Sample code (modify as per your data structure):
        HashMap<String, Double> monthlyIncomeMap = new HashMap<>();
        HashMap<String, Double> monthlyExpenseMap = new HashMap<>();

        for (DataSnapshot snapshot : incomeSnapshot.getChildren()) {
            double income = snapshot.getValue(Double.class);
            long timestamp = Long.parseLong(snapshot.getKey());
            String month = getMonthFromTimestamp(timestamp); // Implement getMonthFromTimestamp method
            monthlyIncomeMap.put(month, monthlyIncomeMap.getOrDefault(month, 0.0) + income);
        }

        for (DataSnapshot snapshot : expenseSnapshot.getChildren()) {
            double expense = snapshot.getValue(Double.class);
            long timestamp = Long.parseLong(snapshot.getKey());
            String month = getMonthFromTimestamp(timestamp); // Implement getMonthFromTimestamp method
            monthlyExpenseMap.put(month, monthlyExpenseMap.getOrDefault(month, 0.0) + expense);
        }

        for (Map.Entry<String, Double> entry : monthlyIncomeMap.entrySet()) {
            String month = entry.getKey();
            double income = entry.getValue();
            double expense = monthlyExpenseMap.getOrDefault(month, 0.0);
            addStatsItem(month, income, expense);
        }

        // Sort the statsItemList in ascending order based on month
        Collections.sort(statsItemList, new Comparator<StatsItem>() {
            @Override
            public int compare(StatsItem item1, StatsItem item2) {
                return item1.getLabel().compareTo(item2.getLabel());
            }
        });

        // Notify the adapter to update the RecyclerView
        statsAdapter.notifyDataSetChanged();
    }

    // Implement logic to calculate and display weekly statistics
    private void calculateWeeklyStats(DataSnapshot incomeSnapshot, DataSnapshot expenseSnapshot) {
        // Similar to yearly and monthly statistics, but calculate and display weekly income and expense
        // You can create HashMaps to store income and expense amounts for each week
        // Calculate total income and expense for each week
        // Then, add stats items to the RecyclerView using addStatsItem method

        // Sample code (modify as per your data structure):
        HashMap<String, Double> weeklyIncomeMap = new HashMap<>();
        HashMap<String, Double> weeklyExpenseMap = new HashMap<>();

        for (DataSnapshot snapshot : incomeSnapshot.getChildren()) {
            double income = snapshot.getValue(Double.class);
            long timestamp = Long.parseLong(snapshot.getKey());
            String week = getWeekFromTimestamp(timestamp); // Implement getWeekFromTimestamp method
            weeklyIncomeMap.put(week, weeklyIncomeMap.getOrDefault(week, 0.0) + income);
        }

        for (DataSnapshot snapshot : expenseSnapshot.getChildren()) {
            double expense = snapshot.getValue(Double.class);
            long timestamp = Long.parseLong(snapshot.getKey());
            String week = getWeekFromTimestamp(timestamp); // Implement getWeekFromTimestamp method
            weeklyExpenseMap.put(week, weeklyExpenseMap.getOrDefault(week, 0.0) + expense);
        }

        // Sort weekly stats in descending order by week
        List<Map.Entry<String, Double>> weeklyStatsList = new ArrayList<>(weeklyIncomeMap.entrySet());
        Collections.sort(weeklyStatsList, (entry1, entry2) -> entry2.getKey().compareTo(entry1.getKey()));


        for (Map.Entry<String, Double> entry : weeklyIncomeMap.entrySet()) {
            String week = entry.getKey();
            double income = entry.getValue();
            double expense = weeklyExpenseMap.getOrDefault(week, 0.0);
            addStatsItem(week, income, expense);
        }

        // Sorting code for weekly stats
        /*Collections.sort(statsItemList, new Comparator<StatsItem>() {
            @Override
            public int compare(StatsItem item1, StatsItem item2) {
                // Compare based on the label (week)
                return item1.getLabel().compareTo(item2.getLabel());
            }
        });

// Notify the adapter to update the RecyclerView
        statsAdapter.notifyDataSetChanged();*/


    }

    // Implement logic to calculate and display daily statistics
    private void calculateDailyStats(DataSnapshot incomeSnapshot, DataSnapshot expenseSnapshot) {
        // Similar to yearly, monthly, and weekly statistics, but calculate and display daily income and expense
        // You can create HashMaps to store income and expense amounts for each day
        // Calculate total income and expense for each day
        // Then, add stats items to the RecyclerView using addStatsItem method

        // Sample code (modify as per your data structure):
        HashMap<String, Double> dailyIncomeMap = new HashMap<>();
        HashMap<String, Double> dailyExpenseMap = new HashMap<>();

        for (DataSnapshot snapshot : incomeSnapshot.getChildren()) {
            double income = snapshot.getValue(Double.class);
            long timestamp = Long.parseLong(snapshot.getKey());
            String day = getDayFromTimestamp(timestamp); // Implement getDayFromTimestamp method
            dailyIncomeMap.put(day, dailyIncomeMap.getOrDefault(day, 0.0) + income);
        }

        for (DataSnapshot snapshot : expenseSnapshot.getChildren()) {
            double expense = snapshot.getValue(Double.class);
            long timestamp = Long.parseLong(snapshot.getKey());
            String day = getDayFromTimestamp(timestamp); // Implement getDayFromTimestamp method
            dailyExpenseMap.put(day, dailyExpenseMap.getOrDefault(day, 0.0) + expense);
        }

        // Calculate daily statistics in descending order
        List<Map.Entry<String, Double>> dailyStatsList = new ArrayList<>(dailyIncomeMap.entrySet());
        Collections.sort(dailyStatsList, (entry1, entry2) -> entry2.getKey().compareTo(entry1.getKey()));


        for (Map.Entry<String, Double> entry : dailyIncomeMap.entrySet()) {
            String day = entry.getKey();
            double income = entry.getValue();
            double expense = dailyExpenseMap.getOrDefault(day, 0.0);
            addStatsItem(day, income, expense);
        }

        // Sorting code for daily stats
        /*Collections.sort(statsItemList, new Comparator<StatsItem>() {
            @Override
            public int compare(StatsItem item1, StatsItem item2) {
                // Compare based on the label (day)
                return item1.getLabel().compareTo(item2.getLabel());
            }
        });

        // Notify the adapter to update the RecyclerView
        statsAdapter.notifyDataSetChanged();
        */

    }

    // Helper method to extract year from a timestamp
    private String getYearFromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    // Helper method to extract month from a timestamp
    private String getMonthFromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMMM", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // Helper method to extract week from a timestamp (modify this based on your requirements)
    // Helper method to extract week from a timestamp
    private String getWeekFromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        // Calculate the week of the year
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

        // Return the appropriate week label
        return "Week " + weekOfYear;
    }


    // Helper method to extract day from a timestamp
    private String getDayFromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // Add stats items to the RecyclerView
    private void addStatsItem(String label, double income, double expense) {
        statsItemList.add(new StatsItem(label, income, expense));
        statsAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        // Navigate back to the MainActivity
        Intent intent = new Intent(StatsActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); //
    }


}
