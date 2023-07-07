package todotasks;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.HashMap;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import todotasks.Task;

public class Controller {

	private Task currentTask = new Task();

	private final ObservableList<Task> tasks = FXCollections.observableArrayList();

	private final HashMap<Integer, Task> taskMap = new HashMap<>();


	@FXML
	private Button add;

	@FXML
	private Button delete;

	@FXML
	private Button cancel;

	@FXML
	private CheckBox completedCheckBox;

	@FXML
	private ComboBox<String> priorities;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private TableColumn<Task, String> priorityColumn;

	@FXML
	private TableColumn<Task, String> descriptionColumn;

	@FXML
	private TableColumn<Task, String> progressColumn;

	@FXML
	private Spinner<Integer> progressSpinner;

	@FXML
	private TextField taskDescription;

	@FXML
	private TableView<Task> tasksTable;

	int lastId = 0;


	@FXML
	void deleteButtonClicked(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText("Are you sure you want to delete?");
		alert.setTitle("Confirm delte");
		alert.getButtonTypes().remove(0,2);
		alert.getButtonTypes().add(0, ButtonType.YES);
		alert.getButtonTypes().add(1, ButtonType.NO);

		Optional<ButtonType> confirmationResponse = alert.showAndWait();
		if(confirmationResponse.get() ==ButtonType.YES)
		{
			Task task = taskMap.get(currentTask.getId());
			int id=task.getId();
			tasks.remove(task);
			taskMap.remove(id,task);
			emptyTaskField();
			tasksTable.getSelectionModel().clearSelection(); 
		}	
	}

	@FXML
	void addButtonClicked(ActionEvent event) {
		System.out.println("add button clicked");
		if(currentTask.getId()==null) {
			Task newTask = new Task(++lastId, currentTask.getDescription(),currentTask.getPriority(),currentTask.getProgress());
			taskMap.put(lastId, newTask);
			tasks.add(newTask);
		}
		else {
			// update
			Task task = taskMap.get(currentTask.getId());
			task.setDescription(currentTask.getDescription());
			task.setPriority(currentTask.getPriority());
			task.setProgress(currentTask.getProgress());
			if(this.completedCheckBox.isSelected())
			{
				task.setProgress(100);
			}
		}
		emptyField();
	}

	private void emptyField() {
		currentTask.setDescription("");
		currentTask.setPriority("");
		currentTask.setProgress(0);
		currentTask.setId(null);
	}

	@FXML
	void cancelButtonClicked(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText("Are you sure you want to cancel?");
		alert.setTitle("Cancelling update");
		alert.getButtonTypes().remove(0,2);
		alert.getButtonTypes().add(0, ButtonType.YES);
		alert.getButtonTypes().add(1, ButtonType.NO);
		Optional<ButtonType> confirmationResponse = alert.showAndWait();
		if(confirmationResponse.get() ==ButtonType.YES)
		{
			emptyTaskField();
			tasksTable.getSelectionModel().clearSelection();
		}
	}

	private void emptyTaskField() {
		currentTask.setDescription("");
		currentTask.setPriority("");
		currentTask.setProgress(0);
		currentTask.setId(null);
	}

	public void initialize() {

		priorities.getItems().addAll("High","Medium","Low");
		progressSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,100, 0));

		progressSpinner.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> arg0, Integer oldValue, Integer newValue) {
				if(newValue==100)
				{
					completedCheckBox.setSelected(true);
				}
				else
				{
					completedCheckBox.setSelected(false);
				}

			}
		});

		ReadOnlyIntegerProperty intProgress = ReadOnlyIntegerProperty.readOnlyIntegerProperty(progressSpinner.valueProperty());

		progressBar.progressProperty().bind(intProgress.divide(100));

		priorities.valueProperty().bindBidirectional(currentTask.priorityProperty());
		taskDescription.textProperty().bindBidirectional(currentTask.descriptionProperty());
		progressSpinner.getValueFactory().valueProperty().bindBidirectional(currentTask.progressProperty());

		tasksTable.setItems(tasks);

		priorityColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("priority"));

		descriptionColumn.setCellValueFactory(rowData -> rowData.getValue().descriptionProperty());

		progressColumn.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Task, String> arg) {
				return Bindings.concat(arg.getValue().progressProperty(), "%");
			}
		});


		StringBinding addButtonTextBinding = new StringBinding() {
			{
				super.bind(currentTask.idProperty());
			}
			@Override
			protected String computeValue() {
				return currentTask.getId() == null ? "Add" : "Update";
			}
		};
		add.textProperty().bind(addButtonTextBinding);

		add.disableProperty().bind(Bindings.greaterThan(3,currentTask.descriptionProperty().length()));

		tasksTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {

			@Override
			public void changed(ObservableValue<? extends Task> observable, Task oldValue, Task newValue) {
				setCurrentTask(newValue);

			}
		});

		taskDescription.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event){
				System.out.println("action on description");
			}
		});
	}

	private void setCurrentTask(Task task) 
	{
		if(task != null)
		{
			currentTask.setId(task.getId());
			currentTask.setDescription(task.getDescription());
			currentTask.setPriority(task.getPriority());
			currentTask.setProgress(task.getProgress());
		}
	}

	public HashMap<Integer, Task> getTaskMap() {
		return taskMap;
	}

	public void setTasksMap(HashMap<Integer, Task> initialTasksMap)
	{
		taskMap.clear();
		tasks.clear();
		taskMap.putAll(initialTasksMap);
		tasks.addAll(initialTasksMap.values());
		int id=0;
		for (HashMap.Entry<Integer, Task> entry : initialTasksMap.entrySet()) {
			Integer key = entry.getKey();
			if(key>id)
			{
				id=key;
			}
		}
		lastId=id;
	}
}