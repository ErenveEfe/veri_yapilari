package com.library.logic;

import com.library.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookTree {
    private TreeNode root;

    public BookTree() {
        this.root = new TreeNode("Library", null);
    }

    public void addBook(String genre, String subGenre, Book book) {
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
        List<Book> list = new ArrayList<>();
        for (TreeNode genreNode : root.getChildren()) {
            if (genreNode.getValue().equalsIgnoreCase(genre)) {
                for (TreeNode subGenreNode : genreNode.getChildren()) {
                    if (subGenre == null || subGenre.equalsIgnoreCase("Hepsi") || subGenreNode.getValue().equalsIgnoreCase(subGenre)) {
                        for (TreeNode bookNode : subGenreNode.getChildren()) {
                            if (bookNode.getBook() != null) {
                                list.add(bookNode.getBook());
                            }
                        }
                    }
                }
                break;
            }
        }
        return list;
    }

    public List<String> getGenres() {
        List<String> list = new ArrayList<>();
        for (TreeNode genreNode : root.getChildren()) {
            list.add(genreNode.getValue());
        }
        return list;
    }

    public List<String> getSubGenres(String genre) {
        List<String> list = new ArrayList<>();
        list.add("Hepsi");
        for (TreeNode genreNode : root.getChildren()) {
            if (genreNode.getValue().equalsIgnoreCase(genre)) {
                for (TreeNode subGenreNode : genreNode.getChildren()) {
                    list.add(subGenreNode.getValue());
                }
                break;
            }
        }
        return list;
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
