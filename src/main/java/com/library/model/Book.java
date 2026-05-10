package com.library.model;

import com.library.logic.BorrowQueue;
import java.util.HashSet;
import java.util.LinkedList;

public class Book {
    private String isbn;
    private String title;
    private String genre;
    private String subGenre;
    private String author;
    private int borrowCount;
    private LinkedList<BorrowHistory> borrowHistory;
    private HashSet<String> uniqueReaders;
    private boolean available;
    private BorrowQueue queue;

    public Book(String isbn, String title, String genre, String subGenre, String author, int borrowCount) {
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.subGenre = subGenre;
        this.author = author;
        this.borrowCount = borrowCount;
        this.borrowHistory = new LinkedList<>();
        this.uniqueReaders = new HashSet<>();
        this.available = true;
        this.queue = new BorrowQueue();
    }

    public String getIsbn() { return isbn; }

    public String getTitle() { return title; }

    public String getGenre() { return genre; }

    public String getSubGenre() { return subGenre; }

    public String getAuthor() { return author; }

    public int getBorrowCount() { return borrowCount; }
    public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }

    public LinkedList<BorrowHistory> getBorrowHistory() { return borrowHistory; }

    public HashSet<String> getUniqueReaders() { return uniqueReaders; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public BorrowQueue getQueue() { return queue; }
}
