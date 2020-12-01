package entities;

import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;


public final class Action {
    /**
     * Action id
     */
    private final int actionId;
    /**
     * Type of action
     */
    private final String actionType;
    /**
     * Used for commands
     */
    private final String type;
    /**
     * Username of user
     */
    private final String username;
    /**
     * The type of object on which the actions will be performed
     */
    private final String objectType;
    /**
     * Sorting type: ascending or descending
     */
    private final String sortType;
    /**
     * The criterion according to which the sorting will be performed
     */
    private final String criteria;
    /**
     * Video title
     */
    private final String title;
    /**
     * Video genre
     */
    private final String genre;
    /**
     * Query limit
     */
    private final int number;
    /**
     * Grade for rating - aka value of the rating
     */
    private final double grade;
    /**
     * Season number
     */
    private final int seasonNumber;
    /**
     * Filters used for selecting videos
     */
    private final List<List<String>> filters = new ArrayList<>();

    protected Action(final int actionId, final String actionType,
                              final String type, final String username, final String genre) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.type = type;
        this.username = username;
        this.genre = genre;
        this.objectType = null;
        this.sortType = null;
        this.criteria = null;
        this.number = 0;
        this.title = null;
        this.grade = 0;
        this.seasonNumber = 0;
    }

    public Action(final int actionId, final String actionType, final String objectType,
                           final String genre, final String sortType, final String criteria,
                           final String year, final int number, final List<String> words,
                           final List<String> awards) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.objectType = objectType;
        this.sortType = sortType;
        this.criteria = criteria;
        this.number = number;
        this.filters.add(new ArrayList<>(Collections.singleton(year)));
        this.filters.add(new ArrayList<>(Collections.singleton(genre)));
        this.filters.add(words);
        this.filters.add(awards);
        this.title = null;
        this.type = null;
        this.username = null;
        this.genre = null;
        this.grade = 0;
        this.seasonNumber = 0;
    }

    public Action(final int actionId, final String actionType, final String type,
                           final String username, final String title, final Double grade,
                           final int seasonNumber) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.type = type;
        this.grade = grade;
        this.username = username;
        this.title = title;
        this.seasonNumber = seasonNumber;
        this.genre = null;
        this.objectType = null;
        this.sortType = null;
        this.criteria = null;
        this.number = 0;
    }

    public int getActionId() {
        return actionId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getSortType() {
        return sortType;
    }

    public String getCriteria() {
        return criteria;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getNumber() {
        return number;
    }

    public double getGrade() {
        return grade;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public List<List<String>> getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return "ActionInputData{"
                + "actionId=" + actionId
                + ", actionType='" + actionType + '\''
                + ", type='" + type + '\''
                + ", username='" + username + '\''
                + ", objectType='" + objectType + '\''
                + ", sortType='" + sortType + '\''
                + ", criteria='" + criteria + '\''
                + ", title='" + title + '\''
                + ", genre='" + genre + '\''
                + ", number=" + number
                + ", grade=" + grade
                + ", seasonNumber=" + seasonNumber
                + ", filters=" + filters
                + '}' + "\n";
    }

    /**
     * @param user this user
     * @param show this show
     * @param fileWriter writer
     * @param arrayResult for writing as JSON
     */
    public void solveCommand(final User user, final Show show, final Writer fileWriter,
                             final JSONArray arrayResult) {
        int value;
        JSONObject obj;
        switch (this.getType()) {
            case "favorite" -> {
                for (int i = 0; i < user.getFavoriteMovies().size(); i++) {
                    if (user.getFavoriteMovies().get(i).equals(this.title)) {
                        try {
                            obj = fileWriter.writeFile(this.actionId,
                                    null,
                                    "error -> "
                                            + this.title
                                            + " is already in favourite list");
                            arrayResult.add(obj);
                        } catch (IOException ignored) {
                        }
                        return;
                    }
                }
                if (!user.getHistory().containsKey(this.title)) {
                    try {
                        obj = fileWriter.writeFile(this.actionId,
                                null,
                                "error -> "
                                        + this.title
                                        + " is not seen");
                        arrayResult.add(obj);
                        break;
                    } catch (IOException ignored) {
                    }
                }
                user.getFavoriteMovies().add(this.title);
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "success -> "
                                    + this.title
                                    + " was added as favourite");
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
            }
            case "view" -> {
                if (user.getHistory().containsKey(this.title)) {
                    value = user.getHistory().get(this.title);
                    user.getHistory().put(this.title, value + 1);
                } else {
                    user.getHistory().put(this.title, 1);
                }
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "success -> "
                                    + this.title
                                    + " was viewed with total views of "
                                    + user.getHistory().get(this.title));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
            }
            case "rating" -> {
                Map<String, Integer> mp = new HashMap<>();
                mp.put(this.title, this.getSeasonNumber());
                if (!user.getHistory().containsKey(this.title)) {
                    try {
                        obj = fileWriter.writeFile(this.actionId,
                                null,
                                "error -> "
                                        + this.title
                                        + " is not seen");
                        arrayResult.add(obj);
                    } catch (IOException ignored) {
                    }
                    return;
                }
                if (user.getRating().containsKey(mp)) {
                    try {
                        obj = fileWriter.writeFile(this.actionId,
                                null,
                                "error -> "
                                        + this.title
                                        + " has been already rated");
                        arrayResult.add(obj);
                    } catch (IOException ignored) {
                    }
                    break;
                }
                user.getRating().put(mp, this.grade);
                if (this.getSeasonNumber() != 0) {
                    show.getSeasons().get(this.getSeasonNumber() - 1).getRatings().add(this.grade);
                } else {
                    show.getRatings().add(this.grade);
                }
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "success -> "
                                    + this.title
                                    + " was rated with "
                                    + this.grade
                                    + " by " + user.getUsername());
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
            }
            default -> {
            }
        }
    }

    /**
     * @param fileWriter writer
     * @param arrayResult for writing as JSON
     * @param shows list of shows
     * @param users list of users
     * @param actors list of actors
     */
    public void solveQueries(final Writer fileWriter,
                             final JSONArray arrayResult, final List<Show> shows,
                             final List<User> users, final List<Actor> actors) {
        JSONObject obj;
        switch (this.criteria) {
            case "average":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNActors(this, shows));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
                break;
            case "awards":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.awards(this, actors));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
                break;
            case "filter_description":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.filterDescription(this, actors));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
                break;
            case "ratings":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNVideosRating(this, shows));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
                break;
            case "favorite":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNVideosFavourite(this, users, shows));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
                break;
            case "longest":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNVideosLongest(this, shows));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
                break;
            case "most_viewed":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNVideosMostViewed(this, shows, users));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
                break;
            case "num_ratings":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstUsers(this, users));
                    arrayResult.add(obj);
                } catch (IOException ignored) {
                }
                break;


            default:
        }
    }

    /**
     * @param user this user
     * @param fileWriter writer
     * @param arrayResult for writing as JSON
     * @param shows list of shows
     * @param users list of users
     */
    public void solveRecommendations(final User user, final Writer fileWriter,
                             final JSONArray arrayResult, final List<Show> shows,
                             final List<User> users) {
        JSONObject obj;
        switch (this.type) {
            case "standard":
                if (UtilRecommendation.standard(this, shows, users) == null) {
                    try {
                        obj = fileWriter.writeFile(this.actionId, null,
                                "StandardRecommendation cannot be applied!");
                        arrayResult.add(obj);
                    } catch (IOException ignored) {
                    }
                } else {
                    try {
                        obj = fileWriter.writeFile(this.actionId, null,
                                "StandardRecommendation result: "
                                        + UtilRecommendation.standard(this, shows, users));
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                }
                break;
            case "best_unseen":
                if (UtilRecommendation.bestUnseen(this, shows, users) == null) {
                    try {
                        obj = fileWriter.writeFile(this.actionId, null,
                                "BestRatedUnseenRecommendation cannot be applied!");
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                } else {
                    try {
                        obj = fileWriter.writeFile(this.actionId, null,
                                "BestRatedUnseenRecommendation result: "
                                        + UtilRecommendation.bestUnseen(this, shows, users));
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                }
                break;
            case "favorite":
                if (user.getSubscriptionType().equals("PREMIUM")) {
                    if (UtilRecommendation.favorite(this, shows, users) == null) {
                        try {
                            obj = fileWriter.writeFile(this.actionId, null,
                                    "FavoriteRecommendation cannot be applied!");
                            arrayResult.add(obj);
                        } catch (IOException e) {
                            System.out.println("not good 2");
                        }
                    } else {
                        try {
                            obj = fileWriter.writeFile(this.actionId, null,
                                    "FavoriteRecommendation result: "
                                            + UtilRecommendation.favorite(this, shows, users));
                            arrayResult.add(obj);
                        } catch (IOException e) {
                            System.out.println("not good 2");
                        }
                    }
                } else {
                    try {
                        obj = fileWriter.writeFile(this.actionId, null,
                                "FavoriteRecommendation cannot be applied!");
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                }
                break;
            case "popular":
                if (user.getSubscriptionType().equals("PREMIUM")) {
                    if (UtilRecommendation.popular(this, shows, users) == null) {
                        try {
                            obj = fileWriter.writeFile(this.actionId, null,
                                    "PopularRecommendation cannot be applied!");
                            arrayResult.add(obj);
                        } catch (IOException e) {
                            System.out.println("not good 2");
                        }
                    } else {
                        try {
                            obj = fileWriter.writeFile(this.actionId, null,
                                    "PopularRecommendation result: "
                                            + UtilRecommendation.popular(this, shows, users));
                            arrayResult.add(obj);
                        } catch (IOException e) {
                            System.out.println("not good 2");
                        }
                    }
                } else {
                    try {
                        obj = fileWriter.writeFile(this.actionId, null,
                                "PopularRecommendation cannot be applied!");
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                }
                break;
            case "search":
                if (user.getSubscriptionType().equals("PREMIUM")) {
                    if (UtilRecommendation.search(this, shows, users).isEmpty()) {
                        try {
                            obj = fileWriter.writeFile(this.actionId, null,
                                    "SearchRecommendation cannot be applied!");
                            arrayResult.add(obj);
                        } catch (IOException e) {
                            System.out.println("not good 2");
                        }
                    } else {
                        try {
                            obj = fileWriter.writeFile(this.actionId, null,
                                    "SearchRecommendation result: "
                                            + UtilRecommendation.search(this, shows, users));
                            arrayResult.add(obj);
                        } catch (IOException e) {
                            System.out.println("not good 2");
                        }
                    }
                } else {
                    try {
                        obj = fileWriter.writeFile(this.actionId, null,
                                "SearchRecommendation cannot be applied!");
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                }
                break;
            default:
        }
    }

}
