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
    private String locationInfo;
    private LinkedList<BorrowHistory> borrowHistory;
    private HashSet<String> uniqueReaders;
    private boolean available;
    private BorrowQueue queue;

    public Book(String isbn, String title, String genre, String subGenre, String author, int borrowCount, String locationInfo) {
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.subGenre = subGenre;
        this.author = author;
        this.borrowCount = borrowCount;
        this.locationInfo = locationInfo;
        this.borrowHistory = new LinkedList<>();
        this.uniqueReaders = new HashSet<>();
        this.available = true;
        this.queue = new BorrowQueue();
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getSubGenre() { return subGenre; }
    public void setSubGenre(String subGenre) { this.subGenre = subGenre; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getBorrowCount() { return borrowCount; }
    public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }

    public String getLocationInfo() { return locationInfo; }
    public void setLocationInfo(String locationInfo) { this.locationInfo = locationInfo; }

    public LinkedList<BorrowHistory> getBorrowHistory() { return borrowHistory; }
    public void setBorrowHistory(LinkedList<BorrowHistory> borrowHistory) { this.borrowHistory = borrowHistory; }

    public HashSet<String> getUniqueReaders() { return uniqueReaders; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public BorrowQueue getQueue() { return queue; }
}
