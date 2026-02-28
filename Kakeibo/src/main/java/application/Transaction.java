package application;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Transaction {

    // DB用ID（未保存のときは0）
    private final LongProperty id;

    private final StringProperty type;
    private final StringProperty category;
    private final IntegerProperty amount;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty memo;

    /**
     * 画面入力など「まだDBに保存していない」取引用（id=0）
     */
    public Transaction(String type, String category, int amount, LocalDate date, String memo) {
        this(0L, type, category, amount, date, memo);
    }

    /**
     * DBから読み込んだ取引用（idあり）
     */
    public Transaction(long id, String type, String category, int amount, LocalDate date, String memo) {
        this.id = new SimpleLongProperty(id);
        this.type = new SimpleStringProperty(type);
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleIntegerProperty(amount);
        this.date = new SimpleObjectProperty<>(date);
        this.memo = new SimpleStringProperty(memo == null ? "" : memo);
    }

    // --- getters ---
    public long getId() { return id.get(); }
    public String getType() { return type.get(); }
    public String getCategory() { return category.get(); }
    public int getAmount() { return amount.get(); }
    public LocalDate getDate() { return date.get(); }
    public String getMemo() { return memo.get(); }

    // --- setters（DB insert後にidを反映するために必要） ---
    public void setId(long id) { this.id.set(id); }
    public void setType(String type) { this.type.set(type); }
    public void setCategory(String category) { this.category.set(category); }
    public void setAmount(int amount) { this.amount.set(amount); }
    public void setDate(LocalDate date) { this.date.set(date); }
    public void setMemo(String memo) { this.memo.set(memo == null ? "" : memo); }

    // --- property getters(ラムダ式で使用) ---
    public LongProperty idProperty() { return id; }
    public StringProperty typeProperty() { return type; }
    public StringProperty categoryProperty() { return category; }
    public IntegerProperty amountProperty() { return amount; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty memoProperty() { return memo; }

    @Override
    public String toString() {
        return "Transaction{id=" + getId()
                + ", type=" + getType()
                + ", category=" + getCategory()
                + ", amount=" + getAmount()
                + ", date=" + getDate()
                + ", memo=" + getMemo()
                + "}";
    }
}