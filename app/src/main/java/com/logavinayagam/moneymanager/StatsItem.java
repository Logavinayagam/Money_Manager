package com.logavinayagam.moneymanager;
public class StatsItem {
    private String label;
    private double totalIncome;
    private double totalExpense;

    public StatsItem(String label, double totalIncome, double totalExpense) {
        this.label = label;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public String getLabel() {
        return label;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }
}
