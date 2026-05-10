package com.library.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    private String id;
    private String password;
    private UserType userType;
    // Set (Küme) veri yapısı kullanılarak tekrarsız veriler (okunan kitaplar) tutuluyor.
    private Set<String> readIsbns;

    public User(String id, String password, UserType userType) {
        this.id = id;
        this.password = password;
        this.userType = userType;
        this.readIsbns = new HashSet<>();
    }

    public String getId() { return id; }

    public String getPassword() { return password; }

    public UserType getUserType() { return userType; }

    // Dışarıya verirken listeye çevirebiliriz (başka yerler liste bekliyorsa hata vermesin diye)
    public List<String> getReadIsbns() { 
        return new ArrayList<>(readIsbns); 
    }
    
    public void addReadIsbn(String isbn) { 
        // Set veri yapısı zaten aynı olanı eklemeyeceği için contains() aramaya gerek kalmıyor.
        // O(1) hızında ekleme yapar.
        readIsbns.add(isbn);
    }
}
