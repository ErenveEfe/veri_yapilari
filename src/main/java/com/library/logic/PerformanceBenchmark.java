package com.library.logic;

import com.library.model.Book;
import com.library.model.User;
import com.library.model.UserType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.io.BufferedReader;
import java.io.FileReader;

/*
 * kodların çalışma hızını ölçmek için kullanılıyor
 */
public class PerformanceBenchmark {

    // test sonuçlarını bir arada tutar
    public static class BenchmarkResult {
        private String testName;
        private String structure1;
        private String structure2;
        private int dataSize;
        private double time1Nano;
        private double time2Nano;
        private String winner;

        public BenchmarkResult(String testName, String structure1, String structure2,
                int dataSize, double time1Nano, double time2Nano) {
            this.testName = testName;
            this.structure1 = structure1;
            this.structure2 = structure2;
            this.dataSize = dataSize;
            this.time1Nano = time1Nano;
            this.time2Nano = time2Nano;
            this.winner = time1Nano <= time2Nano ? structure1 : structure2;
        }

        public String getTestName() {
            return testName;
        }

        public String getStructure1() {
            return structure1;
        }

        public String getStructure2() {
            return structure2;
        }

        public int getDataSize() {
            return dataSize;
        }

        public double getTime1Nano() {
            return time1Nano;
        }

        public double getTime2Nano() {
            return time2Nano;
        }

        public String getWinner() {
            return winner;
        }

        // sonucu milisaniye cinsinden hesaplar
        public double getTime1Ms() {
            return time1Nano / 1_000_000.0;
        }

        public double getTime2Ms() {
            return time2Nano / 1_000_000.0;
        }
    }

    private static final int ITERATION_COUNT = 1000;

    // yazılan algoritmaları belirtilen sayılarda test edip sonuçları döndürür
    public List<BenchmarkResult> runAllBenchmarks(int[] sizes) {
        List<BenchmarkResult> results = new ArrayList<>();

        for (int size : sizes) {

            List<Book> testBooks = loadTestBooks(size);
            int actualSize = testBooks.size();
            if (actualSize == 0)
                continue; // Veri yoksa atla
            String targetIsbn = testBooks.get(actualSize - 1).getIsbn(); // Son elemanı arayacağız (en kötü durum)

            results.add(benchmarkSearch(testBooks, targetIsbn, size));

            results.add(benchmarkContains(testBooks, targetIsbn, size));

            results.add(benchmarkPriority(size));
        }

        return results;
    }

    // dizide lineer aramayla bst aramasının hızını karşılaştırır
    private BenchmarkResult benchmarkSearch(List<Book> books, String targetIsbn, int dataSize) {
        // --- Veri yapilarını hazırla ---
        BookBST bst = new BookBST();
        for (Book b : books) {
            bst.insert(b);
        }

        // --- ArrayList Lineer Arama ---
        // Isınma turu (JVM JIT derleyicisinin optimize etmesi için)
        for (int i = 0; i < 100; i++) {
            linearSearch(books, targetIsbn);
        }
        long start1 = System.nanoTime();
        for (int i = 0; i < ITERATION_COUNT; i++) {
            linearSearch(books, targetIsbn);
        }
        long end1 = System.nanoTime();
        double avgTime1 = (double) (end1 - start1) / ITERATION_COUNT;

        // --- BST Ağaç Araması ---
        // Isınma turu
        for (int i = 0; i < 100; i++) {
            bst.search(targetIsbn);
        }
        long start2 = System.nanoTime();
        for (int i = 0; i < ITERATION_COUNT; i++) {
            bst.search(targetIsbn);
        }
        long end2 = System.nanoTime();
        double avgTime2 = (double) (end2 - start2) / ITERATION_COUNT;

        return new BenchmarkResult(
                "ISBN Arama",
                "ArrayList (Lineer O(n))",
                "BST (Ağaç O(log n))",
                dataSize, avgTime1, avgTime2);
    }

