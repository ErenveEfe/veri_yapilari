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
        db.addUser(new User("std01", "123", UserType.STUDENT));
        db.addUser(new User("std02", "123", UserType.STUDENT));
        db.addUser(new User("std03", "123", UserType.STUDENT));
        db.addUser(new User("std04", "123", UserType.STUDENT));

        db.addUser(new User("aca01", "123", UserType.ACADEMICIAN));
        db.addUser(new User("aca02", "123", UserType.ACADEMICIAN));
        db.addUser(new User("admin", "admin", UserType.ADMIN));

        String path = "library_dataset_unique.csv";
        String line = "";

        try (BufferedReader reader = new BufferedReader(
                new FileReader(path, java.nio.charset.StandardCharsets.UTF_8))) {

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] bookData = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (bookData.length >= 6) {
                    String isbn = bookData[0].replaceAll("^\"|\"$", "").trim();
                    String title = bookData[1].replaceAll("^\"|\"$", "").trim();
                    String genre = bookData[2].replaceAll("^\"|\"$", "").trim();
                    String subGenre = bookData[3].replaceAll("^\"|\"$", "").trim();
                    String author = bookData[4].replaceAll("^\"|\"$", "").trim();
                    int borrowCount = Integer.parseInt(bookData[5].replaceAll("^\"|\"$", "").trim());

                    db.addBook(new Book(isbn, title, genre, subGenre, author, borrowCount));
                }
            }
        } catch (IOException exception) {
            System.err.println("Veri seti dosyası bulunamadı veya okunamadı: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
