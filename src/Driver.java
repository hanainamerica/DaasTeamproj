
import java.util.*;
import java.io.*;

class FleuryDriver {
    public static Scanner userScanner = new Scanner(System.in);
    public static void main(String[] args) {
        char choice;
        boolean endThisGraph = false;
        Fleury<Location> graph = null;
        try {
            do {
                do {
                    int menuChoice = displayMenu();
                    switch (menuChoice) {
                        case 1:
                            userScanner = openInputFile();
                            if(userScanner != null)
                                graph = readGraphFromFile();
                            else
                                userScanner = new Scanner(System.in);
                            break;
                        case 2:
                            if (graph == null) {
                                System.out.println("There is no graph to add edge.");
                                graph = new Fleury<>();
                                System.out.println("Successfully create a new blank graph!");
                            }
                            addEdge(graph);
                            break;
                        case 3:
                            removeEdge(graph);
                            break;
                        case 4:
                            if (graph == null)
                                System.out.println("No graph to undo.");
                            else
                                undoRemoval(graph);
                            break;
                        case 5:
                            if (graph == null) {
                                System.out.println("No graph to display.");
                            } else {
                                displayGraph(graph);
                            }
                            break;
                        case 6:
                            if (graph == null) {
                                System.out.println("No graph to display.");
                            }else{
                                FleuryCircuit(graph);
                            }
                            break;
                        case 7:
                            if (graph == null) {
                                System.out.println("No graph to save.");
                            }else {
                                writeGraphToFile(graph);
                            }
                            break;
                        case 8:
                            endThisGraph = true;
                            break;
                    }
                } while (!endThisGraph);
                System.out.print("Do you want to solve problem for another graph(Y or N): ");
                choice = userScanner.next().charAt(0);
            } while (choice != 'n' && choice != 'N');
        }catch(Exception e) {
            System.err.println("Error in program!");
        }
    }
    
    private static void FleuryCircuit(Fleury<Location> graph) {
        graph.runEulerianCircuit();
        userScanner.nextLine();
        System.out.println("Do you want to store Euler Circuit to a text file(Y or N): ");
        char choice=userScanner.nextLine().charAt(0);
        if(choice=='Y'||choice=='y')
        {
            try {
                System.out.print("Please enter output file name: ");
                FileOutputStream outFile = new FileOutputStream(userScanner.nextLine());
                PrintWriter prtwrt = new PrintWriter(outFile);
                graph.writeEulerCircuitToFile(prtwrt);
                prtwrt.close();
                System.out.println("The Euler Circuit is stored in your file!");
                System.out.println();
            }
            catch (FileNotFoundException fe) {
                System.err.println("Error! File Not Found!");
            }
        }
    }
    
    // assume that each line in the file contains two vertex locations which are connected
    private static Fleury<Location> readGraphFromFile() {
        Location source;
        Location dest;
        Fleury<Location> myGraph = new Fleury<Location>();
        
        while (userScanner.hasNextLine()) {
            String line = userScanner.nextLine();
            String[] s = line.split(",");
            source = new Location();
            dest = new Location();
            
            int index = 0;
            do {
                source.setName(s[index]);
                index++;
            }while(!s[index].equals(""));
            
            index++;
            do {
                dest.setName(s[index]);
                index++;
            }while(index != s.length);
            
            myGraph.addEdge(source, dest);
        }
        userScanner = new Scanner(System.in);
        return myGraph;
    }
    
    private static void addEdge(Fleury<Location> paramGraph) {
        char choice='n';
        do{
            System.out.println("Add an edge to the graph:");
            Location source;
            Location destination;
            System.out.print("Source: ");
            source = getVertexFromClient();
            System.out.print("Destination: ");
            destination = getVertexFromClient();
            paramGraph.addEdge(source, destination);
            System.out.println("Add edge "+source+"-"+destination+" successfully!");
            System.out.print("Do you want to add another edge(Y or N): ");
            choice=userScanner.next().charAt(0);
        }while(choice=='y'||choice=='Y');
    }
    
    private static void removeEdge(Fleury<Location> paramGraph) {
        char choice='n';
        if(paramGraph == null)
        {
            System.out.println("There is no graph to remove edge!");
            return;
        }
        do {
            System.out.println("Remove an edge from the graph:");
            Location start;
            Location end;
            System.out.print("Starting vertex: ");
            System.out.println();
            start = getVertexFromClient();
            System.out.print("Ending vertex: ");
            System.out.println();
            end = getVertexFromClient();
            if (paramGraph.remove(start, end)) {
                System.out.println("This edge is removed from graph.");
            }else {
                System.out.println("Failed to remove edge.");
            }
            System.out.print("Do you want to remove another edge(Y or N): ");
            choice=userScanner.next().charAt(0);
        }while(choice=='y'||choice=='Y');
    }
    
