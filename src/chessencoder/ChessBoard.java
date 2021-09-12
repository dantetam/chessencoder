package chessencoder;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import chessencoder.ChessPiece.ChessPieceType;
import chessencoder.ChessPiece.ChessSide;

public class ChessBoard {

	public static final int numRanks = 8, numFiles = 8; 

	public static final int alphabetSize = 26;
	public static char[] alphabet = new char[alphabetSize];
	private static Set<Character> alphabetSet = new HashSet<>();
	
	static {
		for (int i = 0; i < alphabetSize; i++) {
			char c = (char) ('a' + i);
			alphabet[i] = c;
			alphabetSet.add(c);
		}
	}

	public Map<String, Integer> fileToNumberMap;
	public String[] fileLetterNames;
	
	public ChessSide currentSideToMove = ChessSide.WHITE;

	public ChessPiece[][] board; //rank, then file. The first row represents the first rank, so the whole board is seen from the perspective of black playing digitally.

	public String moveHistory = "";
	public int turnNumber = 1;
	
	public ChessBoard() {
		board = new ChessPiece[numRanks][numFiles];
		initFileLetterNames();
		defaultBoardPieces();
	}
	
	public ChessBoard(ChessPiece[][] origBoard) {
		board = new ChessPiece[numRanks][numFiles];
		initFileLetterNames();
		this.board = new ChessPiece[origBoard.length][origBoard[0].length];
		for (int r = 0; r < origBoard.length; r++) {
			for (int c = 0; c < origBoard[0].length; c++) {
				if (origBoard[r][c] != null) {
					this.board[r][c] = origBoard[r][c].clone();
				}
			}
		}
	}

	private void initFileLetterNames() {
		fileLetterNames = new String[numFiles];
		fileToNumberMap = new HashMap<>();
		for (int i = 0; i < numFiles; i++) {
			String generatedName = new String("" + (char)('a' + i));
			fileLetterNames[i] = generatedName;
			fileToNumberMap.put(generatedName, i);
		}
	}

	private void defaultBoardPieces() {
		for (int file = 0; file < 8; file++) {
			board[1][file] = new ChessPiece(ChessSide.WHITE, ChessPieceType.PAWN_UNMOVED);
			board[6][file] = new ChessPiece(ChessSide.BLACK, ChessPieceType.PAWN_UNMOVED);
		}
		for (int i = 0; i < 2; i++) {
			ChessSide side = i == 0 ? ChessSide.WHITE : ChessSide.BLACK;
			int rank = i == 0 ? 0 : 7;
			board[rank][0] = new ChessPiece(side, ChessPieceType.ROOK);
			board[rank][1] = new ChessPiece(side, ChessPieceType.KNIGHT);
			board[rank][2] = new ChessPiece(side, ChessPieceType.BISHOP);
			board[rank][3] = new ChessPiece(side, ChessPieceType.QUEEN);
			board[rank][4] = new ChessPiece(side, ChessPieceType.KING);
			board[rank][5] = new ChessPiece(side, ChessPieceType.BISHOP);
			board[rank][6] = new ChessPiece(side, ChessPieceType.KNIGHT);
			board[rank][7] = new ChessPiece(side, ChessPieceType.ROOK);
		}
	}

