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
        for (TreeNode child : parent.getChildren()) {
            if (child.getValue().equals(value)) {
                return child;
            }
        }
        TreeNode newNode = new TreeNode(value, null);
        parent.addChild(newNode);
        return newNode;
    }

    public List<Book> getBooksByGenre(String genre, String subGenre) {
        List<Book> result = new ArrayList<>();
        for (TreeNode child : root.getChildren()) {
            if (child.getValue().equalsIgnoreCase(genre)) {
                for (TreeNode subNode : child.getChildren()) {
                    if (subGenre == null || subGenre.equalsIgnoreCase("Hepsi") || subNode.getValue().equalsIgnoreCase(subGenre)) {
                        for (TreeNode bookNode : subNode.getChildren()) {
                            if (bookNode.getBook() != null) {
                                result.add(bookNode.getBook());
                            }
                        }
                    }
                }
                break;
            }
        }
        return result;
    }

    public List<String> getGenres() {
        List<String> result = new ArrayList<>();
        for (TreeNode child : root.getChildren()) {
            result.add(child.getValue());
        }
        return result;
    }

    public List<String> getSubGenres(String genre) {
        List<String> result = new ArrayList<>();
        result.add("Hepsi");
        for (TreeNode child : root.getChildren()) {
            if (child.getValue().equalsIgnoreCase(genre)) {
                for (TreeNode subNode : child.getChildren()) {
                    result.add(subNode.getValue());
                }
                break;
            }
        }
        return result;
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
