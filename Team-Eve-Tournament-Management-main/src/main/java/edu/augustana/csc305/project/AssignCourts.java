package edu.augustana.csc305.project;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public final class AssignCourts {
    private AssignCourts() {}

    public static void assign(Tournament t) {
        //getting lists of matches and courts from Tournament object, if none - exits early
        List<Match> matches = t.getMatches();
        List<Court> courts = t.getCourts();
        if (matches.isEmpty() || courts.isEmpty() || t.getStart() == null) return;

        LocalDateTime start = t.getStart(); //start of tournament
        int numCourts = courts.size();
        Duration matchLength = Duration.ofMinutes(60);

        //loops through matches and assigns them to a court and time
        for  (int i = 0; i < matches.size(); ++i) {
            Match m = matches.get(i);
            int courtIndex = i % numCourts;
            Court c = courts.get(courtIndex);
            int round = i /  numCourts;
            LocalDateTime matchStart = start.plus(matchLength.multipliedBy(round));
            m.setAssignedCourtNumber(c.getNumber());
            m.setScheduledStart(matchStart);

        }
    }
}
