//ai generated
package edu.augustana.csc305.project;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class SaveLoadService {
    private static final File SAVE_DIR = new File("save");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        // support Java 8 date/time types
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            MAPPER.activateDefaultTyping(MAPPER.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        } catch (NoSuchMethodError | Exception ex) {
            ex.printStackTrace();
        }
    }

    private SaveLoadService() {}

    public static class UserFile {
        public String username;
        public String password;
        public List<Tournament> tournaments = new ArrayList<>();
        public Map<String, Integer> idCounters = new HashMap<>();
        public UserFile() {}
    }

    private static String safeName(String username) {
        if (username == null) return "unknown";
        return username.replaceAll("[^A-Za-z0-9_.-]", "_");
    }

    private static File getUserFile(String username) {
        if (!SAVE_DIR.exists()) SAVE_DIR.mkdirs();
        return new File(SAVE_DIR, safeName(username) + "_tournaments.json");
    }

    public static UserFile loadUser(String username, boolean createIfMissing) throws IOException {
        File file = getUserFile(username);
        if (!file.exists()) {
            if (createIfMissing) {
                UserFile userFile = new UserFile();
                userFile.username = username;
                userFile.password = "";
                // initialize counters (optional)
                userFile.idCounters.put("tournament", 0);
                userFile.idCounters.put("team", 0);
                userFile.idCounters.put("player", 0);
                userFile.idCounters.put("match", 0);
                userFile.idCounters.put("court", 0);
                userFile.idCounters.put("pool", 0);
                saveUser(username, userFile);
                return userFile;
            } else return null;
        }

        UserFile uf = MAPPER.readValue(file, UserFile.class);

        // restore counters if present
        if (uf.idCounters != null) {
            if (uf.idCounters.containsKey("tournament")) {
                Tournament.setNextIdIndex(uf.idCounters.get("tournament"));
            }
            if (uf.idCounters.containsKey("team")) {
                Team.setNextIdIndex(uf.idCounters.get("team"));
            }
            if (uf.idCounters.containsKey("player")) {
                Player.setNextIdIndex(uf.idCounters.get("player"));
            }
            if (uf.idCounters.containsKey("match")) {
                Match.setNextIdIndex(uf.idCounters.get("match"));
            }
            if (uf.idCounters.containsKey("court")) {
                Court.setNextIdIndex(uf.idCounters.get("court"));
            }
            if (uf.idCounters.containsKey("pool")) {
                Pool.setNextIdIndex(uf.idCounters.get("pool"));
            }
        }

        // After loading tournaments, resolve internal references (team IDs -> team objects)
        if (uf.tournaments != null) {
            for (Tournament t : uf.tournaments) {
                if (t == null) continue;

                // build team map id -> Team
                Map<String, Team> teamMap = new HashMap<>();
                if (t.getTeams() != null) {
                    for (Team team : t.getTeams()) {
                        if (team != null && team.getID() != null) teamMap.put(team.getID(), team);
                    }
                }

                // resolve matches
                if (t.getMatches() != null) {
                    for (Match m : t.getMatches()) {
                        try {
                            String hid = null;
                            String aid = null;
                            // try getter names flexibly (homeId / getHomeId)
                            try { hid = (String) Match.class.getMethod("getHomeId").invoke(m); } catch (Exception ignore) {}
                            try { aid = (String) Match.class.getMethod("getAwayId").invoke(m); } catch (Exception ignore) {}
                            if (hid == null) {
                                try { Team hh = m.getHome(); if (hh != null) hid = hh.getID(); } catch (Exception ignore) {}
                            }
                            if (aid == null) {
                                try { Team aa = m.getAway(); if (aa != null) aid = aa.getID(); } catch (Exception ignore) {}
                            }
                            if (hid != null) {
                                Team home = teamMap.get(hid);
                                if (home != null) {
                                    try { Match.class.getMethod("setHome", Team.class).invoke(m, home); } catch (Exception ex) {}
                                }
                            }
                            if (aid != null) {
                                Team away = teamMap.get(aid);
                                if (away != null) {
                                    try { Match.class.getMethod("setAway", Team.class).invoke(m, away); } catch (Exception ex) {}
                                }
                            }
                        } catch (Exception ex) {
                            // be resilient — continue
                            ex.printStackTrace();
                        }
                    }
                }

                // resolve pools (teamIds -> teams) if pools exist
                if (t.getPools() != null) {
                    for (Pool p : t.getPools()) {
                        if (p == null) continue;
                        // try teamIds getter + resolve via teamMap
                        try {
                            List<String> teamIds = null;
                            try { teamIds = (List<String>) Pool.class.getMethod("getTeamIds").invoke(p); } catch (Exception ignore) {}
                            if (teamIds != null) {
                                // clear runtime list and add by ID if methods exist
                                try {
                                    Pool.class.getMethod("setMatches", List.class).invoke(p, new ArrayList<Match>());
                                } catch (Exception ignore) {}
                                p.resolveTeamsByID(teamMap);
                            } else {
                                // fallback: if Pool has getTeams and they are full objects, do nothing
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        return uf;
    }

    public static void saveUser(String username, UserFile uf) throws IOException {
        File f = getUserFile(username);
        // refresh counters
        if (uf.idCounters == null) uf.idCounters = new HashMap<>();
        uf.idCounters.put("tournament", Tournament.getNextIdIndex());
        uf.idCounters.put("team", Team.getNextIdIndex());
        uf.idCounters.put("player", Player.getNextIdIndex());
        uf.idCounters.put("match", Match.getNextIdIndex());
        uf.idCounters.put("court", Court.getNextIdIndex());
        uf.idCounters.put("pool", Pool.getNextIdIndex());

        // ensure nested objects are in serializable form:
        // matches already have homeId/awayId fields; pools have teamIds
        MAPPER.writeValue(f, uf);
    }

    public static void saveUser(String username, String password, List<Tournament> tournaments) throws IOException {
        UserFile uf = new UserFile();
        uf.username = username;
        uf.password = password;
        uf.tournaments = tournaments == null ? new ArrayList<>() : new ArrayList<>(tournaments);
        saveUser(username, uf);
    }
}
