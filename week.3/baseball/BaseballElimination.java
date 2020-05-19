/* *****************************************************************************
 *  Name: Alireza Ghey
 *  Date: 19-05-2020
 *  Description: Solution to the baseball elimination problem based on
 *  the Ford-Fulkerson algorithm
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BaseballElimination {
    private int numOfTeams;
    private Team[] teams;
    private Map<String, Integer> teamNames;
    private int maxWinsInTeams = 0;
    private int teamWithMaxWins = 0;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(
            String filename) {
        readTeams(filename);


    }

    private void readTeams(String filename) {
        if (filename == null || filename.length() == 0) throw new IllegalArgumentException();

        In in = new In(filename);
        numOfTeams = Integer.parseInt(in.readLine());

        teams = new Team[numOfTeams];
        teamNames = new HashMap<>();

        for (int i = 0; i < numOfTeams; i++) {
            String name = in.readString();
            int wins = in.readInt();
            int losses = in.readInt();
            int remainingGames = in.readInt();

            Team team = new Team(name, wins, losses, remainingGames, numOfTeams);
            for (int j = 0; j < numOfTeams; j++) {
                team.games[j] = in.readInt();
            }

            teamNames.put(name, i);
            teams[i] = team;

            if (team.wins > maxWinsInTeams) {
                maxWinsInTeams = team.wins;
                teamWithMaxWins = i;
            }
        }


    }

    // number of teams
    public int numberOfTeams() {
        return numOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return teamNames.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!teamNames.containsKey(team)) throw new IllegalArgumentException();
        return teams[teamNames.get(team)].wins;
    }

    // number of losses for given team
    public int losses(String team) {
        if (!teamNames.containsKey(team)) throw new IllegalArgumentException();
        return teams[teamNames.get(team)].losses;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!teamNames.containsKey(team)) throw new IllegalArgumentException();
        return teams[teamNames.get(team)].remainingGames;
    }

    // number of remaining games between team1 and team2
    public int against(String team1,
                       String team2) {
        if (!teamNames.containsKey(team1) || !teamNames.containsKey(team2))
            throw new IllegalArgumentException();

        return teams[teamNames.get(team1)].games[teamNames.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String teamName) {
        if (!teamNames.containsKey(teamName)) throw new IllegalArgumentException();
        Team team = teams[teamNames.get(teamName)];
        if (team.status == TeamStatus.undefined) {
            calcTeamStatus(teamNames.get(teamName));
        }
        return team.status == TeamStatus.eliminated;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(
            String teamName) {
        if (!teamNames.containsKey(teamName)) throw new IllegalArgumentException();
        Team team = teams[teamNames.get(teamName)];
        if (team.status == TeamStatus.undefined) {
            calcTeamStatus(teamNames.get(teamName));
        }
        return team.eliminationCertificate;
    }

    private boolean isTriviallyEliminated(int teamNum) {
        int maxPossibleWins = teams[teamNum].wins + teams[teamNum].remainingGames;
        if (maxPossibleWins < maxWinsInTeams) {
            teams[teamNum].status = TeamStatus.eliminated;
            ArrayList<String> eliminationCertificate = new ArrayList<>();
            eliminationCertificate.add(teams[teamWithMaxWins].name);
            teams[teamNum].eliminationCertificate = eliminationCertificate;

            // printEliminationStatus(teamNum);
            return true;
        }
        else return false;
    }

    private void calcTeamStatus(int teamNum) {
        if (isTriviallyEliminated(teamNum)) return;
        // int numOfTeamVertices;
        // int numOfTeamPairVertices;
        Set<Integer> teamsWithGames = new HashSet<>();
        Set<TeamPair> teamPairsWithGames = new HashSet<>();

        for (int i = 0; i < teams.length; i++) {
            if (i == teamNum) continue;
            Team currTeam = teams[i];
            for (int j = i + 1; j < currTeam.games.length; j++) {
                if (j == teamNum) continue;
                if (currTeam.games[j] > 0) {
                    teamsWithGames.add(i);
                    teamsWithGames.add(j);
                    teamPairsWithGames.add(new TeamPair(i, j));
                }
            }
        }
        // The # of vertices in a baseball elimination network is as follows:
        // 1- # of team pairs that have remaining games
        // (regardless of the number of the remaining games) except for the team we are checking for elimination
        // 2- # of unique teams that have remaining games according to the previous filter
        // 3- 2 artificial vertices for source (s) and sink (t)
        FlowNetwork fNetwork = new FlowNetwork(
                teamPairsWithGames.size() + teamsWithGames.size() + 2);
        Map<Integer, Integer> teamToVertice = new HashMap<>();
        // Map<Integer, Integer> verticeToTeam = new HashMap<>();
        int i = 0;
        for (int team : teamsWithGames) {
            teamToVertice.put(team, teamPairsWithGames.size() + i);
            // verticeToTeam.put(teamPairsWithGames.size() + i, team);
            i++;
        }

        // System.out.println(teamsWithGames);
        // System.out.println(teamPairsWithGames);


        // By convention we take second to last and last vertices
        // in the flowNetwork as source (s) and sink (t) respectively
        int s = fNetwork.V() - 2;
        int t = fNetwork.V() - 1;

        // We dedicate the first numOfTeamPairs vertices in the flow network
        // to the team pair vertices that connect to source(s)
        // Then, the next vertices belong to the teams themselves
        int j = 0;
        for (TeamPair tp : teamPairsWithGames) {
            int gamesLeft = teams[tp.team1].games[tp.team2];
            FlowEdge sourceToTeamPair = new FlowEdge(s, j, gamesLeft);
            FlowEdge team1ToTeamVertex = new FlowEdge(j, teamToVertice.get(tp.team1),
                                                      Double.POSITIVE_INFINITY);
            FlowEdge team2ToTeamVertex = new FlowEdge(j, teamToVertice.get(tp.team2),
                                                      Double.POSITIVE_INFINITY);
            fNetwork.addEdge(sourceToTeamPair);
            fNetwork.addEdge(team1ToTeamVertex);
            fNetwork.addEdge(team2ToTeamVertex);
            j++;
        }

        // Adding team to sink (t) edges
        for (int team : teamToVertice.keySet()) {
            int vertice = teamToVertice.get(team);
            int capacity = teams[teamNum].wins + teams[teamNum].remainingGames - teams[team].wins;
            FlowEdge teamToSink = new FlowEdge(vertice, t, capacity);
            fNetwork.addEdge(teamToSink);
        }

        // System.out.println(fNetwork.toString());

        FordFulkerson ff = new FordFulkerson(fNetwork, s, t);
        // System.out.println(ff.value());

        ArrayList<String> eliminationCertificate = new ArrayList<>();
        teams[teamNum].status = TeamStatus.notEliminated;
        for (int k = 0; k < teams.length; k++) {
            if (teamToVertice.containsKey(k)) {
                // System.out.println(ff.inCut(teamToVertice.get(k)));
                if (ff.inCut(teamToVertice.get(k))) {
                    teams[teamNum].status = TeamStatus.eliminated;
                    eliminationCertificate.add(teams[k].name);
                }
            }
        }
        if (teams[teamNum].status == TeamStatus.eliminated) {
            teams[teamNum].eliminationCertificate = eliminationCertificate;
        }

        // printEliminationStatus(teamNum);

    }

    private void printEliminationStatus(int teamNum) {
        String eliminated = teams[teamNum].status == TeamStatus.eliminated ? "Yes" : "No";
        System.out.println(
                String.format("Is team %s eliminated? %s", teams[teamNum].name, eliminated));
        System.out.println(String.format("Certificate of elimination: %s",
                                         teams[teamNum].eliminationCertificate));
    }

    private class TeamPair implements Comparable<TeamPair> {
        private int team1;
        private int team2;

        public TeamPair(int team1, int team2) {
            this.team1 = team1;
            this.team2 = team2;
        }

        public int compareTo(TeamPair that) {
            if (this.team1 < that.team1) return -1;
            if (this.team1 > that.team1) return 1;
            if (this.team2 < that.team2) return -1;
            if (this.team2 > that.team2) return 1;
            return 0;
        }

        // Note:
        // Since team1 playing with team2 is practically the same as
        // team2 playing with team1, equals and hashCode are implemented
        // to reflect this equality
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TeamPair teamPair = (TeamPair) obj;
            return (team1 == teamPair.team1 && team2 == teamPair.team2) ||
                    (team1 == teamPair.team2 && team2 == teamPair.team1);
        }

        public int hashCode() {
            return Objects.hash(team1) + Objects.hash(team2);
        }

        public String toString() {
            return String.format("%-5s %-5s", team1, team2);
        }
    }

    private enum TeamStatus {
        undefined, eliminated, notEliminated
    }

    private class Team {
        private String name;
        private int wins;
        private int losses;
        private int remainingGames;
        private Integer[] games;
        private TeamStatus status;
        private ArrayList<String> eliminationCertificate;

        public Team(String name, int wins, int losses, int remainingGames, int numOfTeams) {
            this.name = name;
            this.wins = wins;
            this.losses = losses;
            this.remainingGames = remainingGames;
            this.games = new Integer[numOfTeams];
            this.status = TeamStatus.undefined;
        }

        @Override
        public String toString() {
            List<Integer> gm = Arrays.<Integer>asList((games));
            return String.format(
                    "%-20s %-5s %-5s %-5s %-5s", name, wins, losses, remainingGames,
                    gm);
        }
    }

    public static void main(String[] args) {
        BaseballElimination bEl = new BaseballElimination(args[0]);
        int teamNum = Integer.parseInt(args[1]);
        for (Team team : bEl.teams) System.out.println(team);

        bEl.calcTeamStatus(teamNum);

        // System.out.println(bEl.);
    }
}
