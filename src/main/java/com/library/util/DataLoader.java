package com.library.util;

import com.library.logic.LibraryDatabase;
import com.library.model.Book;
import com.library.model.User;
import com.library.model.UserType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {

    public static void initialize(LibraryDatabase db) {
        db.addUser(new User("std01", "1234", UserType.STUDENT));
        db.addUser(new User("aca01", "abcd", UserType.ACADEMICIAN));
        db.addUser(new User("aca02", "abcd", UserType.ACADEMICIAN));
        db.addUser(new User("admin", "admin", UserType.ADMIN));

        String csvFile = "library_dataset.csv";
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header
            br.readLine();

            while ((line = br.readLine()) != null) {
                // Use comma as separator
                String[] bookData = line.split(cvsSplitBy);
                
                if (bookData.length >= 6) {
                    String isbn = bookData[0].trim();
                    String title = bookData[1].trim();
                    String genre = bookData[2].trim();
                    String subGenre = bookData[3].trim();
                    String author = bookData[4].trim();
                    int borrowCount = Integer.parseInt(bookData[5].trim());
                    
                    // Generate a random shelf location for realism
                    String location = "Shelf " + ((int)(Math.random() * 3) + 1);

                    db.addBook(new Book(isbn, title, genre, subGenre, author, borrowCount, location));
                }
            }
        } catch (IOException e) {
            System.err.println("Dataset file not found or could not be read: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
