package edu.augustana.csc305.project;

import com.google.gson.Gson;
import edu.augustana.csc305.lab4.Student;

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
    
    public List<Student> loadTournaments(File loadPath) throws IOException, ClassNotFoundException {
        Gson gson = new Gson();
        Tournament[] tournaments = gson.fromJson(new FileReader(loadPath), Tournament[].class);
        return Arrays.asList(tournaments);
    }
}
