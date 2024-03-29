package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
    // List of all the food items.
    private List<FoodItem> foodItemList;

    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    
    
    /**
     * Public constructor
     */
    public FoodData() {
        // TODO : Complete
    	foodItemList = new ArrayList<FoodItem>();
    	indexes = new HashMap<String, BPTree<Double, FoodItem>>();
    }
    
    
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     */
    @Override
    public void loadFoodItems(String filePath) {
        // TODO : Complete
    	
    	File inputFile = new File(filePath);
    	try {
	    	Scanner in = new Scanner(inputFile);
	    	int skipCount = 0;
	    	
	    	while(in.hasNextLine()) {
	    		String line = in.nextLine();
	    		String[] values = line.split(",");
	    		
	    		if(values.length == 12) {
	    			try {
		    			String id = values[0];
		    			String name = values[1];
		    			String calories = values[2];
		    			Double calorieCount = Double.parseDouble(values[3]);
		    			String fat = values[4];
		    			Double fatCount = Double.parseDouble(values[5]);
		    			String carbohydrates = values[6];
		    			Double carbohydratesCount = Double.parseDouble(values[7]);
		    			String fiber =  values[8];
		    			Double fiberCount = Double.parseDouble(values[9]);
		    			String protein = values[10];
		    			Double proteinCount = Double.parseDouble(values[11]);
	    			
		    			if(!(calories.equalsIgnoreCase("calories") && fat.equalsIgnoreCase("fat") && carbohydrates.equalsIgnoreCase("carbohydrate")
		    					&& fiber.equalsIgnoreCase("fiber") && protein.equalsIgnoreCase("protein"))) {
		    				++skipCount;
		    			}
		    			else {
		    				//construct new foodItem
		    				
		    				FoodItem newItem = new FoodItem(id, name);
		    				newItem.addNutrient(calories, calorieCount);
		    				newItem.addNutrient(fat, fatCount);
		    				newItem.addNutrient(carbohydrates, carbohydratesCount);
		    				newItem.addNutrient(fiber, fiberCount);
		    				newItem.addNutrient(protein, proteinCount);
		    				
		    				foodItemList.add(newItem);
		    				
		    			}
		    				
		    			
	    			}catch(NumberFormatException e) {
	    				++skipCount;
	    			}
	    		}
	    		else {
	    			++skipCount;
	    		}
	    	}
	    	in.close();
	    	//System.out.println(skipCount);
    	
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
    	}
    	
    	Collections.sort(foodItemList, (a,b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase()));
    }
    
    private void addFoodToHashMap(FoodItem newItem) {
    	
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
        // TODO : Complete
    	
    	List<FoodItem> filteredList = new ArrayList<FoodItem>();
    	
    	for(FoodItem food : foodItemList) {
    		if(food.getName().toLowerCase().contains(substring.toLowerCase())) {
    			filteredList.add(food);
    		}
    	}
    	
        Collections.sort(filteredList, (a, b) -> a.getName().compareTo(b.getName()));
        return filteredList;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
        // TODO : Complete
    	
    	List<FoodItem> filteredList = new ArrayList<FoodItem>(foodItemList);
    	
    	for(String rule : rules) {
    		String[] ruleArray = rule.split(" ");
    		String nutrient = ruleArray[0];
    		String comparator = ruleArray[1];
    		Double value = Double.parseDouble(ruleArray[2]);
    		
    		if(comparator.contentEquals(">=")) {
    			Iterator<FoodItem> foodIterator = filteredList.iterator();
    			while(foodIterator.hasNext()) {
    				FoodItem current = foodIterator.next();
    				Double foodValue = current.getNutrientValue(nutrient);
    				if(!(foodValue >= value)) {
    					foodIterator.remove();
    				}
    			}
    		}
    		else if(comparator.contentEquals("<=")) {
    			Iterator<FoodItem> foodIterator = filteredList.iterator();
    			while(foodIterator.hasNext()) {
    				FoodItem current = foodIterator.next();
    				Double foodValue = current.getNutrientValue(nutrient);
    				if(!(foodValue <= value)) {
    					foodIterator.remove();
    				}
    			}
    		}
    		else if(comparator.contentEquals("==")) {
    			Iterator<FoodItem> foodIterator = filteredList.iterator();
    			while(foodIterator.hasNext()) {
    				FoodItem current = foodIterator.next();
    				Double foodValue = current.getNutrientValue(nutrient);
    				if(!(foodValue.equals(value))) {
    					foodIterator.remove();
    				}
    			}
    		}
    	}
    	
    	Collections.sort(filteredList, (a, b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase()));
        return filteredList;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        // TODO : Complete
    	foodItemList.add(foodItem);
    	// do stuff with hashmap
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        // TODO : Complete
        return foodItemList;
    }
    
    public void saveFoodItems(String filename) {
    	File saveFile = new File(filename);
    	
    	Collections.sort(foodItemList, (a,b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase()));
    	
    	try {
    		FileWriter f = new FileWriter(saveFile);
    		BufferedWriter b = new BufferedWriter(f);
    		for(FoodItem food : foodItemList) {
    			Double calories = food.getNutrientValue("calories");
    			Double fat = food.getNutrientValue("fat");
    			Double carbohydrate = food.getNutrientValue("carbohydrate");
    			Double fiber = food.getNutrientValue("fiber");
    			Double protein = food.getNutrientValue("protein");
    			
    			String output = food.getID() + "," + food.getName() + "," + "calories" + ","
    			+ calories + "," + "fat" + "," + fat + "," + "carbohydrate" + "," + carbohydrate + ","
    			+ "fiber" + "," + fiber + "," + "protein" + "," + protein;
    			
    			b.write(output);
    			b.newLine();
    		}
    		b.close();
    		f.close();
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
    	}
    	
    	
    }
    

}
