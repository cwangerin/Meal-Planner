package application;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;
    
    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;
    
    
    /**
     * Public constructor
     *
     * Initializes the BPTree with a root and the given branching factor
     * New root must be a LeafNode
     * 
     * @param branchingFactor The given branching factor of the tree (>2)
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException(
               "Illegal branching factor: " + branchingFactor);
        }
        
        this.branchingFactor = branchingFactor;
        root = new LeafNode();
        
    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
     */
    @Override
    public void insert(K key, V value) {
    	root.insert(key, value);
    	
    }
    
    
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
     */
    @Override
	public List<V> rangeSearch(K key, String comparator) {
		// Return just an empty list if the comparator is invalid or if the key is invalid.
		if (!comparator.contentEquals(">=") && !comparator.contentEquals("==") && !comparator.contentEquals("<="))
			return new ArrayList<V>();
		if (key == null) {
			return new ArrayList<V>();
		}

		return root.rangeSearch(key, comparator);
	}
    
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }
    
    
    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan
     */
    private abstract class Node {
        
        // List of keys
        List<K> keys;
        
        /**
         * Package constructor
         * 
         * A node will always have a list of keys in order to hold the keys.
         */
        Node() {
            keys = new ArrayList<K>();
        }
        
        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();
        
        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();
        
        /*
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * 
         * @return boolean
         */
        abstract boolean isOverflow();
        
        public String toString() {
            return keys.toString();
        }
    
    } // End of abstract class Node
    
    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;
        
        /**
         * Package constructor
         * 
	 	 * Initialize the InternalNode's list of keys and children.
         */
        InternalNode() {
            super();
            children = new ArrayList<Node>();
        }
        
        /**
         * Gets the first leaf key of the child in the first position.
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return children.get(0).getFirstLeafKey();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
        	
        	if (children.size() > branchingFactor) {
        		return true;
        	}
        	else {
        		return false;
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
        	/*
        	 * Method variables:
        	 * int correctIndex holds the correct index in which a key should go into the keys list.
        	 * Node child holds the child in which to insert a key, value pair into.
        	 * Node sibling holds the new sibling of the split node.
        	 * K promotedKey holds the key that is supposed to be placed into the node's parent.
        	 * InternalNode newRoot holds the new root if the original root has to be split.
        	 */
        	
        	// binarySearch is used to find where a key should go in the list. 
        	int correctIndex = Collections.binarySearch(keys, key);
        	// If the index is negative, it shows the position where it would go if it were added.
        	if(correctIndex < 0) {
        		// Reversing the negative so that it can be successfully placed in list.
        		correctIndex = (-1)*(correctIndex+1);
        	}
        	
        	// Gets the correct child and then inserts the key, value pair.
        	Node child = children.get(correctIndex);
        	child.insert(key, value);
        	
        	// Checking if the child that we inserted the key, value into has over-flown.
        	if(child.isOverflow()) {
        		// Splitting the node and getting the sibling.
        		Node sibling = child.split();
        		K promotedKey = sibling.getFirstLeafKey();
        		
        		// Obtaining the position to promote the key to.
        		int correctIndex2 = Collections.binarySearch(keys, promotedKey);
        		if(correctIndex2 < 0) {
        			correctIndex2 = (-1)*(correctIndex2 +1);
        		}
        		
        		// Finally, add promoted key to the parents and then sibling to the children.
        		keys.add(correctIndex2, promotedKey);
        		children.add(correctIndex2 +1, sibling);
        		
        	}
        	
        	// Next, must check if the root has over-flown.
        	if(root.isOverflow()) {
        		InternalNode newRoot = new InternalNode();
        		
        		// Splitting the node, and then adding both sides to the new root node.
        		Node sibling = split();
        		newRoot.keys.add(sibling.getFirstLeafKey());
        		newRoot.children.add(this);
        		newRoot.children.add(sibling);
        		
        		// The new root node becomes the new root.
        		root = newRoot;
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	/*
        	 * Method variables:
        	 * int keySize holds the number of keys in the keys list.
        	 * int startingIndex holds the index where the split occurs.
        	 * int endingIndex holds the last index.
        	 * InternalNode sibling holds the data from splitting the node.
        	 */
        	
        	InternalNode sibling = new InternalNode();
        	int keySize = keys.size();
        	int startingIndex = keySize / 2 + 1;
        	int endingIndex = keySize;
        	
        	// Adding the keys and children that were split from the current node to the sibling.
        	sibling.keys.addAll(keys.subList(startingIndex, endingIndex));
        	sibling.children.addAll(children.subList(startingIndex, endingIndex+1));
        	
        	// Removing the keys and children in current node that were split from the current node.
        	keys.subList(startingIndex-1, endingIndex).clear();
        	children.subList(startingIndex, endingIndex + 1).clear();
        	
            return sibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
        	/*
        	 * Method variables:
        	 * Node lowestChild holds the child in the lowest index position.
        	 * Node highestChild holds the child in the last index position.
        	 * int index holds the index that the key should be found within the keys.
        	 * Node child holds the child at the index.
        	 */
        	
        	// Go down left (lowest) child for <=.
            if(comparator.contentEquals("<=")) {
            	Node lowestChild = children.get(0);
            	return lowestChild.rangeSearch(key, comparator);
            	
            }
            // Go down right (highest) child for >=.
            else if(comparator.contentEquals(">=")) {
            	Node highestChild = children.get(children.size()-1);
            	return highestChild.rangeSearch(key, comparator);
            }
            // Find where to go down if ==.
            else if(comparator.contentEquals("==")) {
            	// binarySearch is used to find the correct index.
            	int index = Collections.binarySearch(keys, key);
            	if(index < 0) {
            		index = -1*(index+1);
            	}
            	Node child = children.get(index);
            	return child.rangeSearch(key, comparator);
            }
            
            // Should never be reached, because we test for potential invalids previous to this.
            return new ArrayList<V>();
        }
    
    } // End of class InternalNode
    
    
    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan
     */
    private class LeafNode extends Node {
        
        // List of values
        List<V> values;
        
        // Reference to the next leaf node
        LeafNode next;
        
        // Reference to the previous leaf node
        LeafNode previous;
        
        /**
         * Package constructor
         * 
         * Initialize the LeafNode with a keys and values list.
         */
        LeafNode() {
            super();
            values = new ArrayList<V>();
        }
        
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            if(values.size() == branchingFactor) {
        		return true;
        	}
        	else {
        		return false;
        	}
            
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
            /*
             * Method variables:
             * Iterator<K> keyIterator is an iterator over the keys list.
             * int correctIndex holds the correct index to insert a key, value pair into.
             * Node sibling holds the sibling node that is created by a split.
             * InternalNode newRoot holds the node that will become the new root after a split.
             */
        	
        	// Find key to insert at and add key.
        	Iterator<K> keyIterator = keys.iterator();
        	if(!keyIterator.hasNext()) {
        		keys.add(key);
        		values.add(value);
        	}
        	else {
        		// Finding the correct index to insert key into.
        		int correctIndex = Collections.binarySearch(keys, key);
        		if(correctIndex < 0) {
        			correctIndex = -1*(correctIndex+1);
        		}
        		
	        	keys.add(correctIndex, key);
	        	values.add(correctIndex, value);
        	}
        	
        	// Must check if the root has over-flown.
        	if(root.isOverflow()) {
        		InternalNode newRoot = new InternalNode();
        		
        		// Stores the sibling made from splitting the root node.
        		Node sibling = split();
        		
        		// Adding the key that should be placed into the parents.
        		newRoot.keys.add(sibling.getFirstLeafKey());
        		
        		// Adding the two split nodes to the root as children.
        		newRoot.children.add(this);
        		newRoot.children.add(sibling);
        		
        		// newRoot becomes the new root now.
        		root = newRoot;
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	/*
        	 * Method variables:
        	 * LeafNode siblingNode holds the split node's sibling.
        	 * int keySize holds the size of the keys list.
        	 * int startingIndex holds the index that the split should occur at.
        	 * int endingIndex holds the end index of the split.
        	 */

        	LeafNode siblingNode = new LeafNode();
        	int keySize = keys.size();
        	int startingIndex = (keySize + 1) / 2;
        	int endingIndex = keySize;
        	
        	// Adding the split keys and values to the newly created sibling. 
        	siblingNode.keys.addAll(keys.subList(startingIndex, endingIndex));
        	siblingNode.values.addAll(values.subList(startingIndex, endingIndex));
        	
        	// Clearing the already split keys and values from the current node.
        	keys.subList(startingIndex, endingIndex).clear();
        	values.subList(startingIndex, endingIndex).clear();
        	
        	// Re-setting the LeafNode links.
        	if(next != null) {
        		next.previous = siblingNode;
        	}
        	
        	siblingNode.next = next;
        	siblingNode.previous = this;
        	
        	next = siblingNode;
        	
            return siblingNode;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
        	/*
        	 * Method variables:
        	 * List<V> masterList is the cumulative list of values that fall into the comparator's range.
        	 * LeafNode curNode holds the current node that we are in.
        	 * LeafNode temp temporarily holds the current node that we are in.
        	 */
        	
        	List<V> masterList = new ArrayList<V>();
        	
        	if(comparator.contentEquals("<=")) {
        		LeafNode curNode = this;
        		
        		// Continue to go to the next node while there's still nodes.
        		while(curNode != null && key.compareTo(curNode.getFirstLeafKey()) >= 0) {
        			// Comparing the individual keys.
        			for(K k : curNode.keys) {
        				if(k.compareTo(key) <= 0) {
        					masterList.add(curNode.values.get(curNode.keys.indexOf(k)));
        				}
        			}
        			curNode = curNode.next;
        		}
        		
        		return masterList;
        	}
        	
        	else if(comparator.contentEquals(">=")) {
        		LeafNode curNode = this;
        		
        		// Continue to go to the previous node while there's still nodes.
        		while(curNode != null && key.compareTo(curNode.keys.get(curNode.keys.size()-1)) <= 0) {
        			List<K> reversedKeys = new ArrayList<K>(curNode.keys);
        			List<V> reversedValues = new ArrayList<V>(curNode.values);
        			
        			// Using reverse in order to get to the end of the list, then traverse back.
        			Collections.reverse(reversedKeys);
        			Collections.reverse(reversedValues);
        			
        			for(K k : reversedKeys) {
        				// Comparing the individual keys.
        				if(k.compareTo(key) >= 0) {
        					masterList.add(reversedValues.get(reversedKeys.indexOf(k)));
        				}
        			}
        			
        			curNode = curNode.previous;
        			
        		}
        		
        		Collections.reverse(masterList);
        		return masterList;
        	}
        	
        	else if(comparator.contentEquals("==")) {
        		LeafNode temp = this;
        		
        		// Continue to go through nodes.
        		while(temp != null && key.compareTo(temp.getFirstLeafKey()) >= 0) {
        			// Comparing the individual keys.
	        		for(K k : temp.keys) {
	        			if(k.compareTo(key) == 0) {
	        				masterList.add(temp.values.get(temp.keys.indexOf(k)));
	        			}
	        		}
	        		temp = temp.next;
        		}
        	}
        	
        	
            return masterList;
        }
        
    } // End of class LeafNode
    
    
    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        //BPTree<Double, Double> bpTree = new BPTree<>(3);
