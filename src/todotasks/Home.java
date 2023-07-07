package todotasks;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Home extends Application{

	Controller controller; // Get access to Controller class

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
		GridPane gridPane = loader.load();
		controller = loader.getController();

		primaryStage.setResizable(false);
		Scene scene = new Scene(gridPane);

		primaryStage.setAlwaysOnTop(false);
		primaryStage.setScene(scene);

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				System.out.println("application is closing.");
				onClose();

			}
		});

		controller.setTasksMap(readTasksFile());
		primaryStage.show();

	}
	
	private HashMap<Integer, Task> readTasksFile() {
		FileInputStream in = null;

		HashMap<Integer, Task> tasksMap = new HashMap<Integer, Task>();
		try {
			in = new FileInputStream("./tasks.xml");
			XMLDecoder decoder = new XMLDecoder(in);
			tasksMap  = (HashMap<Integer, Task>) decoder.readObject();
			decoder.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			return tasksMap;
		}
	}

	private void onClose(){
		FileOutputStream out = null;
		try {
			out = new FileOutputStream("tasks.xml");
			XMLEncoder encoder = new XMLEncoder(out); // Serialization
			encoder.writeObject(controller.getTaskMap());
			encoder.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
