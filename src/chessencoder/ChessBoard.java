package chessencoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chessencoder.ChessPiece.ChessPieceType;
import chessencoder.ChessPiece.ChessSide;

public class ChessBoard {

	public static final int numRanks = 8, numFiles = 8; 
	
	public static final int alphabetSize = 26;
	public char[] alphabet = new char[alphabetSize];
	private Set<Character> alphabetSet = new HashSet<>();
	
	public Map<String, Integer> fileToNumberMap;
	public String[] fileLetterNames;
	
	public ChessPiece[][] board; //rank, then file. The first row represents the first rank, so the whole board is seen from the perspective of black playing digitally.
	
	public ChessBoard() {
		board = new ChessPiece[numRanks][numFiles];
		for (int i = 0; i < alphabetSize; i++) {
			char c = (char) ('a' + i);
			alphabet[i] = c;
			alphabetSet.add(c);
		}
		initFileLetterNames();
		defaultBoardPieces();
	}
	
	private void initFileLetterNames() {
		fileLetterNames = new String[numFiles];
		fileToNumberMap = new HashMap<>();
		for (int i = 0; i < numFiles; i++) {
			String generatedName = new String("" + (char)('a' + i));
			fileLetterNames[i] = generatedName;
			fileToNumberMap.put(generatedName, i);
		}
		
		/*
		fileLetterNames = new String[numFiles];
		String file = "a";
		for (int i = 0; i < numFiles; i++) {
			fileLetterNames[i] = file;
			
		}
		*/
		
		
		
	}
	
	private void defaultBoardPieces() {
		for (int file = 0; file < 8; file++) {
			board[1][file] = new ChessPiece(ChessSide.WHITE, ChessPieceType.PAWN);
			board[6][file] = new ChessPiece(ChessSide.BLACK, ChessPieceType.PAWN);
		}
		for (int i = 0; i < 2; i++) {
			ChessSide side = i == 0 ? ChessSide.WHITE : ChessSide.BLACK;
			int rank = i == 0 ? 0 : 7;
			board[rank][0] = new ChessPiece(side, ChessPieceType.PAWN);
			board[rank][1] = new ChessPiece(side, ChessPieceType.KNIGHT);
			board[rank][2] = new ChessPiece(side, ChessPieceType.BISHOP);
			board[rank][3] = new ChessPiece(side, ChessPieceType.KING);
			board[rank][4] = new ChessPiece(side, ChessPieceType.QUEEN);
			board[rank][5] = new ChessPiece(side, ChessPieceType.BISHOP);
			board[rank][6] = new ChessPiece(side, ChessPieceType.KNIGHT);
			board[rank][7] = new ChessPiece(side, ChessPieceType.PAWN);
		}
	}
	
	public List<String> getAllPossibleAlgebraicMovesForSide(ChessSide side) {
		List<String> totalMoves = new ArrayList<>();
		for (int r = 0; r < numRanks; r++) {
			for (int c = 0; c < numFiles; c++) {
				ChessPiece piece = board[r][c];
				if (piece != null && piece.side == side) {
					List<String> moves = possibleMoves(new Pair(r, c));
					totalMoves.addAll(moves);
				}
			}
		}
		return totalMoves;
	}
	
	//e4 -> 5, 3
	private Pair convertAlgebraicToNum(String algebraic) {
		String file = null;
		int rank = 0;
		for (int i = 0; i < algebraic.length(); i++) {
			if (!alphabetSet.contains(algebraic.charAt(i))) {
				file = algebraic.substring(0, i);
				rank = Integer.parseInt(algebraic.substring(i));
				break;
			}
		}
		return new Pair(rank - 1, fileToNumberMap.get(file));
	}
	
	private String convertPairToAlgebraic(Pair location) {
		return new String(fileLetterNames[location.y] + "" + (location.x + 1));
	}
	
	
	
