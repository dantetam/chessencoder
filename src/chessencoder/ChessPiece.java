package chessencoder;

import java.util.HashMap;
import java.util.Map;

public class ChessPiece {

	public ChessSide side;
	public ChessPieceType type;
	
	public ChessPiece(ChessSide side, ChessPieceType type) {
		this.side = side;
		this.type = type;
	}
	
	public enum ChessSide {
		WHITE, BLACK
	}
	
	public enum ChessPieceType {
		PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
	}
	
}
