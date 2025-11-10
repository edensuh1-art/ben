package edu.augustana.csc305.project;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Box;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class CreateTournamentView extends VBox {
    //list of teams for the tournament
    private ObservableList<Team> teams = FXCollections.observableArrayList();
    
    public CreateTournamentView(ObservableList<Tournament> store) {
        setSpacing(10);
        setPadding(new Insets(6));

        Label title = new Label("Create Tournament");

        //controls for the form, name textbox, date todays date, time default 8:00, courts spinner
        TextField name   = new TextField();
        name.setPromptText("Tournament Name");
        TextField date   = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))); // mm-dd-yyyy
        TextField time   = new TextField("08:00 pm"); // hh:mm am/pm
        Spinner<Integer> courts = new Spinner<>(1, 32, 4);
        //change to reflet the new
        ChoiceBox<String> sport = new ChoiceBox<>();
        ObservableList<String> sportList = sport.getItems();
        sportList.addAll("Volleyball", "Soccer");
        sport.setValue("Select a sport");

        //Arranging the inputs with GridPane
        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(8);
        form.addRow(0, new Label("Name:"), name);
        form.addRow(1, new Label("Date (mm-dd-yyyy):"), date);
        form.addRow(2, new Label("Time (hh:mm am/pm):"), time);
        form.addRow(3, new Label("# Courts:"), courts);
        form.addRow(4, new Label("Sport"), sport); // sport selection -> determines bracket type

        // Add team names and players list with button to submit
        TextField newTeam = new TextField();
        newTeam.setPromptText("Team name…");
        TextField teamPlayers = new TextField();
        teamPlayers.setPromptText("Player names (ex: John, Sally, Alex)");
        Button addTeam = new Button("Add Team");
        addTeam.setOnAction(e -> {
            String n = newTeam.getText().trim();
            String[] playerList = teamPlayers.getText().trim().split(",");
            List<Player> teamPlayerList = new ArrayList<>();
            for (String playerName : playerList){
                if (!playerName.isBlank());teamPlayerList.add(new Player(playerName));
            }
            
            if (!n.isEmpty()) { 
				Team team = new Team(n);
                team.addPlayerList(teamPlayerList);
				teams.add(team);
				newTeam.clear();
			}
            teamPlayers.clear(); 
        });
        
        //Displaying added teams, button to delete added teams
        ListView<Team> teamList = new ListView<>(teams);
        teamList.setPlaceholder(new Label("No teams yet"));
        Button removeTeam = new Button("Remove Selected");
        removeTeam.setOnAction(e -> {
            Team sel = teamList.getSelectionModel().getSelectedItem();
            if (sel != null) teams.remove(sel);
        });

        Button save = new Button("Save Tournament");
        save.setDefaultButton(true);
        save.setOnAction(e -> {
            //On save- check name, date, time,
            try {
                String n = name.getText().trim();
                if (n.isEmpty()) throw new IllegalArgumentException("Name required.");
                String d = date.getText().trim();
                String t = time.getText().trim().toUpperCase();
                //assuming pm in no given am/pm
                if (!t.contains("AM") && !t.contains("PM")){
                    t += " PM";
                }
                LocalDateTime start = LocalDateTime.parse(d + " " + t, DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a"));

                //makes new tournament with name, start time, courts, teams
                Tournament tm = createTournament(n, start, courts.getValue(), teams);
                tm.getTeams().addAll(teams);
                if (sport.getValue() == "Select a sport") throw new IllegalArgumentException("You must select a sport.");
                tm.setCategory(sport.getValue());
				//generates pools - use to set up matches
                if (doRobin()){
                    List<Pool> pools = generatePools(tm.getCourts(), tm.getTeams());
                    tm.setPools(pools);
                    List<Match> matches = generateMatches(tm.getPools(), tm.getCourts());
                } else {
                    for (int i = 0; i < teams.size(); i += 2){
                        tm.addMatch(new Match(teams.get(i), teams.get(i + 1)));
                    }
                }

                store.add(tm); //add tournament to store
                clearForm(name, date, time, courts, teams); //clears or resets form
                new Alert(Alert.AlertType.INFORMATION, "Tournament has been created!").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        getChildren().addAll(
                title,
                new Separator(),
                form,
                new Label("Teams"),
                new HBox(8, newTeam, teamPlayers, addTeam),
                teamList,
                //new HBox(8, teamList)
                new HBox(8, removeTeam),
                new Separator(),
                save
        );
    }

    //Gives the user the option to start the round robin or skip it
    private Boolean doRobin(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Do you want to start a round robin?");
        
        ButtonType buttonNo = new ButtonType("No");
        ButtonType buttonYes = new ButtonType("Yes");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> answer = alert.showAndWait();
        if(answer.get() == buttonYes){
            return true;
        } else return false;
    
    }

    //Helper method checks that inputs are valid, creates a tournament with appropriate number of courts
    private static Tournament createTournament(String name, LocalDateTime start, int numCourts, ObservableList<Team> teams) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Your tournament doesn't have a name.");
        if (start == null) throw new IllegalArgumentException("A start time is required.");
        if (numCourts < 1) throw new IllegalArgumentException("The number of courts must be at least 1.");
		if (teams.size() < 2) throw new IllegalArgumentException("There must be at least 2 teams.");
        if (teams.size() / 2 < numCourts) throw new IllegalArgumentException("The number of courts must be at last half the number of teams. ");
        Tournament t = new Tournament();
        t.setName(name);
        t.setStart(start);
        List<Court> courts = new ArrayList<>();
        for (int i = 1; i <= numCourts; i++) courts.add(new Court(i));
        t.addCourts(courts);
        return t;
    }

	//puts the teams into pools and returns them
    private static List<Pool> generatePools(List<Court> courts, List<Team> teams) {
        List<Pool> pools = new ArrayList<>();
        int numInPool = Math.max(2, teams.size() / courts.size());
        
        List<Team> unusedTeams = new ArrayList<>(teams);
		List<Court> unassignedCourts = new ArrayList<>(courts);
        Collections.shuffle(unusedTeams);
        Collections.shuffle(unassignedCourts);
        for (Court court : unassignedCourts){
            Pool pool = new Pool();
            pool.setCourt(court);
            for (int j = 0; j < numInPool && !unusedTeams.isEmpty(); j++){
                pool.addTeam(unusedTeams.get(0));
                unusedTeams.remove(0);
                
            }
            pools.add(pool);
        }

        int poolIndex = 0;
        while (!unusedTeams.isEmpty()) {
            pools.get(poolIndex % pools.size()).addTeam(unusedTeams.get(0));
            unusedTeams.remove(0);
            poolIndex++;
        }
        return pools;
    }

    //puts the teams into pools and returns them
    private static List<Match> generateMatches(List<Pool> pools,List<Court> courts) {
        List<Match> matches = new ArrayList<>();
        for (Pool pool : pools){
            List<Team> teams = new ArrayList<>(pool.getTeams());
            for (Team homeTeam : teams){
                for (Team awayTeam : teams){
                    if (!homeTeam.equals(awayTeam)){
                        matches.add(new Match(homeTeam, awayTeam));
                    }
                }
            }
        }
        return matches;
    }

    //helper method clears or resets the form
    private static void clearForm(TextField name, TextField date, TextField time, Spinner<Integer> courts, ObservableList<Team> teams) {
        name.clear();
        date.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        time.setText("08:00 PM");
        courts.getValueFactory().setValue(4);
        teams.clear();
    }

    private static void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    public ObservableList<Team> getTeamsObservable(){
        return teams;
    }
}
