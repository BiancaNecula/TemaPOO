package entities;

import entertainment.Season;

import java.util.ArrayList;

public final class Movie extends Show {
    /**
     * Duration in minutes of a season
     */
    private final int duration;

    private int views = 0;

    private final ArrayList<Double> ratings = new ArrayList<>();

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
    public double getSumOfRatings() {
        if (ratings != null && ratings.size() != 0) {
            return ratings.stream().mapToDouble(Double::doubleValue).sum() / ratings.size();
        } else {
            return 0.0;
        }
    }

    @Override
    public int getViews() {
        return views;
    }

    @Override
    public void setViews(final int views) {
        this.views = views;
    }
}
