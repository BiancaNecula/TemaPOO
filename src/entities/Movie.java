package entities;

import entertainment.Season;

import java.util.ArrayList;

public class Movie extends Show{
    /**
            * Duration in minutes of a season
     */
    private final int duration;

    private int views;

    private ArrayList<Double> ratings = new ArrayList<Double>();

    public Movie(final String title, final ArrayList<String> cast,
                          final ArrayList<String> genres, final int year,
                          final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "MovieInputData{" + "title= "
                + super.getTitle() + "year= "
                + super.getYear() + "duration= "
                + duration + "cast {"
                + super.getCast() + " }\n"
                + "genres {" + super.getGenres() + " }\n ";
    }

    public ArrayList<Season> getSeasons() {
        return null;
    }

    public ArrayList<Double> getRatings() {
        return ratings;
    }

    @Override
    public int getViews() {
        return views;
    }

    @Override
    public void setViews(int views) {
        this.views = views;
    }
}