    private static void undoRemoval(Fleury<Location> paramGraph) {
        char choice;
        Edge<Location> removedEdge;
        if (paramGraph.deletedEdges == null) {
            System.out.println("There is no removed edge to be undo removed.\n");
            return;
        }
        do {
            removedEdge = paramGraph.deletedEdges.pop();
            if (removedEdge!= null) {
                paramGraph.addEdge(removedEdge.source.getData(), removedEdge.dest.getData());
                System.out.println(removedEdge.toString() + " was readded to graph.\n"
                    + "Do you want to make another undo removal(Y or N): ");
                choice = userScanner.next().charAt(0);
                System.out.println();
            }else {
                System.out.println("There is no removal to add back to the graph.");
                System.out.println();
                choice = 'n';
            }
        } while (choice == 'y' || choice == 'Y');
    }
    
    private static void writeGraphToFile(Fleury<Location> paramGraph) {
        System.out.print("Please enter output file name: ");
        userScanner = new Scanner(System.in);
        try {
            FileOutputStream outFile = new FileOutputStream(userScanner.nextLine());
            PrintWriter prtwrt = new PrintWriter(outFile);
            paramGraph.writeToFile(prtwrt);
            prtwrt.close();
            System.out.println("The graph is stored in your file!");
            System.out.println();
        } catch (FileNotFoundException fe) {
            System.err.println("Error! File Not Found!");
        }
    }
    
    private static void displayGraph(Fleury<Location> paramGraph) {
        int choice;
        boolean inValid;
        Location start;
        Visitor<Location> visitor = new VertexVisitor();
        do {
            System.out.println("Traversal method:");
            System.out.print("1. Depth-First Traversal.\n"
                    + "2. Breadth-First Traversal.\n"
                    + "3.  Adjacency list of each vertex.\n"
                    + "Please choose traversal method number: ");
            choice = userScanner.nextInt();
            switch (choice) {
                case 1:
                    inValid = false;
                    if (paramGraph.numberOfEdge == 0) {
                        System.out.println("No graph to display.");
                    }else{
                        System.out.print("What vertex do you want to start traversing graph: ");
                        start = getVertexFromClient();
                        paramGraph.depthFirstTraversal(start, visitor);
                    }
                    break;
                case 2:
                    inValid = false;
                    if (paramGraph.numberOfEdge == 0) {
                        System.out.println("No graph to display.");
                    }else{
                        System.out.print("What vertex do you want to start traversing graph: ");
                        start = getVertexFromClient();
                        paramGraph.breadthFirstTraversal(start, visitor);
                    }
                    break;
                case 3:
                    inValid = false;
                    paramGraph.showAdjTable();
                    break;
                default: {
                    inValid = true;
                    System.out.println("Invalid Input! Enter choice numer from 1 to 3.");
                    break;
                }
            }
        } while (inValid);
    }
    
    private static int displayMenu() {
        int menuChoice;
        boolean inValid;
        do {
            inValid = false;
            System.out.print("Menu:\n"
                    + "1. Read the graph from a text file.\n"
                    + "2. Add an edge to the graph.\n"
                    + "3. Remove an edge from the graph.\n"
                    + "4. Undo the previous removals.\n"
                    + "5. Display the graph on the screen.\n"
                    + "6. Find Euler Circuit for the graph.\n"
                    + "7. Write the graph to a text file.\n"
                    + "8. End this graph's operations.\n"
                    + "Please choose number of the operation you want to do: ");
            menuChoice = userScanner.nextInt();
            switch (menuChoice) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    break;
                default: {
                    inValid = true;
                    System.out.println("Invalid Input! Choice must be from 1 to 8.");
                    break;
                }
            }
        } while (inValid);
        return menuChoice;
    }
    
    private static Scanner openInputFile() {
        userScanner = new Scanner(System.in);
        String filename;
        Scanner scanner = null;
        System.out.print("Enter the input filename: ");
        filename = userScanner.nextLine();
        File file = new File(filename);
        
        try {
            scanner = new Scanner(file);
            System.out.println();
        }// end try
        catch (FileNotFoundException fe) {
            System.out.println("Can't open input file.\n");
            return null; // array of 0 elements
        } // end catch
        return scanner;
    }
    
    // ask the client to add the vertex location
    private static Location getVertexFromClient() {
        userScanner = new Scanner(System.in);
        Location location = new Location();
        String line = userScanner.nextLine();
        String[] s = line.split(",");
        for (String st : s)
            location.setName(st);
        return location;
    }
}

/*
•	 -1: it's inefficient to keep assigning a new Scanner(System.in) to the userScanner variable
(after you use it for a File), you should have declared a separate Scanner variable for files
•	-1: I got a NullPointerException after trying to solve an Euler Circuit a 2nd time with the
same graph!
•	-2: you're missing lots of error checking (for example, if a start vertex doesn't exist when
you try to do a Traversal)
 */