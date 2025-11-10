package edu.augustana.csc305.project;

import com.google.gson.Gson;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class JsonSaver{
    public void saveTournaments(File savePath, Iterable<Tournament> tournaments) throws FileNotFoundException {
        Gson gson = new Gson();
        String json = gson.toJson(tournaments);
        try (PrintWriter out = new PrintWriter(savePath)) {
            out.println(json);
        }
    }

    public List<Tournament> loadTournaments(File loadPath) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(loadPath)) {
            Tournament[] tournaments = gson.fromJson(reader, Tournament[].class);
            return Arrays.asList(tournaments);
        }
    }
}
