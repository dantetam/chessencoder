package chessencoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import chessencoder.ChessBoard.Move;
import chessencoder.ChessPiece.ChessSide;

public class ChessTest {

	public static void main(String[] args) {
		ChessBoard board = new ChessBoard();
		Map<String, Move> allMoves = board.getAllPossibleAlgebraicMovesForSide(ChessSide.WHITE);
		List<Map.Entry<String, Move>> movesString = board.getAllMovesAlphabeticalOrder(ChessSide.WHITE);
		
		System.out.println(board.toString());
		
		for (int i = 0; i < 100; i++) {
			int randInt = (int)(Math.random() * 30);
			board.applyNthMoveToBoard(randInt);
		}
		System.out.println(board.toString());
		
		System.out.println(board.moveHistory);
	}
	
}
