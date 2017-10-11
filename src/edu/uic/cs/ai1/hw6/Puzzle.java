package edu.uic.cs.ai1.hw6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.uic.cs.ai1.hw6.State.Direction;

public class Puzzle {
	
	public static void main(String[] args) throws IOException {
		
		File inputFile = new File("initialState");
		BufferedReader inputBuffer = null;
		
		try {		
			/* Read initial state from file */	
			System.out.print("Parsing input file... ");
			inputBuffer = new BufferedReader(new FileReader(inputFile));
			String[] lines = new String[3];
			lines = inputBuffer.lines().collect(Collectors.toList()).toArray(lines);
			inputBuffer.close();
		
			State initialState = new State(lines);
			System.out.println("done.");
				
			try { /* MANHATTAN DISTANCE */
		
				long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long totalBefore = Runtime.getRuntime().totalMemory();
				long startTime = System.currentTimeMillis();
						
				System.out.println("Performing search with Manhattan distance...");
				State solutionManhattan = AStarSearch(initialState, State.manhattan);
				System.out.println("\nSolution found with Manhattan distance in " + solutionManhattan.getgScore() + " steps:");
				System.out.println(solutionManhattan);
				
				long endTime = System.currentTimeMillis();
				long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				
				System.out.println("\nExecution terminated in " + (endTime - startTime) + "ms");
				System.out.println("Memory used: " + (memoryAfter - memoryBefore) / 1024 + "KB");
				if(totalBefore != Runtime.getRuntime().totalMemory())
					System.err.println("The memory measure is wrong!");
			} catch (NoSolutionException e) {
			System.err.println("\nNo solution found");
			}
			
			try { /* HAMMING DISTANCE */
				
				long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long totalBefore = Runtime.getRuntime().totalMemory();
				long startTime = System.currentTimeMillis();
						
				System.out.println("Performing search with Hamming distance...");
				State solutionHamming = AStarSearch(initialState, State.hamming);
				System.out.println("\nSolution found with Hamming distance in " + solutionHamming.getgScore() + " steps:");
				System.out.println(solutionHamming);
				
				long endTime = System.currentTimeMillis();
				long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				
				System.out.println("\nExecution terminated in " + (endTime - startTime) + "ms");
				System.out.println("Memory used: " + (memoryAfter - memoryBefore) / 1024 + "KB");
				if(totalBefore != Runtime.getRuntime().totalMemory())
					System.err.println("The memory measure is wrong!");
			} catch (NoSolutionException e) {
			System.err.println("\nNo solution found");
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("Error: initialState file missing.");
		} catch (InvalidStateRepresentationException e) {
			System.err.println("Error: invalid representation of the initial state.");
			System.err.println("Admitted characters: 1-15, 0 or B for blank, blankspace-separated columns, newline-separated rows.");
		}
		
	}
	
	private static State AStarSearch(State initialState, Function<State, Integer> heuristic) throws NoSolutionException {
		
		/* check if the initial state is a solution */
		if(initialState.isSolution())
			return initialState;
		
		/* create empty explored list and the frontier containing only the initial state */
		TreeSet<State> frontier = new TreeSet<>((a,b) -> a.getfScore() - b.getfScore());
		HashSet<State> explored = new HashSet<>();
		initialState.setgScore(0);
		initialState.sethScore(a -> 0);
		frontier.add(initialState);
		
		/* perform search */
		while(!frontier.isEmpty()) {
			
			/* get the best node in the frontiers, based on f = g + h */
			State currentNode = frontier.pollFirst();
			explored.add(currentNode);
			System.err.println("current node:\n" + currentNode);
			
			/* exploit each possible actions for the current node */
			for(Direction direction : Direction.values()) {
				try {
					State childNode = currentNode.makeMove(direction);
					/* check if already explored */
					if(isIn(explored, childNode))
						continue;
					childNode.setgScore(currentNode.getgScore() + 1);
					childNode.sethScore(heuristic);
					/* if a node with same state and lower f is present in the frontier, continue */
					/* otherwise, add---or replace with---this */
					if(!isIn(frontier, childNode) || frontier.removeIf(node -> node.sameStateOf(childNode) && node.getfScore() > childNode.getfScore()))
						frontier.add(childNode);
				} catch (InvalidMoveException e) {}
			}
		}
		/* the whole tree has been explored and no solution has been found */
		throw new NoSolutionException();
	}
	
	/**
	 * Checks whether a node is in the explored node set, ignoring nodes dissimilarity in fScore.
	 * @param set A <code>? super </code>{@link Set} of states
	 * @param node A {@link State} representation
	 * @return {@code true} if the set contains the node, {@code false} otherwise.
	 */
	
	private static boolean isIn(Set<State> set, State node) {
		for(State visited : set)
			if(visited.sameStateOf(node))
				return true;
		return false;
	}

}
