//ai generated - edits made by group

package edu.augustana.csc305.project;

import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.util.*;

public class BracketView extends GridPane {
    private List<Team> teams;
    private Map<Integer, List<Button>> rounds = new HashMap<>();

    public BracketView(List<Team> teams) {
        this.teams = teams != null ? teams : new ArrayList<>();
        drawBracket();
    }

    private void drawBracket() {
        getChildren().clear();
        rounds.clear();

        int numTeams = teams.size();
        int bracketSize = 1;
        while (bracketSize < numTeams) bracketSize *= 2;
        List<Team> orderedTeams = orderTeamsByWins(teams);
        

        List<Team> paddedTeams = new ArrayList<>(orderedTeams);
        while (paddedTeams.size() < bracketSize) paddedTeams.add(null); // bye spots

        int roundsCount = (int)(Math.log(bracketSize) / Math.log(2));

        for (int r = 0; r <= roundsCount; r++) {
            rounds.put(r, new ArrayList<>());
        }

        // first round
        for (int i = 0; i < bracketSize; i++) {
            Button btn = new Button();
            Team t = paddedTeams.get(i);
            btn.setText(t != null ? t.getName() : "BYE");
            btn.setPrefWidth(120);
            btn.setOnAction(e -> System.out.println("Team clicked: " + (t != null ? t.getName() : "BYE")));
            add(btn, 0, i * 2);
            rounds.get(0).add(btn);
        }

        // other rounds
        for (int r = 1; r <= roundsCount; r++) {
            int matches = bracketSize / (int)Math.pow(2, r);
            for (int m = 0; m < matches; m++) {
                Button btn = new Button("TBD");
                btn.setPrefWidth(120);
                add(btn, r, m * (int)Math.pow(2, r));
                rounds.get(r).add(btn);
            }
        }
    }

    public void updateWinner(int round, int matchIndex, Team winner) {
        if (!rounds.containsKey(round + 1)) return;
        int nextMatch = matchIndex / 2;
        Button nextBtn = rounds.get(round + 1).get(nextMatch);
        nextBtn.setText(winner != null ? winner.getName() : "TBD");
    }

    public static List<Team> orderTeamsByWins(List<Team> teams) {
        List<Team> ordered = new ArrayList<>(teams);
        ordered.sort((t1, t2) -> Integer.compare(t2.getWins(), t1.getWins()));
        return ordered;
    }
}
