package chessencoder;

import java.util.List;

import chessencoder.ChessPiece.ChessSide;

public class ChessTest {

	public static void main(String[] args) {
		ChessBoard board = new ChessBoard();
		List<String> allMoves = board.getAllPossibleAlgebraicMovesForSide(ChessSide.WHITE);
		System.out.println(allMoves);
	}
	
}
