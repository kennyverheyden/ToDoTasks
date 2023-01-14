package todotasks;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Listeners {

	public static void main(String[] args) {

		// int
		SimpleIntegerProperty intProp = new SimpleIntegerProperty();
		intProp.set(10);
		System.out.println(intProp.get());

		// String
		SimpleStringProperty stringProp = new SimpleStringProperty("Initial value");
		System.out.println(stringProp.get());

		// Read only properties
		// Boolean
		ReadOnlyBooleanWrapper readOnlyBooleanWrapper = new ReadOnlyBooleanWrapper(true);
		ReadOnlyBooleanProperty readOnlyBooleanProperty = readOnlyBooleanWrapper.getReadOnlyProperty(); // heeft geen set methode, alleen get methode
		System.out.println(readOnlyBooleanProperty.get());

		// Listener
		intProp.addListener(new ChangeListener<Number>() { 

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				System.out.println("Listener 1: Integer property is change to " + newValue);
				System.out.println("Old value was: " +oldValue);
				System.out.println(observable);
			}
		});

		intProp.set(15);
		intProp.set(100);

		intProp.addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				System.out.println("Int property changed");
			}

		});

		intProp.set(15);
		intProp.set(100);
	}

}