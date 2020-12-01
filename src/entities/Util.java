package entities;

import actor.ActorsAwards;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;


/**
 *
 */
public final class Util {
    /**
     * @param name name of actor
     * @param actors list of actors
     * @return the actor with this name
     */
    public static Actor findActor(final String name, final List<Actor> actors) {
        for (Actor actor : actors) {
            if (actor.getName().equals(name)) {
                return actor;
            }
        }
        return null;
    }

    /**
     * @param query action
     * @param shows list of shows
     * @return list of first N actors
     */
    public static ArrayList<String> firstNActors(final Action query, final List<Show> shows) {
        Map<String, Integer> nrApp = new HashMap<>();
        Map<String, Double> average = new HashMap<>();

        for (Show sh : shows) {
            if (sh.getSumOfRatings() != 0) {
                for (String a : sh.getCast()) {
                    if (average.get(a) != null) {
                        average.put(a, average.get(a) + sh.getSumOfRatings());
                        nrApp.put(a, nrApp.get(a) + 1);
                    } else {
                        average.put(a, sh.getSumOfRatings());
                        nrApp.put(a, 1);
                    }
                }
            }
        }
        Map<String, Double> average2 = new HashMap<>(average);
        average2.replaceAll((k, v) -> v != null ? v / nrApp.get(k) : null);
        return getStringArrayList(query, average2.entrySet());
    }

