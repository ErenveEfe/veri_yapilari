package com.library.model;

import java.time.LocalDate;

public class BorrowHistory {
    private String userId;
    private LocalDate borrowDate;

    public BorrowHistory(String userId, LocalDate borrowDate) {
        this.userId = userId;
        this.borrowDate = borrowDate;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
}
