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

        String csvFilePath = "library_dataset.csv";
        String line = "";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFilePath, java.nio.charset.StandardCharsets.UTF_8))) {
            // Skip the header
            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {
                // Use comma as separator, ignoring commas inside quotes
                String[] bookData = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (bookData.length >= 6) {
                    String isbn = bookData[0].replaceAll("^\"|\"$", "").trim();
                    String title = bookData[1].replaceAll("^\"|\"$", "").trim();
                    String genre = bookData[2].replaceAll("^\"|\"$", "").trim();
                    String subGenre = bookData[3].replaceAll("^\"|\"$", "").trim();
                    String author = bookData[4].replaceAll("^\"|\"$", "").trim();
                    int borrowCount = Integer.parseInt(bookData[5].replaceAll("^\"|\"$", "").trim());
                    
                    // Generate a random shelf location for realism
                    String location = "Shelf " + ((int)(Math.random() * 3) + 1);

                    db.addBook(new Book(isbn, title, genre, subGenre, author, borrowCount, location));
                }
            }
        } catch (IOException exception) {
            System.err.println("Dataset file not found or could not be read: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
