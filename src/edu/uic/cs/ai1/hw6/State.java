package edu.uic.cs.ai1.hw6;

import java.util.function.Function;

/**
 * Represents a state, either initial or during search. Is itself
 * a node of the search. The state is internally represented as a
 * 4-by-4 array of integers. Provides a {@link State.Direction} variable type.
 * @author Marco Mele
 * @since 1.8
 */

public class State {
	
	private int[][] state = new int[4][4];
	private int gScore = -1;
	private int hScore = -1;
	public enum Direction {
		LEFT,
		RIGHT,
		UP,
		DOWN
	}
	
	/**
	 * Computes the Manhattan (city block) distance of a state
	 * from the solution.
	 * @param A {@link State}
	 * @return an integer distance	
	 * @since 1.8
	 */
	
	public static Function<State, Integer> manhattan = (state -> {
		int distance = 0;
		for(int i = 0; i < 4; i ++)
			for(int j = 0; j < 4; j ++) {
				int[] correct = correct(state.state[i][j] != 0 ? state.state[i][j] - 1 : 15);
				distance += Math.abs(correct[0] - i) + Math.abs(correct[1] - j);
			}
		return new Integer(distance);
	});
	
	private static int[] correct(int n) {
		return new int[] {Math.floorDiv(n, 4), n % 4};
	}
	
	/**
	 * Computes the Hamming distance (number of misplaced tiles) of a state
	 * from the solution.
	 * @param A {@link State}
	 * @return an integer distance	
	 * @since 1.8
	 */
	
	public static Function<State, Integer> hamming = (state -> {
		int distance = 0, i = 1;
		for(int[] row : state.state)
			for(int cell : row) {
				if(cell != i)
					if(i != 16 || cell != 0)
						distance ++;
				i ++;
			}
		return new Integer(distance);
	});
	
	/**
	 * Constructs a new state representation from row arrays of integers.
	 * @param rows an array of 4 arrays of 4 integers.
	 * @throws InvalidStateRepresentationException
	 */
	
	public State(String[] rows) throws InvalidStateRepresentationException {
		if(rows.length < 4)
			throw new InvalidStateRepresentationException();
		int r = 0;
		int[] checkArray = new int[16];
		for(int i = 0; i < 16; checkArray[i ++] = 0);
		for(String row : rows) {
			String[] columns = row.split(" ");
			if(columns.length != 4)
				throw new InvalidStateRepresentationException();
			int c = 0;
			for(String column : columns) {
				int value = State.parseValue(column);
				state[r][c ++] = value;
				try {
					checkArray[value] ++;
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new InvalidStateRepresentationException();
				}
			}
			r ++;
		}
		for(int i = 0; i < 16; i ++)
			if(checkArray[i] != 1)
				throw new InvalidStateRepresentationException();
	}
	
	/**
	 * 
	 * @return the cost of reaching the current state from the starting state
	 */
	
	public int getgScore() {
		return gScore;
	}
	
	/**
	 * Set the cost of reaching the current state from the starting state
	 */

	public void setgScore(int gScore) {
		this.gScore = gScore;
	}
	
	/**
	 * 
	 * @return the heuristic evaluation of the cost of reaching the solution from the current state---
	 * might be an invalid value if the cost is yet to be evaluated, i.e. the method {@link sethScore(Function<T,R>)}
	 * has never been invoked on this instance.
	 */

	public int gethScore() {
		return hScore;
	}
	
	/**
	 * Evaluates the cost to the solution based on the provided heuristic.
	 * @param heuristic a {@code Function<State, Integer>} that evaluates a distance measure
	 * based on the state evaluation. Must implement the {@link Function<T,R>} functional interface and its
	 * {@code R Function<T,R>.apply(T t)} method, must be either a named class, or be a Functional Interface, an Anonymous Inner class
	 * or a lambda function.
	 * @since 1.8
	 */

	public void sethScore(Function<State, Integer> heuristic) {
		this.hScore = heuristic.apply(this);
	}
	
	public int getfScore() {
		return gScore + hScore;
	}

	/**
	 * Check if this state is a solution.
	 * @return {@code true} if it is a solution, {@code false} otherwise.
	 */
	
