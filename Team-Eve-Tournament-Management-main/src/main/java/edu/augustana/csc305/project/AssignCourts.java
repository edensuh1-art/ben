package edu.augustana.csc305.project;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public final class AssignCourts {
    private AssignCourts() {}

    public static void assign(Tournament tournament) {
        //getting lists of matches and courts from Tournament object, if none - exits early
        List<Match> matches = tournament.getMatches();
        List<Court> courts = tournament.getCourts();
        if (matches == null || courts == null || matches.isEmpty() || courts.isEmpty() || tournament.getStart() == null) {
            return;
        }

        LocalDateTime start = tournament.getStart(); //start of tournament
        int numCourts = courts.size();

        //loops through matches and assigns them to a court and time
        for  (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            int courtIndex = i % numCourts;
            Court court = courts.get(courtIndex);
            int round = i /  numCourts;
            LocalDateTime matchStart = start.plusMinutes(round * 60L);
            if (match.getAssignedCourt() == -1) match.setAssignedCourt(court);
            match.setScheduledStart(matchStart);

        }
    }
}
