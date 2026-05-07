package com.library.logic;

import com.library.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookTree {
    private TreeNode root;

    public BookTree() {
        this.root = new TreeNode("Library", null);
    }

    public TreeNode getRoot() {
        return root;
    }

    public void addBookToCategory(String genre, String subGenre, Book book) {
        TreeNode genreNode = findOrAddChild(root, genre);
        TreeNode subGenreNode = findOrAddChild(genreNode, subGenre);
        TreeNode bookNode = new TreeNode(book.getTitle(), book);
        subGenreNode.addChild(bookNode);
    }

    private TreeNode findOrAddChild(TreeNode parent, String value) {
        for (TreeNode childNode : parent.getChildren()) {
            if (childNode.getValue().equals(value)) {
                return childNode;
            }
        }
        TreeNode newNode = new TreeNode(value, null);
        parent.addChild(newNode);
        return newNode;
    }

    public List<Book> getBooksByGenre(String genre, String subGenre) {
        List<Book> foundBooks = new ArrayList<>();
        for (TreeNode genreNode : root.getChildren()) {
            if (genreNode.getValue().equalsIgnoreCase(genre)) {
                for (TreeNode subGenreNode : genreNode.getChildren()) {
                    if (subGenre == null || subGenre.equalsIgnoreCase("Hepsi") || subGenreNode.getValue().equalsIgnoreCase(subGenre)) {
                        for (TreeNode bookNode : subGenreNode.getChildren()) {
                            if (bookNode.getBook() != null) {
                                foundBooks.add(bookNode.getBook());
                            }
                        }
                    }
                }
                break;
            }
        }
        return foundBooks;
    }

    public List<String> getGenres() {
        List<String> genreList = new ArrayList<>();
        for (TreeNode genreNode : root.getChildren()) {
            genreList.add(genreNode.getValue());
        }
        return genreList;
    }

    public List<String> getSubGenres(String genre) {
        List<String> subGenreList = new ArrayList<>();
        subGenreList.add("Hepsi");
        for (TreeNode genreNode : root.getChildren()) {
            if (genreNode.getValue().equalsIgnoreCase(genre)) {
                for (TreeNode subGenreNode : genreNode.getChildren()) {
                    subGenreList.add(subGenreNode.getValue());
                }
                break;
            }
        }
        return subGenreList;
    }

    public static class TreeNode {
        private String value;
        private Book book;
        private List<TreeNode> children;

        public TreeNode(String value, Book book) {
            this.value = value;
            this.book = book;
            this.children = new ArrayList<>();
        }

        public void addChild(TreeNode node) {
            this.children.add(node);
        }

        public String getValue() { return value; }
        public Book getBook() { return book; }
        public List<TreeNode> getChildren() { return children; }
    }
}