    // eleman var mı yok mu kontrolünde dizilerin mi yoksa setlerin mi hızlı
    // olduğunu ölçr
    private BenchmarkResult benchmarkContains(List<Book> books, String targetIsbn, int dataSize) {
        // --- Veri yapılarını hazırla ---
        ArrayList<String> isbnList = new ArrayList<>();
        HashSet<String> isbnSet = new HashSet<>();
        for (Book b : books) {
            isbnList.add(b.getIsbn());
            isbnSet.add(b.getIsbn());
        }

        // --- ArrayList.contains ---
        for (int i = 0; i < 100; i++) {
            isbnList.contains(targetIsbn);
        }
        long start1 = System.nanoTime();
        for (int i = 0; i < ITERATION_COUNT; i++) {
            isbnList.contains(targetIsbn);
        }
        long end1 = System.nanoTime();
        double avgTime1 = (double) (end1 - start1) / ITERATION_COUNT;

        // --- HashSet.contains ---
        for (int i = 0; i < 100; i++) {
            isbnSet.contains(targetIsbn);
        }
        long start2 = System.nanoTime();
        for (int i = 0; i < ITERATION_COUNT; i++) {
            isbnSet.contains(targetIsbn);
        }
        long end2 = System.nanoTime();
        double avgTime2 = (double) (end2 - start2) / ITERATION_COUNT;

        return new BenchmarkResult(
                "Eleman Kontrol",
                "ArrayList.contains (O(n))",
                "HashSet.contains (O(1))",
                dataSize, avgTime1, avgTime2);
    }

    // sıraya dizme işleminde sort mu yoksa kuyruk mu daha hılzı ona bakar
    private BenchmarkResult benchmarkPriority(int dataSize) {
        // --- Test kullanıcılarını üret ---
        List<User> testUsers = new ArrayList<>();
        for (int i = 0; i < dataSize; i++) {
            UserType type = (i % 3 == 0) ? UserType.ACADEMICIAN : UserType.STUDENT;
            testUsers.add(new User("user" + i, "pass", type));
        }

        // Öncelik sıralaması için Comparator
        Comparator<User> comp = (u1, u2) -> {
            if (u1.getUserType() == UserType.ACADEMICIAN && u2.getUserType() != UserType.ACADEMICIAN)
                return -1;
            if (u1.getUserType() != UserType.ACADEMICIAN && u2.getUserType() == UserType.ACADEMICIAN)
                return 1;
            return 0;
        };

        // --- ArrayList + Sort ---
        for (int i = 0; i < 50; i++) {
            ArrayList<User> tempList = new ArrayList<>(testUsers);
            tempList.sort(comp);
            tempList.get(0); // en önceliklisini al
        }
        long start1 = System.nanoTime();
        for (int i = 0; i < ITERATION_COUNT; i++) {
            ArrayList<User> tempList = new ArrayList<>(testUsers);
            tempList.sort(comp);
            tempList.get(0);
        }
        long end1 = System.nanoTime();
        double avgTime1 = (double) (end1 - start1) / ITERATION_COUNT;

        // --- PriorityQueue ---
        for (int i = 0; i < 50; i++) {
            PriorityQueue<User> pq = new PriorityQueue<>(comp);
            for (User u : testUsers)
                pq.offer(u);
            pq.poll();
        }
        long start2 = System.nanoTime();
        for (int i = 0; i < ITERATION_COUNT; i++) {
            PriorityQueue<User> pq = new PriorityQueue<>(comp);
            for (User u : testUsers)
                pq.offer(u);
            pq.poll();
        }
        long end2 = System.nanoTime();
        double avgTime2 = (double) (end2 - start2) / ITERATION_COUNT;

        return new BenchmarkResult(
                "Önceliklendirme",
                "ArrayList + Sort (O(n log n))",
                "PriorityQueue (O(log n))",
                dataSize, avgTime1, avgTime2);
    }

    // --- Yardımcı Metotlar ---

    // dizide baştan sona doğru tek tek isbn araması yapar
    private Book linearSearch(List<Book> books, String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    // Test için gerçek veri setinden belirtilen sayıda kitap yükler.

    private List<Book> loadTestBooks(int count) {
        List<Book> books = new ArrayList<>();
        String file = "library_benchmark_dataset.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Header atla
            while ((line = br.readLine()) != null && books.size() < count) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String isbn = parts[0].trim();
                    String title = parts[1].trim();
                    String genre = parts[2].trim();
                    String sub = parts[3].trim();
                    String author = parts[4].trim();
                    int borrow = 0;
                    try {
                        borrow = Integer.parseInt(parts[5].trim());
                    } catch (Exception e) {
                    }

                    books.add(new Book(isbn, title, genre, sub, author, borrow));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Veriyi rastgele karıştırır
        Collections.shuffle(books);
        return books;
    }
}
