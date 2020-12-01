package entities;

import entertainment.Season;

import java.util.ArrayList;

public final class Serial extends Show {
    /**
     * Number of seasons
     */
    private final int numberOfSeasons;
    /**
     * Season list
     */
    private final ArrayList<Season> seasons;

    /**
     * Duration in minutes of entire serial
     */
    private int duration;

    /**
     * Number of views
     */
    private int views = 0;


    public Serial(final String title, final ArrayList<String> cast,
                           final ArrayList<String> genres,
                           final int numberOfSeasons, final ArrayList<Season> seasons,
                           final int year) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public int getNumberSeason() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    @Override
    public int getViews() {
        return views;
    }

    @Override
    public void setViews(final int views) {
        this.views = views;
    }

    @Override
    public int getDuration() {
        for (Season s : this.getSeasons()) {
            duration += s.getDuration();
        }
        return duration;
    }

    @Override
    public double getSumOfRatings() {
        double avg = 0.0;
        for (Season s : this.getSeasons()) {
            avg += s.getSumOfRatings();
        }
        return avg / this.numberOfSeasons;
    }

    @Override
    public String toString() {
        return "SerialInputData{" + " title= "
                + super.getTitle() + " " + " year= "
                + super.getYear() + " cast {"
                + super.getCast() + " }\n" + " genres {"
                + super.getGenres() + " }\n "
                + " numberSeason= " + numberOfSeasons
                + ", seasons=" + seasons + "\n\n" + '}';
    }
}
