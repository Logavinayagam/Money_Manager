package com.logavinayagam.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewHistory;
    private List<HistoryEntry> historyList;
    private HistoryAdapter adapter;

    private DatabaseReference userHistoryRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize views
        listViewHistory = findViewById(R.id.listViewHistory);

        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case where the user is not logged in
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the user is not logged in
        }

        userHistoryRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid())
                .child("history");

        // Initialize historyList and adapter
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(this, R.layout.history_entry_item, historyList);

        // Set the adapter for the ListView
        listViewHistory.setAdapter(adapter);

        // Read and display the user's history
        userHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Map the data to a HistoryEntry object
                    HistoryEntry historyEntry = snapshot.getValue(HistoryEntry.class);

                    // Add the history entry to your list
                    historyList.add(historyEntry);
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if necessary
                if (databaseError != null) {
                    Toast.makeText(HistoryActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Navigate back to the MainActivity
        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the HistoryActivity to remove it from the stack
    }
}

