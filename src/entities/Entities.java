package entities;

import common.Constants;

import fileio.Writer;
import fileio.Input;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Entities {
    private final List<Action> commands = new ArrayList<>();
    private final List<Action> queries = new ArrayList<>();
    private final List<Action> recommendations = new ArrayList<>();
    private final List<Actor> actors = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Movie> movies = new ArrayList<>();
    private final List<Serial> serials = new ArrayList<>();
    private final List<Show> shows;
    private final Writer fileWriter;
    private final JSONArray arrayResult;

    public Entities(final Input input, final Writer fileWriter, final JSONArray arrayResult) {
        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
        for (int i = 0; i < input.getUsers().size(); i++) {
            users.add(new User(input.getUsers().get(i).getUsername(),
                    input.getUsers().get(i).getSubscriptionType(),
                    input.getUsers().get(i).getHistory(),
                    input.getUsers().get(i).getFavoriteMovies()));
        }
        for (int i = 0; i < input.getActors().size(); i++) {
            actors.add(new Actor(input.getActors().get(i).getName(),
                    input.getActors().get(i).getCareerDescription(),
                    input.getActors().get(i).getFilmography(),
                    input.getActors().get(i).getAwards()));
        }
        for (int i = 0; i < input.getMovies().size(); i++) {
            movies.add(new Movie(input.getMovies().get(i).getTitle(),
                    input.getMovies().get(i).getCast(),
                    input.getMovies().get(i).getGenres(),
                    input.getMovies().get(i).getYear(),
                    input.getMovies().get(i).getDuration()));
        }
        for (int i = 0; i < input.getSerials().size(); i++) {
            serials.add(new Serial(input.getSerials().get(i).getTitle(),
                    input.getSerials().get(i).getCast(),
                    input.getSerials().get(i).getGenres(),
                    input.getSerials().get(i).getNumberSeason(),
                    input.getSerials().get(i).getSeasons(),
                    input.getSerials().get(i).getYear()));
        }
        for (int i = 0; i < input.getCommands().size(); i++) {
            String actionType = input.getCommands().get(i).getActionType();
            switch (actionType) {
                case Constants.RECOMMENDATION:
                    recommendations.add(new Action(input.getCommands().get(i).getActionId(),
                            input.getCommands().get(i).getActionType(),
                            input.getCommands().get(i).getType(),
                            input.getCommands().get(i).getUsername(),
                            input.getCommands().get(i).getGenre()));
                    break;
                case Constants.QUERY:
                    queries.add(new Action(input.getCommands().get(i).getActionId(),
                            input.getCommands().get(i).getActionType(),
                            input.getCommands().get(i).getObjectType(),
                            input.getCommands().get(i).getFilters().get(1).get(0),
                            input.getCommands().get(i).getSortType(),
                            input.getCommands().get(i).getCriteria(),
                            input.getCommands().get(i).getFilters().get(0).get(0),
                            input.getCommands().get(i).getNumber(),
                            input.getCommands().get(i).getFilters().get(2),
                            input.getCommands().get(i).getFilters().get(3)));
                    break;
                case Constants.COMMAND:
                    commands.add(new Action(input.getCommands().get(i).getActionId(),
                            input.getCommands().get(i).getActionType(),
                            input.getCommands().get(i).getType(),
                            input.getCommands().get(i).getUsername(),
                            input.getCommands().get(i).getTitle(),
                            input.getCommands().get(i).getGrade(),
                            input.getCommands().get(i).getSeasonNumber()));
                    break;
                default:
            }
        }
        shows = Stream.of(movies, serials)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param input input
     */
    public void solve(final Input input) {
        int j = 0, z = 0, t = 0;
        for (int i = 0; i < input.getCommands().size(); i++) {
            String actionType = input.getCommands().get(i).getActionType();
            switch (actionType) {
                case Constants.COMMAND -> {
                    commands.get(j).solveCommand(findUser(commands.get(j).getUsername()),
                            findShow(commands.get(j).getTitle()), fileWriter, arrayResult);
                    j++;
                }
                case Constants.QUERY -> {
                    queries.get(z).solveQueries(fileWriter, arrayResult, shows, users, actors);
                    z++;
                }
                case Constants.RECOMMENDATION -> {
                    recommendations.get(t).solveRecommendations(
                            findUser(recommendations.get(t).getUsername()),
                            fileWriter, arrayResult, shows, users);
                    t++;
                }
                default -> { }
            }

        }
    }

    /**
     * @param username user's name
     * @return the user with this name
     */
    public User findUser(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * @param title title of show
     * @return the show with this title
     */
    public Show findShow(final String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        for (Serial serial : serials) {
            if (serial.getTitle().equals(title)) {
                return serial;
            }
        }
        return null;
    }
}
