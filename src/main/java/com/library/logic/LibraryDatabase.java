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
    private List<Book> sortedBooksByName;
    
    private BookTree bookTree;
    private BookBST bookBST; // Yeni Eklenen İkili Arama Ağacımız
    private LibraryGraph libraryGraph;

    public LibraryDatabase() {
        bookMap = new HashMap<>();
        userMap = new HashMap<>();
        sortedBooksByName = new ArrayList<>();
        bookTree = new BookTree();
        bookBST = new BookBST(); // Ağacı başlattık
        libraryGraph = new LibraryGraph();
    }

    public void addBook(Book book) {
        bookMap.put(book.getIsbn(), book);
        sortedBooksByName.add(book);
        
        // Yeni kitabı İkili Arama Ağacına (BST) ekliyoruz
        bookBST.insert(book);
        
        sortBooks();
        bookTree.addBookToCategory(book.getGenre(), book.getSubGenre(), book);
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
        return new ArrayList<>(sortedBooksByName);
    }
    
    public BookTree getBookTree() { return bookTree; }
    public LibraryGraph getLibraryGraph() { return libraryGraph; }

    private void sortBooks() {
        Collections.sort(sortedBooksByName, Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
    }

    // İKİLİ ARAMA AĞACINDA (BST) ARAMA YAPIYORUZ
    public Book searchBookInBST(String targetIsbn) {
        return bookBST.search(targetIsbn);
    }

    public List<Book> searchBooksByPartialName(String targetName) {
        List<Book> result = new ArrayList<>();
        String lowerTarget = targetName.toLowerCase();
        for (Book currentBook : sortedBooksByName) {
            if (currentBook.getTitle().toLowerCase().contains(lowerTarget)) {
                result.add(currentBook);
            }
        }
        return result;
    }
}
