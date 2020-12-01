package entities;

import entertainment.Season;

import java.util.ArrayList;

public abstract class Show {
    /**
     * Show's title
     */
    private final String title;
    /**
     * The year the show was released
     */
    private final int year;
    /**
     * Show casting
     */
    private final ArrayList<String> cast;
    /**
     * Show genres
     */
    private final ArrayList<String> genres;

    /**
     * duration of show
     */
    private int duration;

    /**
     * number of views
     */
    private int views = 0;

    private ArrayList<Double> ratings = new ArrayList<>();

    public Show(final String title, final int year,
                     final ArrayList<String> cast, final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }

    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final ArrayList<String> getCast() {
        return cast;
    }

    public final ArrayList<String> getGenres() {
        return genres;
    }

    /**
     * @return list of seasons
     */
    public ArrayList<Season> getSeasons() {
        return null;
    }

    /**
     * @return list of ratings
     */
    public ArrayList<Double> getRatings() {
        return ratings;
    }

    /**
     * @return duration in minutes of show
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @return number of views
     */
    public int getViews() {
        return views;
    }

    /**
     * @param views number of views
     */
    public void setViews(final int views) {
        this.views = views;
    }

    /**
     * @return average of ratings
     */
    public double getSumOfRatings() {
        if (ratings != null) {
            return ratings.stream().mapToDouble(Double::doubleValue).sum() / ratings.size();
        } else {
            return 0.0;
        }
    }
}
