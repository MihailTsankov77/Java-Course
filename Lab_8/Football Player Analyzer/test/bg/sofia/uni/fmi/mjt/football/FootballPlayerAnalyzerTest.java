package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FootballPlayerAnalyzerTest {

    static FootballPlayerAnalyzer footballPlayerAnalyzer;
    static Map<String, Player> players;

    @BeforeAll
    public static void setupFootballPlayerAnalyzer() {
        List<String> playersData = List.of("Nelson Ferreira;Nelson Ferreira Coelho;5/26/1982;36;175.26;72.1;LM,RM,CAM;Portugal;66;66;220000;3000;Right",
                "Bob Ferreira;Nelson Bob Coelho;5/26/1982;36;175.26;72.1;LM,RM,CAM;Portugal;66;66;220000;30001;Right",
                "L. Fiordilino;Antonio Fiordilino;7/25/1996;22;152.4;68;CM,LM;Italy;66;76;1000000;1000;Right",
                "H. Kane;Herbie Kane;11/23/1998;20;152.4;67.1;CM;England;66;76;1000000;11000;Right",
                "J. Bandowski;Jannik Bandowski;3/30/1994;24;190.5;81.2;LB,LM;Germany;66;70;700000;4000;Left");

        players = playersData.stream().map(Player::of).reduce(new HashMap<>(), (acc, player) -> {
            acc.put(player.name(), player);
            return acc;
        }, (left, right) -> {
            left.putAll(right);
            return left;
        });

        String data = playersData.stream().reduce((left, right) -> left + "\n" + right).get();

        footballPlayerAnalyzer = new FootballPlayerAnalyzer(new StringReader(" \n"+data));
    }

    @Test
    public void testGetAll() {
        assertTrue(players.values().size()==footballPlayerAnalyzer.getAllPlayers().size() && players.values().containsAll(footballPlayerAnalyzer.getAllPlayers()) && footballPlayerAnalyzer.getAllPlayers().containsAll(players.values()));
    }

    @Test
    public void testGetAllNationalities() {
        assertEquals(Set.of("Portugal","Italy", "England", "Germany"), footballPlayerAnalyzer.getAllNationalities());
    }

    @Test
    public void testGetHighestPaidPlayerByNationality() {
        assertEquals(players.get("Bob Ferreira"), footballPlayerAnalyzer.getHighestPaidPlayerByNationality("Portugal"));
    }

    @Test
    public void testGetHighestPaidPlayerByNationalityThrow() {
        assertThrows(IllegalArgumentException.class, ()-> footballPlayerAnalyzer.getHighestPaidPlayerByNationality(null));
    }

    @Test
    public void testGroupByPosition() {
        Map<Position, Set<Player>> a = Map.of(Position.LM, Set.of(players.get("Nelson Ferreira") ,players.get("Bob Ferreira"), players.get("L. Fiordilino"), players.get("J. Bandowski")),
                Position.RM, Set.of(players.get("Nelson Ferreira"), players.get("Bob Ferreira")),
                Position.CAM, Set.of(players.get("Nelson Ferreira"), players.get("Bob Ferreira")),
                Position.CM, Set.of( players.get("L. Fiordilino"), players.get("H. Kane")),
                Position.LB, Set.of((players.get("J. Bandowski")))
        );
        assertEquals(a, footballPlayerAnalyzer.groupByPosition());
    }

    @Test
    public void testGetTopProspectPlayerForPositionInBudget() {
        assertFalse(footballPlayerAnalyzer.getTopProspectPlayerForPositionInBudget(Position.CM, 1000).isPresent());
    }

    @Test
    public void testGetTopProspectPlayerForPositionInBudgetThrow() {
        assertThrows(IllegalArgumentException.class, ()-> footballPlayerAnalyzer.getTopProspectPlayerForPositionInBudget(null, -1));
    }

    @Test
    public void testGetSimilarPlayers() {
        assertEquals(Set.of(players.get("Bob Ferreira"), players.get("L. Fiordilino"), players.get("Nelson Ferreira")),footballPlayerAnalyzer.getSimilarPlayers(players.get("Nelson Ferreira")));
    }

    @Test
    public void testGetSimilarPlayersThrow() {
        assertThrows(IllegalArgumentException.class, ()-> footballPlayerAnalyzer.getSimilarPlayers(null));
    }

    @Test
    public void testGetPlayersByFullNameKeyword() {
        assertEquals(Set.of(players.get("Bob Ferreira"), players.get("Nelson Ferreira")),footballPlayerAnalyzer.getPlayersByFullNameKeyword("Nel"));
    }

    @Test
    public void testGetPlayersByFullNameKeywordThrow() {
        assertThrows(IllegalArgumentException.class, ()-> footballPlayerAnalyzer.getPlayersByFullNameKeyword(null));
    }
}
