package entities;

import common.Constants;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;

public class Action {
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
    public void solveCommand(User user, Show show, Writer fileWriter, JSONArray arrayResult){
        int value;
        JSONObject obj;
        switch (this.getType()){
            case "favorite":
                boolean found = false;
                for(int i = 0; i < user.getFavoriteMovies().size(); i++){
                    if(user.getFavoriteMovies().get(i).equals(this.title)){
                        try {
                            obj = fileWriter.writeFile(this.actionId,
                                    null,
                                    "error -> "
                                            + this.title
                                            + " is already in favourite list");
                            arrayResult.add(obj);
                        } catch (IOException e) {
                            System.out.println("not good");
                        }
                        return;
                    }
                }
                if(!user.getHistory().containsKey(this.title)){
                    try {
                        obj = fileWriter.writeFile(this.actionId,
                                null,
                                "error -> "
                                        + this.title
                                        + " is not seen");
                        arrayResult.add(obj);
                        break;
                    } catch (IOException e) {
                        System.out.println("not good 2");
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
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "view":
                if(user.getHistory().containsKey(this.title)){
                    value = user.getHistory().get(this.title);
                    user.getHistory().put(this.title, value+1);
                }
                else {
                    user.getHistory().put(this.title, 1);
                }
                show.setViews(show.getViews() + 1);
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "success -> "
                                    + this.title
                                    + " was viewed with total views of "
                                    + user.getHistory().get(this.title));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "rating":
                Map<String, Integer> mp = new HashMap<>();
                mp.put(this.title, this.getSeasonNumber());
                if(!user.getHistory().containsKey(this.title)){
                    try {
                        obj = fileWriter.writeFile(this.actionId,
                                null,
                                "error -> "
                                        + this.title
                                        + " is not seen");
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                    return;
                }
                if(user.getRating().containsKey(mp)){
                    try {
                        obj = fileWriter.writeFile(this.actionId,
                                null,
                                "error -> "
                                        + this.title
                                        + " has been already rated");
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                    break;
                }
                user.getRating().put(mp, this.grade);
                if(this.getSeasonNumber() != 0){
                    show.getSeasons().get(this.getSeasonNumber()-1).getRatings().add(this.grade);
                    try {
                        obj = fileWriter.writeFile(this.actionId,
                                null,
                                "success -> "
                                        + this.title
                                        + " was rated with "
                                        + this.grade
                                        + " by " + user.getUsername());
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                }
                else{
                    show.getRatings().add(this.grade);
                    try {
                        obj = fileWriter.writeFile(this.actionId,
                                null,
                                "success -> "
                                        + this.title
                                        + " was rated with "
                                        + this.grade
                                        + " by " + user.getUsername());
                        arrayResult.add(obj);
                    } catch (IOException e) {
                        System.out.println("not good 2");
                    }
                }

        }
    }
    public void solveQueries(User user, Show show, Writer fileWriter,
                             JSONArray arrayResult, List<Show> shows,
                             List<User> users, List<Actor> actors) {
        JSONObject obj;
        switch (this.criteria){
            case "average":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNActors(show, this, shows));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "awards":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.awards(show, this, actors));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "filter_description":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.filterDescription(show, this, actors));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "ratings":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNVideosRating(show, this, shows));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "favorite":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNVideosFavourite(this, users, shows));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "longest":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNVideosLongest(show, this, shows));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "most_viewed":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstNVideosMostViewed(show, this, shows, users));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "num_ratings":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "Query result: "
                                    + Util.firstUsers(show, this, users));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;


        }
    }

    public void solveRecommendations(User user, Show show, Writer fileWriter,
                             JSONArray arrayResult, List<Show> shows,
                             List<User> users, List<Actor> actors) {
        JSONObject obj;
        switch (this.type) {
            case "standard":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "StandardRecommendation result: "
                                    + UtilRecommendation.standard( this, shows, users));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "best_unseen":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "BestRatedUnseenRecommendation result: "
                                    + UtilRecommendation.bestUnseen( this, shows, users));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "favorite":
            case "popular":
                try {
                    obj = fileWriter.writeFile(this.actionId,
                            null,
                            "PopularRecommendation result: "
                                    + UtilRecommendation.popular( this, shows, users));
                    arrayResult.add(obj);
                } catch (IOException e) {
                    System.out.println("not good 2");
                }
                break;
            case "search":
        }
    }

}