	public List<String> possibleMoves(Pair location) {
		List<String> moves = new ArrayList<>();
		
		List<Pair> potentialDirections = new ArrayList<>();
		
		ChessPiece piece = board[location.x][location.y];
		switch (piece.type) {
		case PAWN:
			int direction = piece.side == ChessSide.WHITE ? 1 : -1;
			addMoveAndCanContinue(moves, piece.side, location, location.sum(direction, -1), true); 
			addMoveAndCanContinue(moves, piece.side, location, location.sum(direction, 1), true); 
			addMoveAndCanContinue(moves, piece.side, location, location.sum(direction, 0), false); 
			break;
		case KNIGHT:
			for (int i = 0; i < 2; i++) {
				boolean capture = i == 0;
				addMoveAndCanContinue(moves, piece.side, location, location.sum(1, 2), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(1, -2), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(-1, 2), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(-1, -2), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(2, 1), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(2, -1), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(-2, 1), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(-2, -1), capture);
			}
			break;
		case BISHOP:
			potentialDirections.add(new Pair(-1, -1));
			potentialDirections.add(new Pair(1, -1));
			potentialDirections.add(new Pair(-1, 1));
			potentialDirections.add(new Pair(1, 1));
			break;
		case ROOK:
			potentialDirections.add(new Pair(0, 1));
			potentialDirections.add(new Pair(0, -1));
			potentialDirections.add(new Pair(-1, 0));
			potentialDirections.add(new Pair(1, 0));
			break;
		case QUEEN:
			potentialDirections.add(new Pair(-1, -1));
			potentialDirections.add(new Pair(1, -1));
			potentialDirections.add(new Pair(-1, 1));
			potentialDirections.add(new Pair(1, 1));
			potentialDirections.add(new Pair(0, 1));
			potentialDirections.add(new Pair(0, -1));
			potentialDirections.add(new Pair(-1, 0));
			potentialDirections.add(new Pair(1, 0));
			break;
		case KING:
			for (int i = 0; i < 2; i++) {
				boolean capture = i == 0;
				addMoveAndCanContinue(moves, piece.side, location, location.sum(0, 1), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(0, -1), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(-1, 0), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(1, 0), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(1, 1), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(1, -1), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(-1, 1), capture);
				addMoveAndCanContinue(moves, piece.side, location, location.sum(-1, -1), capture);
			}
			break;
		}
		
		switch (piece.type) {
		case BISHOP: case ROOK: case QUEEN:
			for (Pair direction: potentialDirections) {
				Pair target = location.sum(0,0);
				while (true) {
					target = target.sum(direction);
					if (addMoveAndCanContinue(moves, piece.side, location, target, false)) {
						continue;
					}
					else {
						addMoveAndCanContinue(moves, piece.side, location, target, true); //A capturing move always stops a piece from going beyond
						break;
					}
				}
			}
			break;
		}
		
		return moves;
	}
	
	public boolean containsEnemyPiece(ChessSide side, Pair target) {
		ChessPiece piece = board[target.x][target.y];
		return piece != null && piece.side != side;
	}
	
	public boolean containsPiece(Pair target) {
		ChessPiece piece = board[target.x][target.y];
		return piece != null;
	}
	
	//Return true if we can continue moving in this direction
	private boolean addMoveAndCanContinue(List<String> moves, ChessSide side, Pair start, Pair target, boolean capture) {
		if (target.x < 0 || target.y < 0 || target.x >= numRanks || target.y >= numFiles) {
			return false;
		}
		if (capture) {
			if (containsEnemyPiece(side, target)) {
				addMoveInAlgebraicNotation(moves, start, target, capture);
			}
			return false;
		}
		else {
			if (containsPiece(target)) {
				return false;
			}
			addMoveInAlgebraicNotation(moves, start, target, capture);
			return true;
		}
	}
	
	//Adds a move to the list, assuming it is valid
	private void addMoveInAlgebraicNotation(List<String> moves, Pair start, Pair target, boolean capture) {
		String move = null;
		ChessPiece piece = board[start.x][start.y];
		if (piece == null) throw new IllegalArgumentException("Trying to find piece abbreviation of empty square");
		if (piece.type == ChessPieceType.PAWN) {
			if (capture) {
				String file = fileLetterNames[start.x];
				move = new String(file + "x" + convertPairToAlgebraic(target));
			}
			else {
				move = convertPairToAlgebraic(target);
			}
		}
		else {
			if (capture) {
				move = letterReps.get(piece.type) + "x" + convertPairToAlgebraic(target);
			}
			else {
				move = letterReps.get(piece.type) + convertPairToAlgebraic(target);
			}
		}
		if (move == null) throw new IllegalArgumentException("Could not find algebraic move from input");
		moves.add(move);
	}
	private static final Map<ChessPieceType, Character> letterReps = new HashMap<ChessPieceType, Character>() {{
		put(ChessPieceType.KNIGHT, 'N');
		put(ChessPieceType.BISHOP, 'B');
		put(ChessPieceType.ROOK, 'R');
		put(ChessPieceType.QUEEN, 'Q');
		put(ChessPieceType.KING, 'K');
	}};
	
	public static class Pair {
		public Integer x,y;
		public Pair(Integer a, Integer b) {
			x = a; y = b;
		}
		public Pair sum(int offsetR, int offsetC) {
			return new Pair(x + offsetR, y + offsetC);
		}
		public Pair sum(Pair other) {
			return new Pair(x + other.x, y + other.y);
		}
	}
	
	public static class Location {
		public String file;
		public int rank;
		public Location(String f, int r) {
			file = f;
			rank = r;
		}
	}
	
}
