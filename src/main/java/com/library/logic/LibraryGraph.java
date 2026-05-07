package com.library.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Graf (Graph) Veri Yapısı:
// Burada kullanıcıların beraber okudukları kitapları birbiriyle ilişkilendirerek
// bir Kitap Öneri (Recommendation) ağı kuruyoruz.
public class LibraryGraph {
    
    // Hangi kitap hangi kitapla kaç kere beraber okunmuş onu tutan Ağırlıklı Graf (Weighted Graph)
    private Map<String, Map<String, Integer>> recommendationGraph;

    public LibraryGraph() {
        recommendationGraph = new HashMap<>();
    }

    // İki kitap beraber okunduysa, aralarındaki bağı (kenarı/edge) güçlendirir
    public void recordCoRead(List<String> userReadIsbns) {
        for (int i = 0; i < userReadIsbns.size(); i++) {
            for (int j = i + 1; j < userReadIsbns.size(); j++) {
                String firstBookIsbn = userReadIsbns.get(i);
                String secondBookIsbn = userReadIsbns.get(j);

                // Graf'ta düğümleri (node) oluştur
                recommendationGraph.putIfAbsent(firstBookIsbn, new HashMap<>());
                recommendationGraph.putIfAbsent(secondBookIsbn, new HashMap<>());

                // Ağırlıkları (bağlantı gücünü) arttır
                recommendationGraph.get(firstBookIsbn).put(secondBookIsbn, recommendationGraph.get(firstBookIsbn).getOrDefault(secondBookIsbn, 0) + 1);
                recommendationGraph.get(secondBookIsbn).put(firstBookIsbn, recommendationGraph.get(secondBookIsbn).getOrDefault(firstBookIsbn, 0) + 1);
            }
        }
    }

    // Bir kitaba en çok benzeyen (beraber okunan) limit sayıda kitabı getirir
    public List<String> getTopRecommendations(String isbn, int limit) {
        if (!recommendationGraph.containsKey(isbn))
            return new ArrayList<>();

        Map<String, Integer> bookConnections = recommendationGraph.get(isbn);
        
        // Graf kenarlarını (edge) ağırlıklarına göre büyükten küçüğe sıralıyoruz
        List<Map.Entry<String, Integer>> sortedEdges = new ArrayList<>(bookConnections.entrySet());
        sortedEdges.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<String> recommendedIsbns = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sortedEdges.size()); i++) {
            recommendedIsbns.add(sortedEdges.get(i).getKey());
        }
        return recommendedIsbns;
    }
}
