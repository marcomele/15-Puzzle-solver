package edu.uic.cs.ai1.hw6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.stream.Collectors;

import edu.uic.cs.ai1.hw6.State.Direction;

public class Puzzle {
	
	public static void main(String[] args) throws IOException {
		
		long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long totalBefore = Runtime.getRuntime().totalMemory();
		long startTime = System.currentTimeMillis();
		
		File inputFile = new File("initialState");
		BufferedReader inputBuffer = null;
				
		try {
		
			/* Read initial state from file */	
			System.out.print("Parsing input file... ");
			inputBuffer = new BufferedReader(new FileReader(inputFile));
			String[] lines = new String[3];
			lines = inputBuffer.lines().collect(Collectors.toList()).toArray(lines);
			
			State initialState = new State(lines);
			System.out.println("done.");
			System.out.println("Performing search...");
			
			/* Apply BFS to search for a solution */
			State solution = breadthFirstSearch(initialState);
			
			System.out.println("\nSolution found:");
			System.out.println(solution);
			
		} catch (FileNotFoundException e) {
			System.err.println("Error: initialState file missing.");
		} catch (InvalidStateRepresentationException e) {
			System.err.println("Error: invalid representation of the initial state.");
			System.err.println("Admitted characters: 1-15, 0 or B for blank, blankspace-separated columns, newline-separated rows.");
		} catch (NoSolutionException e) {
			System.out.println("Execution terminated: no solution found for the given initial state.");
		} finally {
			inputBuffer.close();
		}
		
		long endTime = System.currentTimeMillis();
		long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		System.out.println("\nExecution terminated in " + (endTime - startTime) + "ms");
		System.out.println("Memory used: " + (memoryAfter - memoryBefore) / 1024 + "KB");
		if(totalBefore != Runtime.getRuntime().totalMemory())
			System.err.println("The memory measure is wrong!");
		
	}
	
	private static State breadthFirstSearch(State initialState) throws NoSolutionException {
		
		/* check if the initial state is a solution */
		if(initialState.isSolution())
			return initialState;
		
		/* create empty explored list and the frontier containing only the initial state */
		LinkedList<State> frontier = new LinkedList<>();
		LinkedList<State> explored = new LinkedList<>();
		frontier.add(initialState);
		
		/* perform search */
		while(!frontier.isEmpty()) {
			
			/* get the less-deep node in the frontiers, following insertion order */
			State currentNode = frontier.remove();
			explored.add(currentNode);
			
			/* exploit each possible actions for the current node */
			for(Direction direction : Direction.values()) {
				try {
					State childNode = currentNode.makeMove(direction);
					/* check if the child has to be explored and check solution */
					if(!isIn(explored, childNode) && !isIn(frontier, childNode)) {
						if(childNode.isSolution())
							return childNode;
						/* add the child to the frontier */
						frontier.add(childNode);
					}
				} catch (InvalidMoveException e) {}
			}
		}
		/* the whole tree has been explored and no solution has been found */
		throw new NoSolutionException();
	}
	
	/**
	 * Checks whether a node is in a list, either the frontier or the explored list.
	 * @param list A {@link LinkedList} of states
	 * @param node A {@link State} representation
	 * @return {@code true} if the list contains the node, {@code false} otherwise.
	 */
	
	private static boolean isIn(LinkedList<State> list, State node) {
		for(State visited : list)
			if(visited.sameStateOf(node))
				return true;
		return false;
	}

}
