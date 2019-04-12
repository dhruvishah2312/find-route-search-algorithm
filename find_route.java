/* used the pseudocode in the textbook and various youtube videos for reference
 * alongwith some github programs for better understanding
 */


import java.io.*;
import java.util.*;

public class find_route {
    public static void main(String[] args) {
        try {
        	
        	/*
        	 This method is used to create a paired list from the input and heuristic file and
        	 then call the other classes and functions defined in this program to find the route with the smallest distance.
        	 The difference of the method used is dependednt on the number of arguments given.
        	 If the arguments are 4 then it performs uninformed UCS search 
        	 Else it performs informed A* search
        	 */
            SDWmapping source, destination, source_check = null, destination_check = null, primary_node, sec_node;
            Set < SDWmapping > sdwMapItemValue = new LinkedHashSet < SDWmapping > ();
            File inputFile = new File(args[1]);
            Scanner sc = new Scanner(inputFile);
            Trace tc = new Trace();
            while (sc.hasNext() && !sc.equals("END OF INPUT")) {
                String s = sc.next();

                if (s.equalsIgnoreCase("End")) {
                    break;
                }

                if (tc.trace(s, sdwMapItemValue) == null) {
                    source_check = new SDWmapping(s);
                    sdwMapItemValue.add(source_check);
                    source_check.successors = new ArrayList < DWpairs > ();
                }

                String s1 = sc.next();

                if (tc.trace(s1, sdwMapItemValue) == null) {
                    destination_check = new SDWmapping(s1);
                    destination_check.successors = new ArrayList < DWpairs > ();
                    sdwMapItemValue.add(destination_check);
                }

                primary_node = tc.trace(s, sdwMapItemValue);
                sec_node = tc.trace(s1, sdwMapItemValue);
                int c = sc.nextInt();
                addSetToPath(s1, sdwMapItemValue, new DWpairs(primary_node, c));
                addSetToPath(s, sdwMapItemValue, new DWpairs(sec_node, c));
            }

            source = tc.trace(args[2], sdwMapItemValue);
            destination = tc.trace(args[3], sdwMapItemValue);
            source.distance = 0;
 
            
            if(args.length == 4 && args[0].equals("uninf") ){  // for uninformed search
            	 new QueueManipulation(source, destination);
            	 new DisplayTrace(destination);
            }else { 
            	// for informed search
            	//Hashmap to store the heuristic file inputs.
            	HashMap<String,Integer> map = new HashMap<String,Integer>();
            	System.out.println(args[4]);
            	Scanner sc1 = new Scanner(getHeuristicFile(args[4]));
            	while (sc1.hasNext() && !sc1.equals("END OF INPUT")) {
      	        String[] columns = sc1.nextLine().split("\\s+");
         	    if (columns[0].equalsIgnoreCase("End")) {
                     break;
                 }
         	   
            	    map.put(columns[0],Integer.parseInt(columns[1]));
            	    
            	}
            	sc1.close();
            	new QueueManipulation(source, destination, map);
           	 	new DisplayTrace(destination);
            }
                
            sc.close();
            
            
           
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception : Argument no " + e.getMessage() + " missing.");
        } catch (FileNotFoundException e) {
            System.out.println("Exception :" + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Exception : Please check the cities.");
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public static void addSetToPath(String s, Set < SDWmapping > sdwMapItemValue, DWpairs ed) {
        for (SDWmapping n1: sdwMapItemValue) {
            if (n1.val.equalsIgnoreCase(s)) {
                n1.successors.add(ed);
            }
        }
    }
    
    public static File getHeuristicFile(String filename) {
    	File heuristicFile = new File(filename);
    	return heuristicFile;
    }
}

//Source-Destination-Weight pairs
class SDWmapping {
 public String val;
 public int distance;
 public SDWmapping source;
 public ArrayList < DWpairs > successors;

 public SDWmapping(SDWmapping sdwSuccessor) {
     val = sdwSuccessor.val;
     distance = sdwSuccessor.distance;
     for (DWpairs ed: sdwSuccessor.successors) {
         this.successors.add(ed);
     }
     source = sdwSuccessor.source;
 }

 public SDWmapping(String val) {
     super();
     this.val = val;
 }

 public SDWmapping(int distance, SDWmapping parent) {
     super();
     this.distance = distance;
     this.source = parent;
 }
}


//destination weight pairs
class DWpairs {
public int distance;
SDWmapping destination;
	public DWpairs(SDWmapping destination, int distance) {
	  super();
	  this.distance = distance;
	  this.destination = destination;
	}
}

class MinimumDistaceComparer implements Comparator<SDWmapping> {
 public int compare(SDWmapping sdwMapItem1, SDWmapping sdwMapItem2) {
     int comp1 = sdwMapItem1.distance;
     int comp2 = sdwMapItem2.distance;
     if (comp1 > comp2) {
         return 1;
     } else if (comp1 < comp2) {
         return -1;
     } else {
         return 0;
     }
 }
}

//Priority queue formation and updation
class QueueManipulation {
	
	// For uninformed UCS search
 public QueueManipulation(SDWmapping source, SDWmapping destination) {
     HashSet < SDWmapping > hashset = new HashSet < SDWmapping > ();
     boolean failure = true;
     PriorityQueue < SDWmapping > frontier = new PriorityQueue < SDWmapping > (100, new MinimumDistaceComparer());

     frontier.add(source);
     boolean flag = true;
     while (flag || frontier.isEmpty() == false && failure == true) {
         flag = false;
         SDWmapping current_city = frontier.poll();
         if (current_city.val.equalsIgnoreCase(destination.val)) {
             failure = false;
         }
         hashset.add(current_city);
         for (DWpairs e: current_city.successors) {
             SDWmapping successor = e.destination;
             int cost = e.distance;
             if (frontier.contains(successor) == false && hashset.contains(successor) == false) {
                 successor.distance = current_city.distance + cost;
                 successor.source = current_city;
                 frontier.add(successor);
             } else if (frontier.contains(successor) == true && (successor.distance > (current_city.distance + cost))) {
                 successor.source = current_city;
                 successor.distance = current_city.distance + cost;
                 frontier.remove(successor); //removes old cost successor
                 frontier.add(successor); // adds updated successor
             }
         }
     }
 }
 
 
 // For informed A* search
public QueueManipulation(SDWmapping source, SDWmapping destination, HashMap<String,Integer> cityHeuristicPair ) {
     HashSet < SDWmapping > hashset = new HashSet < SDWmapping > ();
     boolean failure = true;
     PriorityQueue < SDWmapping > frontier = new PriorityQueue < SDWmapping > (100, new MinimumDistaceComparer());

     frontier.add(source);
     boolean flag = true;
     while (flag || frontier.isEmpty() == false && failure == true) {
         flag = false;
         SDWmapping current_city = frontier.poll();
         if (current_city.val.equalsIgnoreCase(destination.val)) {
             failure = false;
         }
         hashset.add(current_city);
         for (DWpairs e: current_city.successors) {
             SDWmapping successor = e.destination;
             int cost = e.distance;
             if (frontier.contains(successor) == false && hashset.contains(successor) == false) {
                 int successor_g = (current_city.distance -cityHeuristicPair.get(current_city.val)) + cost;
            	 successor.distance = successor_g + cityHeuristicPair.get(current_city.val);
                 
                 successor.source = current_city;
                 frontier.add(successor);
             } else if (frontier.contains(successor) == true && (successor.distance > (current_city.distance + cost))) {
                 successor.source = current_city;
                 int successor_g = (current_city.distance -cityHeuristicPair.get(current_city.val)) + cost;
            	 successor.distance = successor_g + cityHeuristicPair.get(current_city.val);
                 frontier.remove(successor); //removes old cost successor
                 frontier.add(successor); // adds updated successor
             }
         }
     }
 }
 
}


class Trace {
 public SDWmapping trace(String s1, Set < SDWmapping > set_path) {
     for (SDWmapping n: set_path) {
         if (n.val.equalsIgnoreCase(s1)) {
             return n;
         }
     }
     return null;
 }
}

//Display the final path after QueueImplementation has been called
class DisplayTrace {
 public DisplayTrace(SDWmapping last) {
     ArrayList < SDWmapping > path = new ArrayList < SDWmapping > ();
     for (SDWmapping sdwMapItem = last; sdwMapItem != null; sdwMapItem = sdwMapItem.source) {
         path.add(sdwMapItem);
     }
     Collections.reverse(path);
     if (last.distance == 0) {
         System.out.println("Distance : infinity");
     } else {
         System.out.println("distance: " + last.distance + " km");
     }

     System.out.println("route:");
     if (last.distance == 0) {
         System.out.println("none");
     } else {
         for (int i = 0; i <= path.size() - 1; i++) {
             SDWmapping temp, final_value;
             if (path.size() > i + 1) {
                 temp = path.get(i);
                 final_value = path.get(i + 1);
                 int difference = final_value.distance - temp.distance;
                 System.out.println("" + temp.val + " to " + final_value.val + ", " + difference + " km");
             }
         }
     }
 }
}