//    	BPTree<Integer, Integer> bpTree2 = new BPTree<>(3);
        
        /*
        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Double j = dd[rnd1.nextInt(4)];
            list.add(j);
            bpTree.insert(j, j);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
        }
        //List<Double> filteredValues = bpTree.rangeSearch(0.2d, ">=");
        //System.out.println("Filtered values: " + filteredValues.toString());
         * 
         */
    	/*
    	for(int i = 0; i < 100; i++) {
    		bpTree2.insert(i,i);
    		System.out.println("\n\nTree structure: \n" + bpTree2.toString());
      
    	}
    	*/
    	/*
    	for(int i = 0; i < 10; i++) {
    		Random randGen = new Random();
    		int randInt = randGen.nextInt(100);
    		bpTree2.insert(randInt, randInt);
    		System.out.println("\n\nTree structure: \n" + bpTree2.toString());
    	}
    	*/
//    	BPTree<Integer, Integer> bpTree2 = new BPTree<>(3);
//		System.out.println(bpTree2);
//    	Random randGen = new Random();
//    	for(int i = 0; i < 100; i++) {
//    		int randomInt = randGen.nextInt(100);
//    		bpTree2.insert(randomInt, randomInt);
//    		if(Math.random() > 0.5) {
//    			bpTree2.insert(randomInt, randomInt);
//    		}
//    		System.out.println("\nTree structure: \n" + bpTree2.toString());
//    	}
    	
    	
//    	bpTree2.insert(2,2);
//    	bpTree2.insert(2, 2);
//    	bpTree2.insert(2,2);
//    	bpTree2.insert(1, 1);
//    	bpTree2.insert(3, 3);
//    	bpTree2.insert(0, 0);
//    	bpTree2.insert(0, 0);
//    	System.out.println("\n\nTree structure: \n" + bpTree2.toString());
//    	
//    	System.out.println(bpTree2.rangeSearch(16, "<="));
//    	System.out.println(bpTree2.rangeSearch(1,">="));
//    	System.out.println(bpTree2.rangeSearch(2, "=="));
    }

} // End of class BPTree