    /**
     * @param query action
     * @param entries map for sorting
     * @return list of keys (usually strings)
     */
    private static ArrayList<String> getStringArrayList(final Action query,
                                                        final Set<Map.Entry<String,
                                                                Double>> entries) {
        Map<String, Double> average2;
        if (query.getSortType().equals("desc")) {
            average2 =
                    entries.stream()
                            .sorted(Map.Entry.<String, Double>comparingByValue()
                                    .thenComparing(Map.Entry.comparingByKey()).reversed())
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        } else {
            average2 =
                    entries.stream()
                            .sorted(Map.Entry.<String, Double>comparingByValue()
                                    .thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        return new ArrayList<>(average2.keySet());
    }

    /**
     * @param query action
     * @param actors list of actors
     * @return list of actors with awards
     */
    public static ArrayList<String> awards(final Action query, final List<Actor> actors) {
        Map<String, Integer> actorsAwards = new HashMap<>();
        ArrayList<String> awardsString = new ArrayList<>();
        for (Actor a : actors) {
            for (ActorsAwards aw : a.getAwards().keySet()) {
                awardsString.add(aw.toString());
            }
            if (awardsString.containsAll(query.getFilters().get(3))) {
               actorsAwards.put(a.getName(),
                       (int) a.getAwards().values().stream()
                               .mapToLong(Integer::longValue).sum());
            }
            awardsString.clear();
        }
        return getStrings(query, actorsAwards);

    }

    /**
     * @param query action
     * @param actors list of actors
     * @return list of actors which have some words in description
     */
    public static ArrayList<String> filterDescription(final Action query,
                                                       final List<Actor> actors) {
        ArrayList<String> actorsWithFilteredDescription = new ArrayList<>();
        for (Actor a : actors) {
            boolean ok = true;
            String description = a.getCareerDescription()
                    .replaceAll("[,.-]", " ");
            for (String item : query.getFilters().get(2)) {
                if (!(description.toLowerCase().contains(" " + item + " "))) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                actorsWithFilteredDescription.add(a.getName());
            }
        }
        if (query.getSortType().equals("desc")) {
            actorsWithFilteredDescription.sort(Collections.reverseOrder());
        } else {
            Collections.sort(actorsWithFilteredDescription);
        }
        return actorsWithFilteredDescription;

    }

    /**
     * @param query action
     * @param shows list of shows
     * @return list of first N shows after rating
     */
    public static ArrayList<String> firstNVideosRating(final Action query,
                                                       final List<Show> shows) {
        Map<String, Double> ratings = new HashMap<>();
        for (Show sh : shows) {
                if ((query.getObjectType().equals("movies")
                        && sh.getSeasons() == null)
                        || (query.getObjectType().equals("shows")
                        && sh.getSeasons() != null)) {
                    if (query.getFilters().get(0).get(0) != null
                            && query.getFilters().get(1).get(0) != null) {
                        if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                                && sh.getYear()
                                == Integer.parseInt(query.getFilters().get(0).get(0))) {
                            if (sh.getSumOfRatings() != 0) {
                                ratings.put(sh.getTitle(), sh.getSumOfRatings());
                            }
                        }
                    } else if (query.getFilters().get(0).get(0) == null
                            && query.getFilters().get(1).get(0) != null) {
                        if (sh.getGenres().contains(query.getFilters().get(1).get(0))) {
                            if (sh.getSumOfRatings() != 0) {
                                ratings.put(sh.getTitle(), sh.getSumOfRatings());
                            }
                        }
                    }
                }

        }

        return getStringArrayList(query, ratings.entrySet());

    }

    /**
     * @param query action
     * @param users list of users
     * @param shows list of shows
     * @return list of first N shows which are the most favourite
     */
    public static ArrayList<String> firstNVideosFavourite(final Action query,
                                                          final List<User> users,
                                                          final List<Show> shows) {
        Map<String, Integer> nrAppearances = new HashMap<>();
        for (User u : users) {
            for (String s : u.getFavoriteMovies()) {
                for (Show sh : shows) {
                    if ((query.getObjectType().equals("movies")
                            && sh.getSeasons() == null)
                            || (query.getObjectType().equals("shows")
                                    && sh.getSeasons() != null)) {
                        if (sh.getTitle().equals(s)) {
                            if (query.getFilters().get(0).get(0) != null
                                    && query.getFilters().get(1).get(0) != null) {
                                if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                                        && sh.getYear()
                                        == Integer.parseInt(query.getFilters().get(0).get(0))) {
                                    if (nrAppearances.containsKey(s)) {
                                        int value = nrAppearances.get(s);
                                        nrAppearances.put(s, value + 1);
                                    } else {
                                        nrAppearances.put(s, 1);
                                    }
                                }
                            } else if (query.getFilters().get(0).get(0) != null
                                    && query.getFilters().get(1).get(0) == null) {
                                if (sh.getYear()
                                        == Integer.parseInt(query.getFilters().get(0).get(0))) {
                                    if (nrAppearances.containsKey(s)) {
                                        int value = nrAppearances.get(s);
                                        nrAppearances.put(s, value + 1);
                                    } else {
                                        nrAppearances.put(s, 1);
                                    }
                                }
                            } else if (query.getFilters().get(1).get(0) != null
                                    && query.getFilters().get(0).get(0) == null) {
                                if (sh.getGenres().contains(query.getFilters().get(1).get(0))) {
                                    if (nrAppearances.containsKey(s)) {
                                        int value = nrAppearances.get(s);
                                        nrAppearances.put(s, value + 1);
                                    } else {
                                        nrAppearances.put(s, 1);
                                    }
                                }
                            } else {
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
        return getStrings(query, nrAppearances);
    }

    /**
     * @param query action
     * @param shows list of shows
     * @return list of first N videos which are the longest
     */
    public static ArrayList<String> firstNVideosLongest(final Action query,
                                                        final List<Show> shows) {
        Map<String, Integer> duration = new HashMap<>();
        for (Show sh : shows) {
            if ((query.getObjectType().equals("movies") && sh.getSeasons() == null)
                    || (query.getObjectType().equals("shows") && sh.getSeasons() != null)) {
                if (query.getFilters().get(0).get(0) != null
                        && query.getFilters().get(1).get(0) != null) {
                    if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                            && sh.getYear()
                            == Integer.parseInt(query.getFilters().get(0).get(0))) {
                        duration.put(sh.getTitle(), sh.getDuration());
                    }
                } else if (query.getFilters().get(0).get(0) != null
                        && query.getFilters().get(1).get(0) == null) {
                    if (sh.getYear()
                            == Integer.parseInt(query.getFilters().get(0).get(0))) {
                        duration.put(sh.getTitle(), sh.getDuration());
                    }
                } else if (query.getFilters().get(0).get(0) == null
                        && query.getFilters().get(1).get(0) != null) {
                    if (sh.getGenres().contains(query.getFilters().get(1).get(0))) {
                        duration.put(sh.getTitle(), sh.getDuration());
                    }
                } else {
                    duration.put(sh.getTitle(), sh.getDuration());
                }
            }
        }

        return getStrings(query, duration);

    }

    /**
     * @param title title of show
     * @param shows list of shows
     * @return the show which have this title
     */
    public static Show findShow(final String title, final List<Show> shows) {
        for (Show show : shows) {
            if (show.getTitle().equals(title)) {
                return show;
            }
        }
        return null;
    }

    /**
     * @param query action
     * @param shows list of shows
     * @param users list of users
     * @return list of first N shows most viewed
     */
    public static ArrayList<String> firstNVideosMostViewed(final Action query,
                                                           final List<Show> shows,
                                                           final List<User> users) {
        Map<String, Integer> views = new HashMap<>();
        for (User u : users) {
            for (String s : u.getHistory().keySet()) {
                if (findShow(s, shows).getViews() != 0) {
                    findShow(s, shows).setViews(findShow(s, shows)
                            .getViews() + u.getHistory().get(s));
                } else {
                    findShow(s, shows).setViews(u.getHistory().get(s));
                }
            }
        }

        for (Show sh : shows) {
            if (sh.getViews() != 0
                    && ((query.getObjectType().equals("movies") && sh.getSeasons() == null)
                    || (query.getObjectType().equals("shows") && sh.getSeasons() != null))) {
                if (query.getFilters().get(0).get(0) != null
                        && query.getFilters().get(1).get(0) != null) {
                    if (sh.getGenres().contains(query.getFilters().get(1).get(0))
                            && sh.getYear() == Integer.parseInt(query.getFilters().get(0).get(0))) {
                        views.put(sh.getTitle(), sh.getViews());
                    }
                } else if (query.getFilters().get(0).get(0) != null
                        && query.getFilters().get(1).get(0) == null) {
                    if (sh.getYear() == Integer.parseInt(query.getFilters().get(0).get(0))) {
                        views.put(sh.getTitle(), sh.getViews());
                    }
                } else if (query.getFilters().get(0).get(0) == null
                        && query.getFilters().get(1).get(0) != null) {
                    if (sh.getGenres().contains(query.getFilters().get(1).get(0))) {
                        views.put(sh.getTitle(), sh.getViews());
                    }
                }
            }
        }
        return getStrings(query, views);

    }

    /**
     * @param query action
     * @param users list of users
     * @return list of first N users which gave more ratings
     */
    public static ArrayList<String> firstUsers(final Action query, final List<User> users) {
        Map<String, Integer> first = new HashMap<>();
        for (User u : users) {
            if (u.getRating().size() != 0) {
                first.put(u.getUsername(), u.getRating().size());
            }
        }
        return getStrings(query, first);
    }

    /**
     * @param query action
     * @param first map for sorting
     * @return sorted list of keys
     */
    private static ArrayList<String> getStrings(final Action query,
                                                final Map<String, Integer> first) {
        Map<String, Integer> firstN;
        if (query.getSortType().equals("desc")) {
            firstN =
                    first.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue()
                                    .thenComparing(Map.Entry.comparingByKey()).reversed())
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        } else {
            firstN =
                    first.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue()
                                    .thenComparing(Map.Entry.comparingByKey()))
                            .limit(query.getNumber())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        return new ArrayList<>(firstN.keySet());
    }
}
