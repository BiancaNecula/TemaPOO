package entities;

import entertainment.Season;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public final class UtilRecommendation {
    /**
     * @param username user's name
     * @param users list of users
     * @return user with this name
     */
    public static User findUser(final String username, final List<User> users) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * @param recommendation action
     * @param shows list of shows
     * @param users list of users
     * @return recommended show (first unseen)
     */
    public static String standard(final Action recommendation,
                                  final List<Show> shows,
                                  final List<User> users) {
        for (Show show : shows) {
            if (!findUser(recommendation.getUsername(), users)
                    .getHistory().containsKey(show.getTitle())) {
                return show.getTitle();
            }
        }
        return null;
    }

    /**
     * @param recommendation action
     * @param shows list of shows
     * @param users list of users
     * @return recommended show (best unseen)
     */
    public static String bestUnseen(final Action recommendation,
                                    final List<Show> shows,
                                    final List<User> users) {
        Map<String, Double> ratings = new LinkedHashMap<>();
        for (Show show : shows) {
            if (show.getSeasons() != null && show.getSeasons().size() != 0) {
                double serialRating = 0.0;
                for (Season s : show.getSeasons()) {
                    if (s.getRatings().size() != 0) {
                        double avg = s.getRatings().stream().mapToDouble(Double::doubleValue).sum()
                                / s.getRatings().size();
                        serialRating += avg;
                    }
                }
                ratings.put(show.getTitle(), serialRating / show.getSeasons().size());

            } else {
                if (show.getRatings().size() != 0) {
                    ratings.put(show.getTitle(), show.getRatings()
                            .stream().mapToDouble(Double::doubleValue).sum()
                            / show.getRatings().size());
                } else {
                    ratings.put(show.getTitle(), 0.0);
                }
            }
        }
        Map<String, Double> sorted;
        sorted =
                ratings.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
        ArrayList<String> sortedShows = new ArrayList<>(sorted.keySet());
        for (String s : sortedShows) {
            if (!findUser(recommendation.getUsername(), users).getHistory().containsKey(s)) {
                return s;
            }
        }
        return null;
    }

    /**
     * @param recommendation action
     * @param shows list of shows
     * @param users list of users
     * @return recommended show (the most popular)
     */
    public static String popular(final Action recommendation,
                                 final List<Show> shows,
                                 final List<User> users) {
        Map<String, Integer> popularGenre = new LinkedHashMap<>();
        for (Show sh : shows) {
            for (String genre : sh.getGenres()) {
                if (popularGenre.containsKey(genre)) {
                    int value = popularGenre.get(genre);
                    popularGenre.put(genre, value + sh.getViews());
                } else {
                    popularGenre.put(genre, sh.getViews());
                }
            }
        }
        popularGenre =
                popularGenre.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        for (String g : popularGenre.keySet()) {
            for (Show sh : shows) {
                if (sh.getGenres().contains(g)) {
                    if (!findUser(recommendation.getUsername(), users)
                            .getHistory().containsKey(sh.getTitle())) {
                        return sh.getTitle();
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param recommendation action
     * @param shows list of shows
     * @param users list of users
     * @return recommended show (the most favorite)
     */
    public static String favorite(final Action recommendation,
                                  final List<Show> shows,
                                  final List<User> users) {
        Map<String, Integer> favoriteTop = new LinkedHashMap<>();
        for (Show s : shows) {
            favoriteTop.put(s.getTitle(), 0);
        }
        for (User u : users) {
            for (String fav : u.getFavoriteMovies()) {
                if (favoriteTop.containsKey(fav)) {
                    int value = favoriteTop.get(fav);
                    favoriteTop.put(fav, value + 1);
                }
            }
        }

        favoriteTop =
                favoriteTop.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        for (String sh : favoriteTop.keySet()) {
            if (!findUser(recommendation.getUsername(), users)
                    .getHistory().containsKey(sh)) {
                return sh;
            }
        }
        return null;
    }

    /**
     * @param recommendation action
     * @param shows list of shows
     * @param users list of users
     * @return list of shows which are unseen from a genre
     */
    public static ArrayList<String> search(final Action recommendation,
                                           final List<Show> shows,
                                           final List<User> users) {
        Map<String, Double> searchFilter = new LinkedHashMap<>();
        for (Show sh : shows) {
            if (sh.getGenres().contains(recommendation.getGenre())
                    && !findUser(recommendation.getUsername(), users)
                    .getHistory().containsKey(sh.getTitle())) {

                searchFilter.put(sh.getTitle(), sh.getSumOfRatings());
            }
        }
        searchFilter =
                searchFilter.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue()
                                .thenComparing(Map.Entry.comparingByKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        return new ArrayList<>(searchFilter.keySet());
    }
}
