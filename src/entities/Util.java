package entities;

import entertainment.Season;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {
    public static ArrayList<String> firstNActors(Show show, Action query, List<Show> shows){
        Map<String, Double> sumOfRatings = new HashMap<>();
        Map<String, Double> average = new HashMap<>();
        Map<String, Integer> nrAppearances = new HashMap<>();
        for(Show sh : shows){
            if(sh.getSeasons() != null && sh.getSeasons().size() != 0){
                for (Season s : sh.getSeasons()){
                    if(s.getRatings().size() != 0){
                        Double avg =  s.getRatings().stream().mapToDouble(Double::doubleValue).sum()
                                / s.getRatings().size();
                        for(String a : sh.getCast()){
                            if(sumOfRatings.get(a) != null)
                                sumOfRatings.put(a, avg + sumOfRatings.get(a));
                            else
                                sumOfRatings.put(a, avg);
                        }
                    }
                }
                for(String a : sh.getCast()){
                    if (sumOfRatings.get(a) != null) {
                        if (average.get(a) != null) {
                            average.put(a, average.get(a) + (sumOfRatings.get(a) / sh.getSeasons().size()));
                        }
                        else{
                            average.put(a, sumOfRatings.get(a) / sh.getSeasons().size());
                        }
                    }
                }
            }
            else {
                if(sh.getRatings().size() != 0){
                    for(String a : sh.getCast()){
                        System.out.println(a + " " +sh.getRatings());
                        if (average.get(a) != null) {
                            average.put(a, average.get(a) +
                                    sh.getRatings().stream().mapToDouble(Double::doubleValue).sum()
                                            / sh.getRatings().size());
                        }
                        else{
                            average.put(a,
                                    sh.getRatings().stream().mapToDouble(Double::doubleValue).sum()
                                            / sh.getRatings().size());
                        }
                    }
                }
            }
        }

        System.out.println(average);
        Map<String,Double> topN =
                average.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                        .limit(query.getNumber())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        ArrayList<String> firstActors = new ArrayList<>(topN.keySet());
        return firstActors;
    }


    public static ArrayList<String> firstUsers(Show show, Action query, List<User> users){
        Map<String, Integer> first = new HashMap<>();
        for (User u : users) {
            if(u.getRating().size() != 0)
                first.put(u.getUsername(),u.getRating().size());
        }
        Map<String, Integer> firstN;
        if(query.getSortType() == "desc") {
            firstN =
                    first.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        else{
            firstN =
                    first.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> firstUsers = new ArrayList<>(firstN.keySet());
        return firstUsers;
    }
}
