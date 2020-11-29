package entities;

import entertainment.Season;

import java.util.*;
import java.util.stream.Collectors;

public class UtilRecommendation {
    public static User findUser(String username, List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }
        return null;
    }

    public static String standard(Action recommandation, List<Show> shows, List<User> users) {
        for(int i = 0; i<shows.size(); i++){
            if(!findUser(recommandation.getUsername(), users).getHistory().containsKey(shows.get(i).getTitle())){
                return shows.get(i).getTitle();
            }
        }
        return null;
    }

    public static String bestUnseen(Action recommandation, List<Show> shows, List<User> users) {
        Map<String, Double> ratings = new LinkedHashMap<>();
        for(int i = 0; i<shows.size(); i++){
            if(shows.get(i).getSeasons() != null && shows.get(i).getSeasons().size() != 0) {
                Double serialRating = 0.0;
                for (Season s : shows.get(i).getSeasons()) {
                    if (s.getRatings().size() != 0) {
                        Double avg = s.getRatings().stream().mapToDouble(Double::doubleValue).sum()
                                / s.getRatings().size();
                        serialRating += avg;
                    }
                }
                ratings.put(shows.get(i).getTitle(), serialRating / shows.get(i).getSeasons().size());

            }
            else{
                if (shows.get(i).getRatings().size() != 0) {
                    ratings.put(shows.get(i).getTitle(), shows.get(i).getRatings().stream().mapToDouble(Double::doubleValue).sum()
                            / shows.get(i).getRatings().size());
                }
                else{
                    ratings.put(shows.get(i).getTitle(), 0.0);
                }
            }
        }
        Map<String, Double> sorted;
        sorted =
                ratings.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        ArrayList<String> sortedShows = new ArrayList<>(sorted.keySet());
        for(String s : sortedShows){
            if(!findUser(recommandation.getUsername(), users).getHistory().containsKey(s)){
                return s;
            }
        }
        return null;
    }

    public static String popular(Action recommandation, List<Show> shows, List<User> users) {
        Map<String, Integer> popularGenre = new LinkedHashMap<>();
        for(Show sh : shows){
            for(String genre : sh.getGenres()){
                if(popularGenre.containsKey(genre)) {
                    int value = popularGenre.get(genre);
                    popularGenre.put(genre, value + sh.getViews());
                }
                else {
                    popularGenre.put(genre, sh.getViews());
                }
            }
        }
        popularGenre =
                popularGenre.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for(String g : popularGenre.keySet()){
            for(Show sh : shows) {
                if (sh.getGenres().contains(g)) {
                    if (!findUser(recommandation.getUsername(), users).getHistory().containsKey(sh.getTitle())){
                        return sh.getTitle();
                    }
                }
            }
        }
        return null;
    }
}
