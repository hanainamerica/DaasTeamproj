
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

class Edge<E> implements Comparable< Edge<E> >
{
    Vertex<E> source, dest;
    double cost;
    
    Edge( Vertex<E> src, Vertex<E> dst, Double cst)
    {
        source = src;
        dest = dst;
        cost = cst;
    }
    
    Edge( Vertex<E> src, Vertex<E> dst, Integer cst)
    {
        this (src, dst, cst.doubleValue());
    }
    
    Edge()
    {
        this(null, null, 0.0);
    }
    
    public String toString() {
        return "Edge: "+ source.getData() + " to " + dest.getData() + ", " +
                "distance: " + cost;
    }
    
    public int compareTo(Edge<E> rhs)
    {
        return (cost < rhs.cost? -1 : cost > rhs.cost? 1 : 0);
    }
}

public class Fleury<E> extends Graph<E>
{
    public int numberOfEdge;                 // Number of edges; 0 if no edge
    public LinkedStack<Edge<E>> cycle;       // Eulerian cycle; null if no such cycle
    public LinkedStack<Edge<E>> deletedEdges; // Deleted edges
    
    public Fleury() {
        super();
        numberOfEdge = 0;
        cycle = new LinkedStack<>();
        deletedEdges = new LinkedStack<>();
    }
    
    public int getNumberOfEdge() {
        return numberOfEdge;
    }
    
    public void addEdge(E source, E dest) {
        super.addEdge(source, dest, 0);
        numberOfEdge++;
    }
    
    @Override
    public boolean remove(E start, E end) {
        if (super.remove(start, end)) {
            deletedEdges.push(new Edge<>(new Vertex<>(start), new Vertex<>(end),0));
            numberOfEdge--;
            return true;
        }
        return false;
    }
    
    @Override
    public void clear() {
        super.clear();
        numberOfEdge = 0;
    }
    
    public void runEulerianCircuit() {
        if (!hasEulerianCircuit()) {
            System.out.println("no euler circuit");
            System.out.println();
        }else {
            printEulerC();
        }
        Edge<E> edge;
        for (int i = 0; i < cycle.size(); i++) {
            edge = deletedEdges.pop();
            addEdge(edge.source.data, edge.dest.data);
        }
    }
    
    // Returns true if the graph has an Eulerian cycle.
    public boolean hasEulerianCircuit() {
        // 1. graph must have at least one edge
        if (numberOfEdge == 0)
            return false;
        // 2. each vertex has even degree
        Iterator<Entry<E, Vertex<E>>> iter;
        iter = vertexSet.entrySet().iterator();
        while (iter.hasNext()) {
            Vertex<E> curr = iter.next().getValue();
            if (curr.adjList.size() % 2 != 0)
                return false;
        }
        // 3. start with one non-isolated vertex with even degree
        Vertex<E> start = nonIsolatedVertex();
        findEulerCircuit(start);
        return numberOfEdge == 0 && start.equals(cycle.peek().dest);
    }
    
    private void findEulerCircuit(Vertex<E> start) {
        if (start.adjList.size() == 0)
            return;
        Vertex<E> v;
        LinkedQueue<Vertex<E>> vertices = new LinkedQueue<>();
        Iterator<Entry<E, Pair<Vertex<E>, Double>>> iter;
        iter = start.iterator();
        while(iter.hasNext()) {
            v = iter.next().getValue().first;
            vertices.enqueue(v);
        }
        
        while (!vertices.isEmpty()) {
            Vertex<E> vertex = vertices.dequeue();
            if (isValidNextEdge(start, vertex)) {
                remove(start.data, vertex.data);
                cycle.push(new Edge<>(start,vertex,0));
                findEulerCircuit(vertex);
            }
        }
    }
    
    private boolean isValidNextEdge(Vertex<E> start, Vertex<E> end) {
        if (start.adjList.size() == 0)
            return false;
        // The edge start-end is valid in one of the
        // following two cases:
    
        // 1) If end is the only adjacent vertex of start
        // ie size of adjacent vertex list is 1
        if (start.adjList.size() == 1)
            return true;
        // 2) If there are multiple adjacents, then
        // start-end is not a bridge Do following steps
        // to check if start-end is a bridge
        // 2.a) count of vertices reachable from start
        unvisitVertices();
        int count1 = dfsCount(start);
        // 2.b) Remove edge (start, end) and after removing
        //  the edge, count vertices reachable from start
        remove(start.data, end.data);
        unvisitVertices();
        int count2 = dfsCount(start);
        // 2.c) Add the edge back to the graph
        Edge<E> v = deletedEdges.pop();
        addEdge(v.source.data, v.dest.data);
        return count1 <= count2;
    }
    
    private int dfsCount(Vertex<E> start) {
        Vertex<E> nextVertex;
        start.visit();
        int count = 1;
        Iterator<Entry<E, Pair<Vertex<E>, Double>>> iter =
                start.iterator();
        while (iter.hasNext())
        {
            nextVertex = iter.next().getValue().first;
            if(!nextVertex.isVisited()) {
                count = count + dfsCount(nextVertex);
            }
        }
        return count;
    }
    
    // returns any non-isolated vertex; null if no such vertex
    private Vertex<E> nonIsolatedVertex() {
        ArrayList<Vertex<E>> list = new ArrayList<>(); // store all non-isolated vertices
        int index;    // to get a random vertex
        
        Iterator<Entry<E, Vertex<E>>> iter;
        iter = vertexSet.entrySet().iterator();
        while (iter.hasNext()) {
            Vertex<E> curr = iter.next().getValue();
            if (curr.adjList.size() != 0)
                list.add(curr);
        }
        
        if (list.size() > 0) {
            index = (int)(Math.random()*list.size());
            return list.get(index);
        }
        return null;
    }
    
    public void printEulerC() {
        LinkedStack<Edge<E>> stack = new LinkedStack<>();
        while (!cycle.isEmpty()) {
            stack.push(cycle.pop());
        }
        while (!stack.isEmpty()) {
            Edge<E> edge = stack.pop();
            cycle.push(edge);
            System.out.print(edge.source.data);
            System.out.print(" -> ");
            System.out.println(edge.dest.data);
        }
    }
    
    public void writeEulerCircuitToFile(PrintWriter writer) {
        writer.println("\n-----------------");
        writer.println("Euler Circuit");
        writer.println("-----------------");
        LinkedStack<Edge<E>> stack = new LinkedStack<>();
        while (!cycle.isEmpty()) { stack.push(cycle.pop()); }
        while (!stack.isEmpty()) {
            Edge<E> edge = stack.pop();
            cycle.push(edge);
            writer.print(edge.source.data);
            writer.print(" -> ");
            writer.print(edge.dest.data);
            writer.println();
        }
    }
    
    @Override
    public void writeToFile(PrintWriter writer) {
        super.writeToFile(writer);
    }
}
