package application;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Transaction {
    private final StringProperty type;
    private final StringProperty category;
    private final IntegerProperty amount;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty memo;

    public Transaction(String type, String category, int amount, LocalDate date, String memo) {
        this.type = new SimpleStringProperty(type);
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleIntegerProperty(amount);
        this.date = new SimpleObjectProperty<>(date);
        this.memo = new SimpleStringProperty(memo);
    }

    // getter
    public String getType() {
    	return type.get(); 
    	}
    
    public String getCategory() {
    	return category.get(); 
    	}
    
    public int getAmount() {
    	return amount.get(); 
    	}
    
    public LocalDate getDate() { 
    	return date.get(); 
    	}
    
    public String getMemo() { 
    	return memo.get(); 
    	}

    // ラムダ式で使うプロパティ getter
    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty memoProperty() {
        return memo;
    }
}
