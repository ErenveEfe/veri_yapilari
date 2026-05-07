package com.library.logic;

import com.library.model.Book;
import com.library.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class LibraryDatabase {
    private HashMap<String, Book> bookMap;
    private HashMap<String, User> userMap;
    private List<Book> sortedBooks;
    
    private BookTree categoryTree;
    private BookBST searchTree; // Yeni Eklenen İkili Arama Ağacımız
    private LibraryGraph graph;

    public LibraryDatabase() {
        bookMap = new HashMap<>();
        userMap = new HashMap<>();
        sortedBooks = new ArrayList<>();
        categoryTree = new BookTree();
        searchTree = new BookBST(); // Ağacı başlattık
        graph = new LibraryGraph();
    }

    public void addBook(Book book) {
        bookMap.put(book.getIsbn(), book);
        sortedBooks.add(book);
        
        // Yeni kitabı İkili Arama Ağacına (BST) ekliyoruz
        searchTree.insert(book);
        
        sortBooks();
        categoryTree.addBook(book.getGenre(), book.getSubGenre(), book);
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

    // İKİLİ ARAMA AĞACINDA (BST) ARAMA YAPIYORUZ
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
