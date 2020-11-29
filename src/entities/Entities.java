package entities;

import common.Constants;
import fileio.*;
import org.json.simple.JSONArray;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Entities {
    List<Action> commands = new ArrayList<>();
    List<Action> queries = new ArrayList<>();
    List<Action> recommendations = new ArrayList<>();
    List<Actor> actors = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<Movie> movies = new ArrayList<>();
    List<Serial> serials = new ArrayList<>();
    List<Show> shows = new ArrayList<>();
    Writer fileWritter;
    JSONArray arrayResult;

    public Entities(Input input, Writer fileWriter, JSONArray arrayResult) {
        this.fileWritter = fileWriter;
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
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());;
    }

    public void solve() {
        for (int i = 0; i < commands.size(); i++) {
            commands.get(i).solveCommand(findUser(commands.get(i).getUsername()),
                    findShow(commands.get(i).getTitle()), fileWritter, arrayResult);
        }
        for (int i = 0; i < queries.size(); i++) {
            queries.get(i).solveQueries(findUser(queries.get(i).getUsername()),
                    findShow(queries.get(i).getTitle()), fileWritter, arrayResult, shows, users, actors);
        }
        for (int i = 0; i < recommendations.size(); i++) {
            recommendations.get(i).solveRecommendations(findUser(recommendations.get(i).getUsername()),
                    findShow(recommendations.get(i).getTitle()), fileWritter, arrayResult, shows, users, actors);
        }
    }

    public User findUser(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }
        return null;
    }

    public Show findShow(String title) {
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getTitle().equals(title)) {
                return movies.get(i);
            }
        }
        for (int i = 0; i < serials.size(); i++) {
            if (serials.get(i).getTitle().equals(title)) {
                return serials.get(i);
            }
        }
        return null;
    }
}
