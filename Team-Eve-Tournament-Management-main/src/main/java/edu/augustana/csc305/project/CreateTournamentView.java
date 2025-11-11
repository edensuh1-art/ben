package edu.augustana.csc305.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CreateTournamentView extends VBox {
    //list of teams for the tournament
    private final ObservableList<Team> teams = FXCollections.observableArrayList();
    private final String username;
    private final String password;

    
    public CreateTournamentView(ObservableList<Tournament> store, ObservableList<Team> teams, String username, String passward) {
        this.username = username;
        this.password = passward;
		this.teams.addAll(teams);
		
        setSpacing(10);
        setPadding(new Insets(6));

        Label title = new Label("Create Tournament");

        //controls for the form, name textbox, date todays date, time default 8:00, courts spinner, sport combobox
        TextField name   = new TextField();
        name.setPromptText("Tournament Name");
        name.setPrefWidth(200);
        TextField date   = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))); // MM-dd-yyyy
        TextField time   = new TextField("08:00 PM"); // hh:mm AM/PM
        Spinner<Integer> courts = new Spinner<>(1, 32, 4);
        ComboBox<String> sport = new ComboBox<>();
        sport.getItems().addAll("Volleyball", "Soccer");
        sport.setPromptText("Select a sport");

        //Arranging the inputs with GridPane
        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(8);
        form.addRow(0, new Label("Name:"), name);
        form.addRow(1, new Label("Date (mm-dd-yyyy):"), date);
        form.addRow(2, new Label("Time (hh:mm am/pm):"), time);
        form.addRow(3, new Label("# Courts:"), courts);
        form.addRow(4, new Label("Sport"), sport); // sport selection -> determines bracket type

        //Displaying added teams, button to delete added teams
        ListView<Team> teamList = new ListView<>(teams);
        teamList.setPlaceholder(new Label("No teams yet"));
        Button removeTeam = new Button("Remove Selected");
        removeTeam.setOnAction(e -> {
            Team selected = teamList.getSelectionModel().getSelectedItem();
            if (selected != null) teams.remove(selected);
        });

        // Add team names and players list with button to submit
        TextField newTeam = new TextField();
        newTeam.setPromptText("Team name…");
        newTeam.setPrefWidth(200);
        TextField teamPlayers = new TextField();
        teamPlayers.setPrefWidth(400);
        teamPlayers.setPromptText("Optional: Player names (ex: John, Sally, Alex)");
        Button addTeam = new Button("Add Team");
        addTeam.setOnAction(e -> {
            String teamName = newTeam.getText().trim();
            if (teamName.isEmpty() || teamName.isBlank()) { 
				new Alert(Alert.AlertType.ERROR, "Team name required.").showAndWait();
                return;
            }

            String playerText = teamPlayers.getText().trim();
            List<Player> teamPlayerList = new ArrayList<>();
            if (!playerText.isEmpty() && !playerText.isBlank()){ 
                String[] playerList = playerText.split(",");
                for (String playerName : playerList){
                    if (!playerName.trim().isEmpty()){ //ignores blank names
                        teamPlayerList.add(new Player(playerName));
                    }
                }
            }

            Team team = new Team(teamName);
            team.addPlayers(teamPlayerList); //does not need a check since the list will be empty if no players are added
            teams.add(team);
            teamList.refresh();
            newTeam.clear();
            teamPlayers.clear(); 
        });

        

        Button save = new Button("Save Tournament");
        save.setDefaultButton(true);
        save.setOnAction(e -> {
            //On save- check name, date, time,
            try {
                String tournamentName = name.getText().trim();
                if (tournamentName.isEmpty()) throw new IllegalArgumentException("Name required.");
                String dateString = date.getText().trim();
                String timeString = time.getText().trim().toUpperCase();
                //assuming pm if no given am/pm
                if (!timeString.contains("AM") && !timeString.contains("PM")) timeString += " PM";
                LocalDateTime start;
                try{
                    start = LocalDateTime.parse(dateString + " " + timeString, DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a"));
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Invalid date/time formant. Use MM-dd-yyyy hh:mm AM/PM.");
                }

                int numCourts = courts.getValue();
                //check courts and teams: must have at least 2 teams per court
                if (numCourts < 1) throw new IllegalArgumentException("The number of courts must be at least 1."); //shouldn't throw because of spinner
                if (teams.size() < 2) throw new IllegalArgumentException("There must be at least 2 teams.");
                if (numCourts * 2 > teams.size()) throw new IllegalArgumentException("There must be enough teams to have at least two teams per court.");
                
                //a sport must be selected
                String sportChoice = sport.getValue();
                if (sportChoice == null || sportChoice.isBlank()) throw new IllegalArgumentException("You must select a sport.");

                //makes new tournament with name, start time, courts, teams
                Tournament tournament = createTournament(tournamentName, start, numCourts, teams, sportChoice);

				//generates pools - use to set up matches
                if (doRobin() && !tournament.getCategory().equals("Soccer")){
                    List<Pool> pools = generatePools(tournament.getCourts(), tournament.getTeams());
                    for (Pool pool : pools) pool.setID(tournament.getID() + "-" + pool.getID());
                    tournament.setPools(pools);
                    List<Match> matches = generateMatches(tournament.getPools(), tournament.getCourts(), sportChoice);
                    for (Match match : matches){
                        for (Pool pool : pools) {
                            if (pool.getTeams().contains(match.getHome())) match.setID(pool.getID() + "-" + match.getID());
                        }
                    }
                    tournament.addMatches(matches);
                } else {
                    //ignores extra teams left over from odd number of teams - extra teams are handled within the bracket
                    for (int i = 0; i + 1 < teams.size(); i += 2){
                        if (teams.get(i + 1) != null) {
                            Match match = new Match(teams.get(i), teams.get(i + 1));
                            match.setCategory(sportChoice);
                            match.setID(tournament.getID() + "-" + match.getID()); //for save file
                            tournament.addMatch(match);
                        }
                    }
                }

                store.add(tournament); //add tournament to store
                SaveLoadService.saveUser(username, passward, new ArrayList<>(store));
                clearForm(name, date, time, courts, teams); //clears or resets form
                new Alert(Alert.AlertType.INFORMATION, "Tournament has been created!").showAndWait();

            } catch (Exception ex) {
                // print full stack to console for debugging and show full exception text in alert
                ex.printStackTrace();
                String msg = ex.toString();
                new Alert(Alert.AlertType.ERROR, msg).showAndWait();
            }
        });

        getChildren().addAll(
                title,
                new Separator(),
                form,
                new Label("Teams"),
                new HBox(8, newTeam, teamPlayers, addTeam),
                teamList,
                new HBox(8, removeTeam),
                new Separator(),
                save
        );
    }

    //Helper method checks that inputs are valid, creates a tournament with appropriate number of courts
    private static Tournament createTournament(String name, LocalDateTime start, int numCourts, ObservableList<Team> teams, String sportChoice) {
        Tournament tournament = new Tournament(name, start);
        tournament.setCategory(sportChoice);
        for(Team team : teams){
            team.setID(tournament.getID() + "-" + team.getID());
            for (Player player : team.getPlayers()) {
                player.setID(team.getID() + "-" + player.getID());
            }
        }
        tournament.addTeams(teams);
        List<Court> courts = new ArrayList<>();
        for (int i = 1; i <= numCourts; i++) {
            Court court = new Court(i);
            court.setID(tournament.getID() + "-" + court.getID());
            courts.add(court);
        }
        tournament.addCourts(courts);
        return tournament;
    }

    //Gives the user the option to start the round robin or skip it
    private Boolean doRobin(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Do you want to start a round robin?");
        
        ButtonType buttonNo = new ButtonType("No");
        ButtonType buttonYes = new ButtonType("Yes");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == buttonYes;
    }

	//puts the teams into pools and returns them for the round robin
    private static List<Pool> generatePools(List<Court> courts, List<Team> teams) {
        List<Pool> pools = new ArrayList<>();
        if (courts == null || courts.isEmpty()) return pools;
        int numInPool = Math.max(2, teams.size() / courts.size());
        
        List<Team> unusedTeams = new ArrayList<>(teams);
		List<Court> unassignedCourts = new ArrayList<>(courts);
        Collections.shuffle(unusedTeams);
        Collections.shuffle(unassignedCourts);

        for (Court court : unassignedCourts){
            Pool pool = new Pool();
            pool.setCourt(court);
            for (int j = 0; j < numInPool && !unusedTeams.isEmpty(); j++){
                pool.addTeam(unusedTeams.remove(0));
                
            }
            pools.add(pool);
        }

        int poolIndex = 0;
        while (!unusedTeams.isEmpty()) {
            pools.get(poolIndex % pools.size()).addTeam(unusedTeams.remove(0));
            poolIndex++;
        }
        return pools;
    }

    //puts the teams into pools and returns them for the round robin
    private static List<Match> generateMatches(List<Pool> pools, List<Court> courts, String sportChoice) {
        List<Match> matches = new ArrayList<>();
        for (Pool pool : pools){
            List<Team> poolTeams = new ArrayList<>(pool.getTeams());
            for (int i = 0; i < poolTeams.size(); i++){
                for (int j = i + 1; j < poolTeams.size(); j++) {
                    Match match = new Match(poolTeams.get(i), poolTeams.get(j));
                    match.setCategory(sportChoice);
                    match.setAssignedCourt(pool.getCourt());
                    pool.addMatch(match);
                    matches.add(match);
                    
                }
            }
        }
        return matches;
    }

    //helper method clears or resets the form for the next tournament
    private static void clearForm(TextField name, TextField date, TextField time, Spinner<Integer> courts, ObservableList<Team> teams) {
        name.clear();
        date.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        time.setText("08:00 PM");
        courts.getValueFactory().setValue(4);
        teams.clear();
    }

    /* private static void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    } */

    public ObservableList<Team> getTeamsObservable(){
        return teams;
    }
}
