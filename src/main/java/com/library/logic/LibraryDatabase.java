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

    // DİZİ (ARRAY) VERİ YAPISI:
    // Sabit boyutlu bir ilkel dizi kullanarak en popüler 10 kitabı tutuyoruz.
    // ArrayList yerine Array kullanmamızın sebebi: boyut sabit ve önceden biliniyor,
    // bu durumda Array daha az bellek tüketir ve indeks erişimi O(1) hızındadır.
    private static final int TOP_N = 10;
    private Book[] topBooksArray = new Book[TOP_N];

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
        
        // Sabit boyutlu dizi (Array) güncelleniyor
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

    // DİZİ (ARRAY) GÜNCELLEME:
    // En çok okunan kitapları sabit boyutlu diziye (Array) aktarıyoruz.
    // Dizi boyutu sabittir (TOP_N=10), bu yüzden ArrayList'e gerek yoktur.
    public void updateTopBooks() {
        List<Book> all = new ArrayList<>(sortedBooks);
        all.sort((a, b) -> Integer.compare(b.getBorrowCount(), a.getBorrowCount()));
        for (int i = 0; i < TOP_N && i < all.size(); i++) {
            topBooksArray[i] = all.get(i);
        }
    }

    // Sabit boyutlu diziyi döndürür — O(1) erişim
    public Book[] getTopBooksArray() {
        return topBooksArray;
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
