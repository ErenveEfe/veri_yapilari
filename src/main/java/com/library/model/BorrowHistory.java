package com.library.model;

import java.time.LocalDate;

public class BorrowHistory {
    private String userId;
    private LocalDate borrowDate;

    public BorrowHistory(String userId, LocalDate borrowDate) {
        this.userId = userId;
        this.borrowDate = borrowDate;
    }

}
