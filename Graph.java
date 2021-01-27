import java.util.ArrayList;
import java.util.Arrays;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class Graph {
	
	private ArrayList<ArrayList<Integer>> adjacencyList;
	
	Graph(ArrayList<ArrayList<Integer>> al) {
		adjacencyList = al;
	}
	
	public int vertexNumber() {
		return adjacencyList.size();
	}
	
	public ArrayList<Integer> neighbors(int v) {
		return adjacencyList.get(v);
	}
	
	
	
	// x_i,v (the i-th vertex in the path is v) is represented by the integer i + n*v + 1 because for some reason sat4j can't deal with 0
	// Therefore, x_k = x_i,v where i = (k - 1) % n and v = (k - 1) // n
	
	public int[] hamiltonianPath(int s, int t) {
		
		int n = vertexNumber();
		ISolver solver = SolverFactory.newDefault();
		
		try {
			
			// Each vertex appears AT LEAST ONCE in the path
			for (int v = 0; v < n; v ++) {
				int[] a = new int[n];
				for (int i = 0; i < n; i ++) {
					a[i] = i + n*v + 1;
				}
				solver.addClause(new VecInt(a));
			}
			
			// Each vertex appears NO MORE THAN ONCE in the path
			for (int v = 0; v < n; v ++) {
				for (int i = 0; i < n; i ++) {
					for (int j = i + 1; j < n; j ++) {
						solver.addClause(new VecInt(new int[] {- (i + n*v + 1), - (j + n*v + 1)}));
					}
				}
			}
			
			// Each index in the path is occupied by AT LEAST ONE vertex
			for (int i = 0; i < n; i ++) {
				int[] a = new int[n];
				for (int v = 0; v < n; v ++) {
					a[v] = i + n*v + 1;
				}
				solver.addClause(new VecInt(a));
			}
			
			// Each index is occupied by NO MORE THAN ONE vertex
			for (int i = 0; i < n; i ++) {
				for (int v = 0; v < n; v ++) {
					for (int u = v + 1; u < n; u ++) {
						solver.addClause(new VecInt(new int[] {- (i + n*v + 1), - (i + n*u + 1)}));
					}
				}
			}
			
			// Consecutive vertices in the path are adjacent in the graph
			for (int i = 0; i < n - 1; i ++) {
				for (int v = 0; v < n; v ++) {
					for (int u = 0; u < n; u ++) {
						if (!neighbors(u).contains(v)) {
							solver.addClause(new VecInt(new int[] {- (i + n*u + 1), - (i + 1 + n*v + 1)}));
						}
					}
				}
			}
			
			// The first vertex is s
			solver.addClause(new VecInt(new int[] {0 + n*s + 1}));
			
			// The last vertex is t
			solver.addClause(new VecInt(new int[] {n - 1 + n*t + 1}));
			
		} catch (ContradictionException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Number of variables: " + solver.nVars());
		System.out.println("Number of constraints: " + solver.nConstraints());
		
		try {
			if (solver.isSatisfiable()) {
				
				System.out.println("Satisfiable problem!");
				
				int[] solution = solver.model();				
				int[] path = new int[n];

				for (int k = 0; k < n*n; k ++) {
					if (solution[k] > 0) {
						path[(solution[k] - 1) % n] = (solution[k] - 1) / n;
					}
				}
				
				System.out.println(Arrays.toString(path));
				return path;
				
			} else {
				System.out.println("Unsatisfiable problem!");
				return new int[] {-1};
			}
			
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");
			return new int[] {-1};
		}
		
	}
	
	
	
	public static Graph completeGraph(int n) {
		ArrayList<ArrayList<Integer>> al = new ArrayList<ArrayList<Integer>>();
		for (int k = 0; k < n; k ++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int i = 0; i < n; i ++) {
				if (i!=k) a.add(i); 
			}
			al.add(a);
		}
		return new Graph(al);
	}
	
	
	public static Graph cycleGraph(int n) {
		ArrayList<ArrayList<Integer>> al = new ArrayList<ArrayList<Integer>>();
		for (int k = 0; k < n; k ++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add((k + 1) % n);
			al.add(a);
		}
		return new Graph(al);
	}
	
	
	public static void main(String[] args) {
		
		/*
		ArrayList<Integer> l0 = new ArrayList<Integer>(Arrays.asList(3));
		ArrayList<Integer> l1 = new ArrayList<Integer>(Arrays.asList(0, 2));
		ArrayList<Integer> l2 = new ArrayList<Integer>(Arrays.asList(0, 3));
		ArrayList<Integer> l3 = new ArrayList<Integer>(Arrays.asList(1, 4));
		ArrayList<Integer> l4 = new ArrayList<Integer>(Arrays.asList(0));
		
		ArrayList<ArrayList<Integer>> al  = new ArrayList<ArrayList<Integer>>(Arrays.asList(l0, l1, l2, l3, l4));
		
		System.out.println(al);
		
		Graph g = new Graph(al);
		
		g.hamiltonianPath(1, 0);
		*/
		
		/*
		Graph cg = completeGraph(100);
		System.out.println(cg.adjacencyList);
		cg.hamiltonianPath(5, 8);
		*/
		
		
		Graph cyg = cycleGraph(100);
		System.out.println(cyg.adjacencyList);
		cyg.hamiltonianPath(7, 6);
		
	}

}