	public boolean isSolution() {
		return checkSolution(state);
	}
	
	/**
	 * Constructs a new state given the internal array representation. <b>For internal use only.</b>
	 * @param state a 4-by-4 <b>valid</b> array representation.
	 */
	
	private State(int[][] state) {
		this.state = state;
	}
	
	/**
	 * Makes a move from the current state moving a tile in the specified direction.
	 * @param direction A value from type {@link State.Direction}.
	 * @return The new state representation, if the move was valid.
	 * @throws InvalidMoveException
	 */
	
	public State makeMove(Direction direction) throws InvalidMoveException {
		int[][] after = new int[4][4];
		for(int i = 0; i < 4; i ++)
			for(int j = 0; j < 4; j ++)
				after[i][j] = state[i][j];
		int[] blank = State.getBlankIndex(state);
		try {
			switch (direction) {
			case LEFT:
				after[blank[0]][blank[1]] = after[blank[0]][blank[1] + 1];
				after[blank[0]][blank[1] + 1] = 0;
				break;
			case RIGHT:
				after[blank[0]][blank[1]] = after[blank[0]][blank[1] - 1];
				after[blank[0]][blank[1] - 1] = 0;
				break;
			case UP:
				after[blank[0]][blank[1]] = after[blank[0] + 1][blank[1]];
				after[blank[0] + 1][blank[1]] = 0;
				break;
			case DOWN:
				after[blank[0]][blank[1]] = after[blank[0] - 1][blank[1]];
				after[blank[0] - 1][blank[1]] = 0;
				break;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InvalidMoveException();
		}
		return new State(after);
	}
	
	/**
	 * Looks-up the position of the blank tile in the current representation. <b>For internal use only.</b>
	 * @param state A valid 4-by-4 array representation.
	 * @return A 2-integers array with the blank tile coordinates, row and column, starting from 0.
	 */
	
	private static int[] getBlankIndex(int[][] state) {
		for(int i = 0; i < 4; i ++)
			for(int j = 0; j < 4; j ++)
				if(state[i][j] == 0)
					return new int[] {i, j};
		return null;
	}
	
	/**
	 * A modified string parser for integers; accepts integers and lowercase and uppercase B. <b>For internal use only.</b>
	 * @param cell A string
	 * @return The corresponding integer representation -- 0 for B.
	 * @throws InvalidStateRepresentationException
	 */
	
	private static int parseValue(String cell) throws InvalidStateRepresentationException {
		int value = 0;
		try {
			value = Integer.parseInt(cell);
		} catch (NumberFormatException e) {
			if(cell.contains("B") || cell.contains("b"))
				value = 0;
			else throw new InvalidStateRepresentationException();
		}
		return value;
	}
	
	/**
	 * Internal method to check if the state is a solution. <b>For internal use only.</b>
	 * @param state a 4-by-4 state representation
	 * @return {@code true} if this is a solution, {@code false} otherwise.
	 */
	
	private static boolean checkSolution(int[][] state) {
		int i = 1;
		for(int[] row : state)
			for(int cell : row) {
				if(cell != i)
					return cell == 0 && i == 16;
				i ++;
			}
		return true;
	}
	
	/**
	 * Checks if two State objects are the same exact node in both state and score.
	 * @param other A State object
	 * @return {@code true} if the two nodes are the same, {@code false} otherwise.
	 */
	
	public boolean equals(State other) {
		return this.sameStateOf(other) ? this.getfScore() == other.getfScore() : false;
	}
	
	/**
	 * Checks if two State objects represent the same state but not necessarily the same node.
	 * @param other A State object
	 * @return {@code true} if the internal representations of the two states is the same, {@code false} otherwise.
	 */
	
	public boolean sameStateOf(State other) {
		for(int i = 0; i < 4; i ++)
			for(int j = 0; j < 4; j ++)
				if(this.state[i][j] != other.state[i][j])
					return false;
		return true;
	}
	
	@Override
	public String toString() {
		String result = new String("\n");
		for(int[] row : state) {
			for(int cell : row)
				result = result + cell + " ";
			result = result + "\n";
		}
		return result;
	}

}
