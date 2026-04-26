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
    private List<Book> sortedBooksByIsbn;
    private List<Book> sortedBooksByName;
    
    private BookTree bookTree;
    private LibraryGraph libraryGraph;

    public LibraryDatabase() {
        bookMap = new HashMap<>();
        userMap = new HashMap<>();
        sortedBooksByIsbn = new ArrayList<>();
        sortedBooksByName = new ArrayList<>();
        bookTree = new BookTree();
        libraryGraph = new LibraryGraph();
    }

    public void addBook(Book book) {
        bookMap.put(book.getIsbn(), book);
        sortedBooksByIsbn.add(book);
        sortedBooksByName.add(book);
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
        return new ArrayList<>(sortedBooksByIsbn);
    }
    
    public BookTree getBookTree() { return bookTree; }
    public LibraryGraph getLibraryGraph() { return libraryGraph; }

    private void sortBooks() {
        Collections.sort(sortedBooksByIsbn, Comparator.comparing(Book::getIsbn));
        Collections.sort(sortedBooksByName, Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
    }

    public Book binarySearchBookByIsbn(String targetIsbn) {
        int left = 0;
        int right = sortedBooksByIsbn.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Book midBook = sortedBooksByIsbn.get(mid);
            int cmp = midBook.getIsbn().compareTo(targetIsbn);

            if (cmp == 0) {
                return midBook;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    public List<Book> searchBooksByPartialName(String targetName) {
        List<Book> result = new ArrayList<>();
        String lowerTarget = targetName.toLowerCase();
        for (Book b : sortedBooksByName) {
            if (b.getTitle().toLowerCase().contains(lowerTarget)) {
                result.add(b);
            }
        }
        return result;
    }
}
