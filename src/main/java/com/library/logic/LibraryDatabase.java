package com.library.logic;

import com.library.model.Book;
import com.library.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

// tüm veri yapılarını tek bir yerden yönetmek için kullanıyouz
public class LibraryDatabase {
    private HashMap<String, Book> bookMap;
    private HashMap<String, User> userMap;
    private List<Book> sortedBooks;
    
    private BookTree categoryTree;
    private BookBST searchTree;
    private LibraryGraph graph;

    // en popüler 10 kitabı sabit boyutlu dizide tutuyor
    private static final int TOP_N = 10;
    private Book[] topBooksArray = new Book[TOP_N];

    public LibraryDatabase() {
        bookMap = new HashMap<>();
        userMap = new HashMap<>();
        sortedBooks = new ArrayList<>();
        categoryTree = new BookTree();
        searchTree = new BookBST();
        graph = new LibraryGraph();
    }

    // yeni gelen kitabı tüm veri yapılarına aynı anda kaydeder
    public void addBook(Book book) {
        bookMap.put(book.getIsbn(), book);
        sortedBooks.add(book);
        searchTree.insert(book);
        sortBooks();
        categoryTree.addBook(book.getGenre(), book.getSubGenre(), book);
        updateTopBooks();
    }

    public void addUser(User user) {
        userMap.put(user.getId(), user);
    }

    public Book getBookByIsbnMap(String isbn) {
        return bookMap.get(isbn);
    }

    public User getUser(String id) {
        return userMap.get(id);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(sortedBooks);
    }
    
    public BookTree getBookTree() { return categoryTree; }
    public LibraryGraph getLibraryGraph() { return graph; }

    private void sortBooks() {
        Collections.sort(sortedBooks, Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
    }

    // çok okunan kitapları belirleyip arrayi günceller
    public void updateTopBooks() {
        List<Book> all = new ArrayList<>(sortedBooks);
        all.sort((a, b) -> Integer.compare(b.getBorrowCount(), a.getBorrowCount()));
        for (int i = 0; i < TOP_N && i < all.size(); i++) {
            topBooksArray[i] = all.get(i);
        }
    }

    // arayüz için en popüler listesini yollar
    public Book[] getTopBooksArray() {
        return topBooksArray;
    }

    // isbn kullanarak bst üzerinden kitabı bulur
    public Book searchByIsbn(String targetIsbn) {
        return searchTree.search(targetIsbn);
    }

    public List<Book> searchByName(String targetName) {
        List<Book> result = new ArrayList<>();
        String lowerTarget = targetName.toLowerCase();
        for (Book bookItem : sortedBooks) {
            if (bookItem.getTitle().toLowerCase().contains(lowerTarget)) {
                result.add(bookItem);
            }
        }
        return result;
    }
}
