package chessencoder;

import java.util.HashMap;
import java.util.Map;

import chessencoder.ChessPiece.ChessPieceType;

public class ChessPiece {

	public ChessSide side;
	public ChessPieceType type;
	
	public ChessPiece(ChessSide side, ChessPieceType type) {
		this.side = side;
		this.type = type;
	}
	
	public enum ChessSide {
		WHITE, BLACK;
		public static ChessSide getOpposite(ChessSide side) {
			return side == WHITE ? BLACK : WHITE;
		}
	}
	
	public enum ChessPieceType {
		PAWN, PAWN_UNMOVED, KNIGHT, BISHOP, ROOK, QUEEN, KING
	}
	
	public static final Map<ChessPieceType, Character> CHESS_PIECE_LETTER_REPS = new HashMap<ChessPieceType, Character>() {{
		put(ChessPieceType.KNIGHT, 'N');
		put(ChessPieceType.BISHOP, 'B');
		put(ChessPieceType.ROOK, 'R');
		put(ChessPieceType.QUEEN, 'Q');
		put(ChessPieceType.KING, 'K');
		put(ChessPieceType.PAWN_UNMOVED, '*');
		put(ChessPieceType.PAWN, '*');
	}};
	
	public String toString() {
		if (type == null) return "_";
		if (CHESS_PIECE_LETTER_REPS.containsKey(type)) {
			return CHESS_PIECE_LETTER_REPS.get(type).toString();
		} else {
			throw new RuntimeException("Invalid piece type: " + type);
		}
	}
	
	public ChessPiece clone() {
		return new ChessPiece(this.side, this.type);
	}
	
}