	public Map<String, Move> getAllPossibleAlgebraicMovesForSide(ChessSide side) {
		Map<String, Move> totalMoves = new HashMap<>();
		for (int r = 0; r < numRanks; r++) {
			for (int c = 0; c < numFiles; c++) {
				ChessPiece piece = board[r][c];
				if (piece != null && piece.side == side) {
					Map<String, Move> moves = possibleMoves(new Pair(r, c));
					for (Entry<String, Move> entry: moves.entrySet()) {
						totalMoves.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return totalMoves;
	}
	
	public List<Map.Entry<String, Move>> getAllMovesAlphabeticalOrder(ChessSide side) {
		List<Map.Entry<String, Move>> sortedMoves = new ArrayList<>();
		
		Map<String, Move> allMoves = this.getAllPossibleAlgebraicMovesForSide(side);
		List<String> movesList = new ArrayList<>(allMoves.keySet());
		Collections.sort(movesList);
		
		for (String move: movesList) {
			sortedMoves.add(new AbstractMap.SimpleEntry<>(move, allMoves.get(move)));
		}
		return sortedMoves;
	}
	
	public void applyNthMoveToBoard(int nextInput) {
		String chosenMoveAlgebraicStr = null;
		List<Move> chosenMoves = null;
		
		List<Map.Entry<String, Move>> orderedMoves = getAllMovesAlphabeticalOrder(currentSideToMove);
		
		List<Map.Entry<String, List<Move>>> orderedMovesListTree = new ArrayList<>();
		for (Map.Entry<String, Move> entry : orderedMoves) {
			List<Move> newList = new ArrayList<>();
			newList.add(entry.getValue());
			orderedMovesListTree.add(new AbstractMap.SimpleEntry<>(entry.getKey(), newList));
		}
		
		int indexToExpand = 0;
		while (true) {
			System.err.println(orderedMovesListTree.size() + " " + indexToExpand + " " + nextInput);
			if (nextInput <= orderedMovesListTree.size()) {
				chosenMoveAlgebraicStr = orderedMovesListTree.get(nextInput).getKey();
				chosenMoves = orderedMovesListTree.get(nextInput).getValue();
				break;
			} else {
				String curMovesString = orderedMovesListTree.get(indexToExpand).getKey();
				List<Move> currentMovesBeforeExpand = orderedMovesListTree.get(indexToExpand).getValue();
				ChessBoard cloneBoard = this.clone();
				for (Move move: currentMovesBeforeExpand) {
					cloneBoard.applyMoveToBoard(move);
				}
				orderedMovesListTree.remove(indexToExpand);
				
				List<Map.Entry<String, Move>> expandedMoves = cloneBoard.getAllMovesAlphabeticalOrder(cloneBoard.currentSideToMove);
				for (Map.Entry<String, Move> expandedMove : expandedMoves) {
					List<Move> listCloneAppendedWithMove = new ArrayList<>(currentMovesBeforeExpand);
					listCloneAppendedWithMove.add(expandedMove.getValue());
					
					String appendedMoveString = curMovesString + " " + expandedMove.getKey();
					
					Map.Entry<String, List<Move>> newExpandedEntry = new AbstractMap.SimpleEntry<>(appendedMoveString, listCloneAppendedWithMove);
					
					orderedMovesListTree.add(indexToExpand, newExpandedEntry);
					indexToExpand++;
				}
				if (indexToExpand >= orderedMovesListTree.size()) {
					indexToExpand = 0;
				}
			}
		}
		
		System.err.println("For input " + nextInput + ", the chosen moves: " + chosenMoveAlgebraicStr);
		for (Move move: chosenMoves) {
			this.advanceGameHistory(chosenMoveAlgebraicStr);
			this.applyMoveToBoard(move);
		}
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



	public Map<String, Move> possibleMoves(Pair location) {
		Map<String, Move> moves = new HashMap<String, Move>();

		List<Pair> potentialDirections = new ArrayList<>();

		ChessPiece piece = board[location.x][location.y];
		switch (piece.type) {
		case PAWN: case PAWN_UNMOVED:
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
		case PAWN_UNMOVED:
			int direction = piece.side == ChessSide.WHITE ? 2 : -2;
			addMoveAndCanContinue(moves, piece.side, location, location.sum(direction, 0), false); 
			break;
		default:
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
					} else {
						addMoveAndCanContinue(moves, piece.side, location, target, true); //A capturing move always stops a piece from going beyond
						break;
					}
				}
			}
			break;
		default:
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
	private boolean addMoveAndCanContinue(Map<String, Move> moves, ChessSide side, Pair start, Pair target, boolean capture) {
		if (target.x < 0 || target.y < 0 || target.x >= numRanks || target.y >= numFiles) {
			return false;
		}
		if (capture) {
			if (containsEnemyPiece(side, target)) {
				addMoveInAlgebraicNotation(moves, start, target, capture);
			}
			return false;
		} else {
			if (containsPiece(target)) {
				return false;
			}
			addMoveInAlgebraicNotation(moves, start, target, capture);
			return true;
		}
	}

	//Adds a move to the list, assuming it is valid
	private void addMoveInAlgebraicNotation(Map<String, Move> moves, Pair start, Pair target, boolean capture) {
		String move = null;
		ChessPiece piece = board[start.x][start.y];
		if (piece == null) throw new IllegalArgumentException("Trying to find piece abbreviation of empty square");
		if (piece.type == ChessPieceType.PAWN || piece.type == ChessPieceType.PAWN_UNMOVED) {
			if (capture) {
				String file = fileLetterNames[start.x];
				move = new String(file + "x" + convertPairToAlgebraic(target));
			} else {
				move = convertPairToAlgebraic(target);
			}
		} else {
			if (capture) {
				move = ChessPiece.CHESS_PIECE_LETTER_REPS.get(piece.type) + "x" + convertPairToAlgebraic(target);
			}
			else {
				move = ChessPiece.CHESS_PIECE_LETTER_REPS.get(piece.type) + convertPairToAlgebraic(target);
			}
		}
		if (move == null) throw new IllegalArgumentException("Could not find algebraic move from input");
		moves.put(move, new Move(start, target, capture));
	}
	
	public void applyMoveToBoard(Move move) { //String algebraicNotation, 
		ChessPiece mover = getPiece(move.start);
		ChessPiece target = getPiece(move.target);
		if (mover == null) throw new IllegalArgumentException();
		if (move.capture) {
			if (target == null) throw new IllegalArgumentException();
		}
		board[move.start.x][move.start.y] = null;
		board[move.target.x][move.target.y] = mover;
		this.currentSideToMove = this.currentSideToMove == ChessSide.WHITE ? ChessSide.BLACK : ChessSide.WHITE;
	}
	
	public void advanceGameHistory(String algebraicNotation) {
		if (this.currentSideToMove == ChessSide.WHITE) {
			moveHistory += " " + turnNumber + ".";
			turnNumber++;
		} else {
			//moveHistory += "\n";
		}
		moveHistory += " " + algebraicNotation;
	}
	
	public ChessPiece getPiece(Pair pair) {
		return board[pair.x][pair.y];
	}
	
	public String toString() {
		String result = "";
		for (int i = 0; i < 15; i++) result += "-";
		result += "\n";
		for (int i = 7; i >= 0; i--) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < board[0].length; j++) {
				String pieceRep = board[i][j] != null ? board[i][j].toString() : "_";
				if (board[i][j] != null && board[i][j].side == ChessSide.BLACK) 
					pieceRep = pieceRep + " ";
				else
					pieceRep = pieceRep + " ";
				sb.append(pieceRep);
			}
			result += sb.toString() + "\n\n";
		}
		for (int i = 0; i < 15; i++) result += "-";
		result += "\n";
		return result;
	}

	public ChessBoard clone() {
		ChessBoard result = new ChessBoard(this.board);
		return result;
	}
	
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

	public static class Move {
		public Pair start, target;
		public boolean capture;
		public Move(Pair start, Pair target, boolean capture) {
			this.start = start;
			this.target = target;
			this.capture = capture;
		}
	}

}
