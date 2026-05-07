package com.library.logic;

import com.library.model.Book;

// İkili Arama Ağacı (Binary Search Tree - BST) Veri Yapısı
// Kitapları ISBN numaralarına göre sıralı bir ağaç yapısında tutar.
public class BookBST {

    // Ağacın her bir düğümü
    private class Node {
        Book book;
        Node left;
        Node right;

        public Node(Book book) {
            this.book = book;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;

    public BookBST() {
        this.root = null;
    }

    // Ağaca yeni kitap ekler
    public void insert(Book book) {
        root = insertRec(root, book);
    }

    // Öz yinelemeli (recursive) olarak doğru yeri bulup ekler
    private Node insertRec(Node root, Book book) {
        // Ağaç boşsa veya en uca geldiysek yeni düğümü oluşturur
        if (root == null) {
            root = new Node(book);
            return root;
        }

        // ISBN numarasına göre karşılaştırma yapar ve ağaçta ilerler
        if (book.getIsbn().compareTo(root.book.getIsbn()) < 0) {
            root.left = insertRec(root.left, book);
        } else if (book.getIsbn().compareTo(root.book.getIsbn()) > 0) {
            root.right = insertRec(root.right, book);
        }

        return root;
    }

    // ISBN'e göre kitabı ağaçta arar
    public Book search(String isbn) {
        Node result = searchRec(root, isbn);
        if (result != null) {
            return result.book;
        }
        return null; // Bulunamazsa
    }

    private Node searchRec(Node root, String isbn) {
        // Ağacın sonuna gelinmişse veya aranan kitap bulunmuşsa
        if (root == null || root.book.getIsbn().equals(isbn)) {
            return root;
        }

        // Aranan değer şu anki düğümden küçükse sola gider
        if (root.book.getIsbn().compareTo(isbn) > 0) {
            return searchRec(root.left, isbn);
        }
        // Aranan değer şu anki düğümden büyükse sağa gider
        else {
            return searchRec(root.right, isbn);
        }
    }
}
