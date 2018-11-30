package application;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class Main extends Application {
	ListView<String> listView;
	ArrayList<String> food;
	TextField foodInput,calorieInput, nameFilter;
	TableView table;
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Hello World!");
			BorderPane root = new BorderPane();
			VBox vbox = new VBox();
			HBox hbox = new HBox();
			HBox hbox2 = new HBox();
			Scene scene = new Scene(root,1280,720);
			food = new ArrayList();
			listView = new ListView<>();
			//table = new TableView();
			foodInput = new TextField();
			//calorieInput = new TextField();
			nameFilter = new TextField();
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			vbox.getChildren().add(getData());
			
			
			
			
			/**
			TableColumn foodColumn = new TableColumn("Food");
			foodColumn.setCellFactory(new PropertyValueFactory<>("food"));
			TableColumn calorieColumn = new TableColumn("Calories");
			calorieColumn.setCellFactory(new PropertyValueFactory<>("calorie"));
			table.setItems(getFood());
			table.getColumns().addAll(foodColumn,calorieColumn);
			**/
			listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			foodInput.setPromptText("food");
			//calorieInput.setPromptText("calorie");
			nameFilter.setPromptText("name filter");
			Button addButton = new Button("Add");
			Button deleteButton = new Button("Delete");
			Button nameFilterButton = new Button("Filter");
			Button nameUnfilterButton = new Button("Unfilter");
			hbox.getChildren().addAll(foodInput, addButton, deleteButton);
			hbox2.getChildren().addAll(nameFilter,nameFilterButton, nameUnfilterButton);
			//hbox.getChildren().addAll(foodInput, calorieInput, addButton, deleteButton);
			addButton.setOnAction((ActionEvent e) -> {
				//if(!(foodInput.getText().equals(""))) {
				//	listView.getItems().add(foodInput.getText());
				//}
				//foodInput.clear();
				Stage addFoodWindow = new Stage();
				BorderPane bp2 = new BorderPane();
				VBox vbox2 = new VBox();
				TextField foodName = new TextField();
				TextField calorieCount = new TextField();
				TextField fatGrams = new TextField();
				TextField carbGrams = new TextField();
				TextField fiberGrams = new TextField();
				TextField proteinGrams = new TextField();
				Label title = new Label();
				title.setText("Add food item with its nutrients");
				foodName.setPromptText("food name");
				calorieCount.setPromptText("calorie count");
				fatGrams.setPromptText("fat grams");
				carbGrams.setPromptText("carbohydrate grams");
				fiberGrams.setPromptText("fiber grams");
				proteinGrams.setPromptText("protein grams");
				foodName.setFocusTraversable(false);
				calorieCount.setFocusTraversable(false);
				fatGrams.setFocusTraversable(false);
				carbGrams.setFocusTraversable(false);
				fiberGrams.setFocusTraversable(false);
				proteinGrams.setFocusTraversable(false);
				Button submit = new Button("submit");
				submit.setOnAction(r -> addFoodWindow.close());
				vbox2.getChildren().addAll(title,foodName,calorieCount,fatGrams,
						carbGrams,fiberGrams,proteinGrams,submit);
				bp2.setCenter(vbox2);
				Scene popupScene = new Scene(bp2, 750, 450);
				addFoodWindow.setTitle("Add Food Item");
				addFoodWindow.setScene(popupScene);
				addFoodWindow.show();
				
			});
			deleteButton.setOnAction((ActionEvent e) -> {
				ObservableList<String> delete = listView.getSelectionModel().getSelectedItems();
				listView.getItems().removeAll(delete);
				//listView.getItems().remove(foodInput.getText());
				//foodInput.clear();
			});
			nameFilterButton.setOnAction((ActionEvent e) -> {
				Iterator iter = listView.getItems().iterator();
				while(iter.hasNext()) {
					String next = (String) iter.next();
					food.add(next);
					if(!next.contains(nameFilter.getText())) {
						iter.remove();
					}
				}
			});
			nameUnfilterButton.setOnAction((ActionEvent e) -> {
				listView.getItems().clear();;
				listView.getItems().addAll(food);
				food.clear();
				//listView.setItems(food);
			});
		    //vbox.getChildren().add(listView);
			vbox.getChildren().add(listView);
		    vbox.getChildren().add(hbox);
		    vbox.getChildren().add(hbox2);
		    root.setRight(vbox);
		    vbox.setStyle("-fx-background-color: red");
		    primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public HBox getData() {
		HBox hbox = new HBox();
		Button btn = new Button("Upload Data");
		TextField text = new TextField();
		text.setPromptText("file");
		text.setFocusTraversable(false);
		btn.setOnAction((ActionEvent e) -> {
			String filename = text.getText();
			File file = new File(filename);
			try {
				Scanner sc = new Scanner(file);
				while(sc.hasNextLine()) {
					String line = sc.nextLine();
					listView.getItems().add(line);;
					System.out.println(line);
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		hbox.getChildren().add(new Label("import:"));
		hbox.getChildren().add(text);
	    hbox.getChildren().add(btn);
	    hbox.setSpacing(10);
	    return hbox;
	}
	public ObservableList getFood() {
		ObservableList food = FXCollections.observableArrayList();
		return food;
	}
	public static void main(String[] args) {
		launch(args);
	}
}
