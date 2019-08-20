package amazons;

import org.junit.Test;

import static org.junit.Assert.*;

import ucb.junit.textui;

import java.util.*;

import static amazons.Piece.*;

/**
 * The suite of all JUnit tests for the enigma package.
 *
 * @author
 */
public class UnitTest {

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * A dummy test as a placeholder for real ones.
     */
    @Test
    public void dummyTest() {
        assertTrue("There are no unit tests!", true);
    }

    /**
     * Tests basic correctness of put and get on the initialized board.
     */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /**
     * Tests proper identification of legal/illegal queen moves.
     */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /**
     * Tests toString for initial board state and a smiling board state. :)
     */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));

    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   B - - - - - - - - B\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   W - - - - - - - - W\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n" +
                    "   - S S S - - S S S -\n" +
                    "   - S - S - - S - S -\n" +
                    "   - S S S - - S S S -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - W - - - - W - -\n" +
                    "   - - - W W W W - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n";


    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, reachableFromTestBoard);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(reachableFromTestSquares.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(reachableFromTestSquares.size(), numSquares);
        assertEquals(reachableFromTestSquares.size(), squares.size());
    }

    /**
     * Tests legalMovesIterator to make sure it returns all legal Moves.
     * This method needs to be finished and may need to be changed
     * based on your implementation.
     */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        buildBoard(b, legalMovesTestBoard); // FIXME
        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            assertTrue(legalMovesTestMoves.contains(m)); // FIXME
            numMoves += 1;
            moves.add(m);
        }
        assertEquals(legalMovesTestMoves.size(), numMoves); // FIXME
        assertEquals(legalMovesTestMoves.size(), moves.size()); // FIXME
    }

    @Test
    public void testLegalMoves2() {
        Board b = new Board();
        Iterator<Move> legalMoves = b.legalMoves();
        int count = 0;
        while (legalMoves.hasNext()) {
            count++;
        }
        assertTrue(count == 2176);
    }


    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] reachableFromTestBoard =
            {
                    {E, E, E, E, E, E, E, E, E, E},
                    {E, E, E, E, E, E, E, E, W, W},
                    {E, E, E, E, E, E, E, S, E, S},
                    {E, E, E, S, S, S, S, E, E, S},
                    {E, E, E, S, E, E, E, E, B, E},
                    {E, E, E, S, E, W, E, E, B, E},
                    {E, E, E, S, S, S, B, W, B, E},
                    {E, E, E, E, E, E, E, E, E, E},
                    {E, E, E, E, E, E, E, E, E, E},
                    {E, E, E, E, E, E, E, E, E, E},
            };

    static final Piece[][] legalMovesTestBoard =
            {
                    {E, E, E, E, E, E, E, E, E, E},
                    {E, E, E, E, E, E, E, E, E, E},
                    {E, E, E, E, E, E, E, E, E, E},
                    {E, E, E, E, E, E, E, E, E, E},
                    {E, E, E, E, E, E, E, E, E, E},
                    {E, E, E, E, E, E, E, E, E, E},
                    {S, S, S, E, E, E, E, E, E, E},
                    {E, W, S, E, E, E, E, E, E, E},
                    {E, E, S, E, E, E, E, E, E, E},
                    {W, E, S, E, E, E, E, E, E, E},
            };

    static final Set<Square> reachableFromTestSquares =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));


    static final Set<Move> legalMovesTestMoves = new HashSet<>(Arrays.asList(
            //moves
            Move.mv(Square.sq(0, 0), Square.sq(0, 1), Square.sq(0, 2)),
            Move.mv(Square.sq(0, 0), Square.sq(0, 1), Square.sq(1, 1)),
            Move.mv(Square.sq(0, 0), Square.sq(0, 1), Square.sq(1, 0)),
            Move.mv(Square.sq(0, 0), Square.sq(0, 1), Square.sq(0, 0)),
            Move.mv(Square.sq(0, 0), Square.sq(0, 2), Square.sq(0, 1)),
            Move.mv(Square.sq(0, 0), Square.sq(0, 2), Square.sq(1, 1)),
            Move.mv(Square.sq(0, 0), Square.sq(0, 2), Square.sq(0, 0)),
            Move.mv(Square.sq(0, 0), Square.sq(1, 1), Square.sq(0, 1)),
            Move.mv(Square.sq(0, 0), Square.sq(1, 1), Square.sq(0, 2)),
            Move.mv(Square.sq(0, 0), Square.sq(1, 1), Square.sq(1, 0)),
            Move.mv(Square.sq(0, 0), Square.sq(1, 1), Square.sq(0, 0)),
            Move.mv(Square.sq(0, 0), Square.sq(1, 0), Square.sq(0, 1)),
            Move.mv(Square.sq(0, 0), Square.sq(1, 0), Square.sq(1, 1)),
            Move.mv(Square.sq(0, 0), Square.sq(1, 0), Square.sq(0, 0)),

            Move.mv(Square.sq(1, 2), Square.sq(0, 1), Square.sq(0, 2)),
            Move.mv(Square.sq(1, 2), Square.sq(0, 1), Square.sq(1, 1)),
            Move.mv(Square.sq(1, 2), Square.sq(0, 1), Square.sq(1, 0)),
            Move.mv(Square.sq(1, 2), Square.sq(0, 1), Square.sq(1, 2)),
            Move.mv(Square.sq(1, 2), Square.sq(0, 2), Square.sq(0, 1)),
            Move.mv(Square.sq(1, 2), Square.sq(0, 2), Square.sq(1, 1)),
            Move.mv(Square.sq(1, 2), Square.sq(0, 2), Square.sq(1, 2)),
            Move.mv(Square.sq(1, 2), Square.sq(1, 1), Square.sq(0, 1)),
            Move.mv(Square.sq(1, 2), Square.sq(1, 1), Square.sq(0, 2)),
            Move.mv(Square.sq(1, 2), Square.sq(1, 1), Square.sq(1, 0)),
            Move.mv(Square.sq(1, 2), Square.sq(1, 1), Square.sq(1, 2)),
            Move.mv(Square.sq(1, 2), Square.sq(1, 0), Square.sq(0, 1)),
            Move.mv(Square.sq(1, 2), Square.sq(1, 0), Square.sq(1, 1)),
            Move.mv(Square.sq(1, 2), Square.sq(1, 0), Square.sq(1, 2))
    ));

    /** @Test public void queenMoveTest() {
    Board b = new Board;
    makeSmile(b);
    Square.sq(4,2);
    }
     */

}


