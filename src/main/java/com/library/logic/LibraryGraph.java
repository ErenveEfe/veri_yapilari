package com.library.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class LibraryGraph {
    private Map<String, Set<String>> locationGraph;
    private Map<String, Map<String, Integer>> recommendationGraph;

    public LibraryGraph() {
        locationGraph = new HashMap<>();
        recommendationGraph = new HashMap<>();

        addLocationEdge("Entrance", "Corridor A");
        addLocationEdge("Corridor A", "Shelf 1");
        addLocationEdge("Corridor A", "Shelf 2");
        addLocationEdge("Shelf 2", "Corridor B");
        addLocationEdge("Corridor B", "Shelf 3");
        addLocationEdge("Corridor B", "Emergency Exit");
    }

    private void addLocationEdge(String loc1, String loc2) {
        locationGraph.putIfAbsent(loc1, new HashSet<>());
        locationGraph.putIfAbsent(loc2, new HashSet<>());
        locationGraph.get(loc1).add(loc2);
        locationGraph.get(loc2).add(loc1);
    }

    public LinkedList<String> findShortestPathToExit(String startLocation) {
        String target = "Emergency Exit";
        if (!locationGraph.containsKey(startLocation) || !locationGraph.containsKey(target)) {
            return new LinkedList<>();
        }

        Queue<String> bfsQueue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        bfsQueue.add(startLocation);
        visited.add(startLocation);
        parentMap.put(startLocation, null);

        while (!bfsQueue.isEmpty()) {
            String current = bfsQueue.poll();

            if (current.equals(target)) {
                return reconstructPath(parentMap, target);
            }

            for (String neighbor : locationGraph.getOrDefault(current, new HashSet<>())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    bfsQueue.add(neighbor);
                }
            }
        }
        return new LinkedList<>();
    }

    private LinkedList<String> reconstructPath(Map<String, String> parentMap, String target) {
        LinkedList<String> path = new LinkedList<>();
        String curr = target;
        while (curr != null) {
            path.addFirst(curr);
            curr = parentMap.get(curr);
        }
        return path;
    }

    public void recordCoRead(List<String> userReadIsbns) {
        for (int i = 0; i < userReadIsbns.size(); i++) {
            for (int j = i + 1; j < userReadIsbns.size(); j++) {
                String isbn1 = userReadIsbns.get(i);
                String isbn2 = userReadIsbns.get(j);

                recommendationGraph.putIfAbsent(isbn1, new HashMap<>());
                recommendationGraph.putIfAbsent(isbn2, new HashMap<>());

                recommendationGraph.get(isbn1).put(isbn2, recommendationGraph.get(isbn1).getOrDefault(isbn2, 0) + 1);
                recommendationGraph.get(isbn2).put(isbn1, recommendationGraph.get(isbn2).getOrDefault(isbn1, 0) + 1);
            }
        }
    }

    public List<String> getTopRecommendations(String isbn, int limit) {
        if (!recommendationGraph.containsKey(isbn))
            return new ArrayList<>();

        Map<String, Integer> edges = recommendationGraph.get(isbn);
        List<Map.Entry<String, Integer>> list = new ArrayList<>(edges.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, list.size()); i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }
}
