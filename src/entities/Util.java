package entities;

import actor.ActorsAwards;
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

        Map<String,Double> topN =
                average.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                        .limit(query.getNumber())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        ArrayList<String> firstActors = new ArrayList<>(topN.keySet());
        return firstActors;
    }

    public static ArrayList<String> awards(Show show, Action query, List<Actor> actors) {
        Map<String, Integer> actorsAwards = new HashMap<>();
        ArrayList<String> awardsString = new ArrayList<>();
        for (Actor a : actors){
            for(ActorsAwards aw : a.getAwards().keySet()) {
                awardsString.add(aw.toString());
            }
            if(awardsString.containsAll(query.getFilters().get(3))){
               actorsAwards.put(a.getName(), (int) a.getAwards().values().stream().mapToLong(Integer::longValue).sum());
            }
            awardsString.clear();
        }
        Map<String, Integer> sorted;
        if(query.getSortType() == "desc") {
            sorted =
                    actorsAwards.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        else{
            sorted =
                    actorsAwards.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> sortedActors = new ArrayList<>(sorted.keySet());
        return sortedActors;

    }

    public static ArrayList<String> filterDescription (Show show, Action query, List<Actor> actors) {
        ArrayList<String> actorsWithFilteredDescription = new ArrayList<>();
        for (Actor a : actors){
            boolean ok = true;
            for(String item : query.getFilters().get(2)) {
                if (!a.getCareerDescription().contains(item)) {
                    ok = false;
                    break;
                }
            }
            if(ok == true){
                actorsWithFilteredDescription.add(a.getName());
            }
        }
        if(query.getSortType() == "desc") {
            Collections.sort(actorsWithFilteredDescription, Collections.reverseOrder());
        }
        else{
            Collections.sort(actorsWithFilteredDescription);
        }
        return actorsWithFilteredDescription;

    }


    public static ArrayList<String> firstNVideosRating(Show show, Action query, List<Show> shows) {
        Map<String, Double> ratings = new HashMap<>();
        for(Show sh : shows){
            if(sh.getSeasons() != null && sh.getSeasons().size() != 0) {
                if(query.getFilters().get(0).get(0) != null && query.getFilters().get(1).get(0) != null) {
                    if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                            && sh.getYear() == Integer.parseInt(query.getFilters().get(0).get(0))) {
                        for (Season s : sh.getSeasons()) {
                            if (s.getRatings().size() != 0) {
                                Double avg = s.getRatings().stream().mapToDouble(Double::doubleValue).sum()
                                        / s.getRatings().size();
                                ratings.put(sh.getTitle(), avg);
                            }
                        }
                    }
                }
            }
            else {
                if(query.getFilters().get(0).get(0) != null && query.getFilters().get(1).get(0) != null) {
                    if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                            && sh.getYear() == Integer.parseInt(query.getFilters().get(0).get(0))) {
                        if (sh.getRatings().size() != 0) {
                            ratings.put(sh.getTitle(), sh.getRatings().stream().mapToDouble(Double::doubleValue).sum()
                                    / sh.getRatings().size());

                        }
                    }
                }
            }
        }
        Map<String, Double> firstN;
        if(query.getSortType() == "desc") {
            firstN =
                    ratings.entrySet().stream()
                            .sorted(Map.Entry.<String, Double>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        else{
            firstN =
                    ratings.entrySet().stream()
                            .sorted(Map.Entry.<String, Double>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> firstShows = new ArrayList<>(firstN.keySet());
        return firstShows;

    }

    public static ArrayList<String> firstNVideosFavourite(Action query, List<User> users, List<Show> shows){
        Map<String, Integer> nrAppearances = new HashMap<>();
        for(User u : users){
            for(String s : u.getFavoriteMovies()){
                for(Show sh : shows){
                    if(sh.getTitle().equals(s)){
                        if(query.getFilters().get(0).get(0) != null && query.getFilters().get(1).get(0) != null) {
                            if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                                    && sh.getYear() == Integer.parseInt(query.getFilters().get(0).get(0))) {
                                if (nrAppearances.containsKey(s)) {
                                    int value = nrAppearances.get(s);
                                    nrAppearances.put(s, value + 1);
                                } else {
                                    nrAppearances.put(s, 1);
                                }
                            }
                        }
                    }
                }

            }
        }
        Map<String, Integer> firstN;
        if(query.getSortType() == "desc") {
            firstN =
                    nrAppearances.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        else{
            firstN =
                    nrAppearances.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> firstShows = new ArrayList<>(firstN.keySet());
        return firstShows;

    }

    public static ArrayList<String> firstNVideosLongest(Show show, Action query, List<Show> shows) {
        Map<String, Integer> duration = new HashMap<>();
        for(Show sh : shows){
            if(query.getFilters().get(0).get(0) != null && query.getFilters().get(1).get(0) != null) {
                if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                            && sh.getYear() == Integer.parseInt(query.getFilters().get(0).get(0))) {
                        duration.put(sh.getTitle(), sh.getDuration());
                }
            }
        }

        Map<String, Integer> firstN;
        if(query.getSortType() == "desc") {
            firstN =
                    duration.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        else{
            firstN =
                    duration.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> firstShows = new ArrayList<>(firstN.keySet());
        return firstShows;

    }

    public static Show findShow(String title, List<Show> shows) {
        for (int i = 0; i < shows.size(); i++) {
            if (shows.get(i).getTitle().equals(title)) {
                return shows.get(i);
            }
        }
        return null;
    }
    public static ArrayList<String> firstNVideosMostViewed(Show show, Action query, List<Show> shows, List<User> users) {
        Map<String, Integer> views = new HashMap<>();
        for (User u : users){
            u.getHistory().forEach((s, v) -> findShow(s, shows).setViews(findShow(s, shows).getViews() + v));
        }
        for(Show sh : shows){
            if(sh.getViews() != 0) {
                if (query.getFilters().get(0).get(0) != null && query.getFilters().get(1).get(0) != null) {
                    if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                            && sh.getYear() == Integer.parseInt(query.getFilters().get(0).get(0))) {
                        views.put(sh.getTitle(), sh.getViews());
                    }
                }
            }
        }

        Map<String, Integer> firstN;
        if(query.getSortType() == "desc") {
            firstN =
                    views.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        else{
            firstN =
                    views.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> firstShows = new ArrayList<>(firstN.keySet());
        return firstShows;

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
