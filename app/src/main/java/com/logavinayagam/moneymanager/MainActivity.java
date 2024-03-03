package com.logavinayagam.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextIncome;
    private EditText editTextExpense;
    private EditText editTextReason;
    private Button btnSubmit;
    private Button btnLogout;
    private Button btnViewHistory;
    private Button btnStats;
    private TextView textViewRemainingBalance;
    private TextView bText;


    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userRef; // Reference to the user's data in Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // User not logged in, handle this (e.g., redirect to LoginActivity)
        }

        // Initialize Firebase Database reference
        userRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        editTextIncome = findViewById(R.id.editTextIncome);
        editTextExpense = findViewById(R.id.editTextExpense);
        editTextReason = findViewById(R.id.editTextReason);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnStats = findViewById(R.id.btnStats);    //(if stats need)
        textViewRemainingBalance = findViewById(R.id.textViewRemainingBalance);
        Toast.makeText(MainActivity.this, "Developed by logavinayagam", Toast.LENGTH_LONG).show();


        //try------


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        }

        textViewRemainingBalance = findViewById(R.id.textViewRemainingBalance);

        // Calculate and display the remaining balance
        calculateAndDisplayRemainingBalance();

        // Set up a TextWatcher for editTextIncome
        editTextIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check if editTextIncome is focused
                if (editTextIncome.isFocused() && editable.length() > 0) {
                    // Update the hint of editTextExpense based on editTextIncome value
                    editTextExpense.setHint("0");
                }
            }
        });

        // Set up a TextWatcher for editTextExpense
        editTextExpense.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check if editTextExpense is focused
                if (editTextExpense.isFocused() && editable.length() > 0) {
                    // Update the hint of editTextIncome based on editTextExpense value
                    editTextIncome.setHint("0");
                }
            }
        });

        // --------
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Step 1: Validate input
                String incomeStr = editTextIncome.getText().toString().trim();
                String expenseStr = editTextExpense.getText().toString().trim();
                String reason = editTextReason.getText().toString().toUpperCase().trim();

                if (incomeStr.isEmpty() && expenseStr.isEmpty()) {
                    // Handle validation error (input fields are empty)
                    Toast.makeText(MainActivity.this, "Income and expense fields must not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    if(incomeStr.isEmpty()){
                        editTextIncome.setText("0");
                        incomeStr="0";
                    }
                    else{
                        editTextExpense.setText("0");
                        expenseStr="0";
                    }
                }

                // Parse input values to doubles
                double income = Double.parseDouble(incomeStr);
                double expense = Double.parseDouble(expenseStr);

                // Step 2: Store income and expense data in Firebase
                DatabaseReference userIncomeRef = userRef.child("income");
                DatabaseReference userExpenseRef = userRef.child("expense");
                DatabaseReference userReasonRef = userRef.child("reason");

                // Push income and expense records to Firebase with a timestamp
                String timestamp = String.valueOf(System.currentTimeMillis());

                userIncomeRef.child(timestamp).setValue(income);
                userExpenseRef.child(timestamp).setValue(expense);

                // Step 3: Calculate remaining balance
                // Retrieve income and expense data from Firebase
                userIncomeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        double totalIncome = 0.0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            totalIncome += snapshot.getValue(Double.class);
                        }

                        // Retrieve expense data from Firebase
                        double finalTotalIncome = totalIncome;
                        userExpenseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                double totalExpense = 0.0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    totalExpense += snapshot.getValue(Double.class);
                                }

                                // Step 4: Calculate remaining balance
                                double remainingBalance = finalTotalIncome - totalExpense;

                                // Format the remaining balance to display only 2 decimal places
                                String formattedBalance = String.format(Locale.getDefault(), "%.2f", remainingBalance);

                                // Update textViewRemainingBalance
                                textViewRemainingBalance.setText("Remaining Balance: $" + formattedBalance);

                                // Remove the ValueEventListener to prevent duplicate updates
                                userIncomeRef.removeEventListener(this);
                                userExpenseRef.removeEventListener(this);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle onCancelled
                                Toast.makeText(MainActivity.this, "Error retrieving expense data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("ExpenseDataError", "Error retrieving expense data: " + databaseError.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle onCancelled
                        Toast.makeText(MainActivity.this, "Error retrieving database: ", Toast.LENGTH_SHORT).show();
                    }
                });

                // Step 4: Add a history entry to Firebase
                DatabaseReference userHistoryRef = userRef.child("history");

                HistoryEntry historyEntry = new HistoryEntry(reason, income-expense, getCurrentDate(), getCurrentTime());
                userHistoryRef.child(timestamp).setValue(historyEntry);

                // Clear input fields
                editTextIncome.setText("");
                editTextExpense.setText("");
                editTextReason.setText("");

                //renaming hint
                editTextIncome.setHint("Enter income");
                editTextExpense.setHint("Enter expense");

            }
        });




        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                // Handle logout here (e.g., redirect to LoginActivity)
                startActivity(new Intent(MainActivity.this, LoginActivity.class)); // Redirect to login
                finish(); // Close the main activity

            }
        });

        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle viewing history (start HistoryActivity)
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)); // Redirect to History
                finish(); // Close the main activity

            }
        });

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle viewing stats (start StatsActivity)
                startActivity(new Intent(MainActivity.this, StatsActivity.class)); // Redirect to Stats
                finish(); // Close the main activity

            }
        });
    }
    // Helper methods to get current date and time
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdf.format(new Date());
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    //try
    private void calculateAndDisplayRemainingBalance() {
        if (userRef != null) {
            userRef.child("income").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double totalIncome = 0.0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        double incomeAmount = snapshot.getValue(Double.class);
                        totalIncome += incomeAmount;
                    }

                    // Retrieve and calculate total expenses
                    double finalTotalIncome = totalIncome;
                    userRef.child("expense").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            double totalExpense = 0.0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                double expenseAmount = snapshot.getValue(Double.class);
                                totalExpense += expenseAmount;
                            }

                            // Calculate remaining balance
                            double remainingBalance = finalTotalIncome - totalExpense;

                            // Format the remaining balance to display only 2 decimal places
                            String formattedBalance = String.format(Locale.getDefault(), "%.2f", remainingBalance);

                            // Display the remaining balance in a TextView
                            textViewRemainingBalance.setText("Remaining Balance: $" + formattedBalance);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors when retrieving expense data
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors when retrieving income data
                }
            });
        }
    }
}
