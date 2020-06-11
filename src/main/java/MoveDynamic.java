import java.util.ArrayList;
import java.util.List;

/**
 * Deprecated since ver. 2.0
 */

public class MoveDynamic {
    private static MoveDynamic instance = null;
    private static List<Move> moves = new ArrayList<>(5);

    private MoveDynamic() {
        Move rock = new Move("rock"),
                paper = new Move("paper"),
                scissors = new Move("scissors"),
                lizard = new Move("lizard"),
                spock = new Move("spock");

        rock.addSuperior(paper);
        rock.addSuperior(spock);
        rock.addInferior(scissors);
        rock.addInferior(lizard);

        paper.addSuperior(scissors);
        paper.addSuperior(lizard);
        paper.addInferior(rock);
        paper.addInferior(spock);

        scissors.addSuperior(rock);
        scissors.addSuperior(spock);
        scissors.addInferior(paper);
        scissors.addInferior(lizard);

        lizard.addSuperior(rock);
        lizard.addSuperior(scissors);
        lizard.addInferior(paper);
        lizard.addInferior(spock);

        spock.addSuperior(paper);
        spock.addSuperior(lizard);
        spock.addInferior(rock);
        spock.addInferior(scissors);

        moves.add(rock); moves.add(scissors); moves.add(paper); moves.add(lizard); moves.add(spock);
    }

    public static MoveDynamic getInstance() {
        if (instance == null) {
            instance = new MoveDynamic();
        }
        return instance;
    }

    public int defineResult (String playerMoveName, String computerMoveName) {
        Move playerMove = null, computerMove = null;
        for (Move move : moves) {
            if (move.getName().equals(playerMoveName)) {
                playerMove = move;
            }
            if (move.getName().equals(computerMoveName)) {
                computerMove = move;
            }
            if (playerMove != null && computerMove != null) {
                break;
            }
        }
        if (playerMove.willBeat(computerMove)) {
            return 1;
        } else if (playerMove.willLoseTo(computerMove)) {
            return  -1;
        } else {
            return 0;
        }
    }

    private class Move {
        private String name;
        private List<Move> superior = new ArrayList<>();
        private List<Move> inferior = new ArrayList<>();

        Move(String name) {
            this.name = name;
        }

        public void addSuperior(Move winner) {
            this.superior.add(winner);
        }

        public void addInferior(Move loser) {
            this.inferior.add(loser);
        }

        public boolean willLoseTo(Move move) {
            return this.superior.contains(move);
        }

        public boolean willBeat(Move move) {
            return this.inferior.contains(move);
        }

        public String getName() {
            return name;
        }
    }
}
