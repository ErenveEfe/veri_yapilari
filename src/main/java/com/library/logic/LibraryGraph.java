package com.library.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// kitaplar arası okuma ilişkisini graf yapısıyla tutuyor
public class LibraryGraph {

    private Map<String, Map<String, Integer>> graph;

    public LibraryGraph() {
        graph = new HashMap<>();
    }

    // aynı kişinin okuduğu kitapları birbirne bağlar
    public void addCoRead(List<String> readList) {
        for (int i = 0; i < readList.size(); i++) {
            for (int j = i + 1; j < readList.size(); j++) {
                String isbn1 = readList.get(i);
                String isbn2 = readList.get(j);

                // Graf'ta düğümleri (node) oluştur
                graph.putIfAbsent(isbn1, new HashMap<>());
                graph.putIfAbsent(isbn2, new HashMap<>());

                // Ağırlıkları (bağlantı gücünü) arttır
                graph.get(isbn1).put(isbn2, graph.get(isbn1).getOrDefault(isbn2, 0) + 1);
                graph.get(isbn2).put(isbn1, graph.get(isbn2).getOrDefault(isbn1, 0) + 1);
            }
        }
    }

    // bu kitabı okuyanların okuduğu diğer kitapları önerir
    public List<String> getRecommendations(String isbn, int limit) {
        if (!graph.containsKey(isbn))
            return new ArrayList<>();

        Map<String, Integer> edges = graph.get(isbn);

        List<Map.Entry<String, Integer>> list = new ArrayList<>(edges.entrySet());
        list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, list.size()); i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }
}
