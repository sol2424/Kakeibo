package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;


public class Controller  {

    @FXML private TextField categoryField; // 種類
    @FXML private TextField amountField;   // 金額
    @FXML private TextField memoField;     // メモ
    @FXML private DatePicker datePicker;   // 日付
    @FXML private RadioButton incomeRadio; // 収入
    @FXML private RadioButton expenseRadio;// 支出
    @FXML private Button addButton;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableView<Transaction> transactionTable2;
    @FXML private TableColumn<Transaction, String> categoryColumn, memoColumn;
    @FXML private TableColumn<Transaction, Integer> amountColumn;
    @FXML private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn2;
    @FXML private TableColumn<Transaction, Integer> amountColumn2;
    @FXML private TableColumn<Transaction, LocalDate> dateColumn2;
    @FXML private TableColumn<Transaction, String> memoColumn2;

    @FXML private ComboBox<Integer> yearComboBox, monthComboBox;
    @FXML private BarChart<String, Number> barChart;
    @FXML private CheckBox incomeCheckBox;
    @FXML private CheckBox expenseCheckBox;
    @FXML  private Label incomeTotalLabel;
    @FXML private Label expenseTotalLabel;


    private ObservableList<Transaction> masterData = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredData;
    
    @FXML
    public void initialize() {
        // ── ラジオボタン設定 ──
        ToggleGroup group = new ToggleGroup();
        incomeRadio.setToggleGroup(group);
        expenseRadio.setToggleGroup(group);
        expenseRadio.setSelected(true);

        // フィルター用データ準備
        filteredData = new FilteredList<>(masterData, p -> true);
        transactionTable.setItems(masterData);
        transactionTable2.setItems(filteredData);

        // コンボボックス初期化と連動
        updateYearMonthComboBoxes();
        yearComboBox.setOnAction(e -> updateFilter());
        monthComboBox.setOnAction(e -> updateFilter());
        incomeCheckBox.setOnAction(e -> updateFilter());
        expenseCheckBox.setOnAction(e -> updateFilter());

        // データ追加時に年・月を再更新
        masterData.addListener((ListChangeListener<Transaction>) c -> updateYearMonthComboBoxes());

        // 初回フィルター
        filterTransactions();
        
        categoryColumn.setCellValueFactory(c -> c.getValue().categoryProperty());
        amountColumn.setCellValueFactory(c -> c.getValue().amountProperty().asObject());
        dateColumn.setCellValueFactory(c -> c.getValue().dateProperty());
        memoColumn.setCellValueFactory(c -> c.getValue().memoProperty());

        
        categoryColumn2.setCellValueFactory(c -> c.getValue().categoryProperty());
        amountColumn2.setCellValueFactory(c -> c.getValue().amountProperty().asObject());
        dateColumn2.setCellValueFactory(c -> c.getValue().dateProperty());
        memoColumn2.setCellValueFactory(c -> c.getValue().memoProperty());

        transactionTable2.setItems(filteredData); // filteredDataは FilteredList<Transaction>

        transactionTable.setRowFactory(tv -> {	//コンテキストメニューで削除
            TableRow<Transaction> row = new TableRow<>();

            // コンテキストメニューを定義（各行ごとに個別に設定）
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("削除");

            // 削除を選んだときの処理
            deleteItem.setOnAction(event -> {
                Transaction item = row.getItem();
                if (item != null) {
                    transactionTable.getSelectionModel().select(item); // 念のため選択
                    masterData.remove(item);
                    updateBarChart();
                }
            });

            contextMenu.getItems().add(deleteItem);

            // コンテキストメニューの条件付き表示
            row.contextMenuProperty().bind(
                javafx.beans.binding.Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(contextMenu)
            );

            return row;
        });
        
        amountColumn.setCellFactory(col -> new TableCell<>() {    //収入は青文字、支出は赤文字で表示する
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item.toString());
                Transaction tx = getTableView().getItems().get(getIndex());
                setStyle("収入".equals(tx.getType())
                    ? "-fx-text-fill: blue;"
                    : "-fx-text-fill: red;");
            }
        });
        amountColumn2.setCellFactory(column -> new TableCell<Transaction,Integer>() {
            @Override
            protected void updateItem(Integer amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    setText(amount.toString());

                    if (transaction.getType().equals("収入")) {
                        setStyle("-fx-text-fill: blue;");
                    } else if (transaction.getType().equals("支出")) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle(""); // デフォルト
                    }

                }
            }
        });
    }

    
        private void updateYearMonthComboBoxes() {
        Set<Integer> years = new TreeSet<>();
        Set<Integer> months = new TreeSet<>();

        for (Transaction t : masterData) {
            years.add(t.getDate().getYear());
            months.add(t.getDate().getMonthValue());
        }

        yearComboBox.setItems(FXCollections.observableArrayList(years));
        monthComboBox.setItems(FXCollections.observableArrayList(months));
    }

    private void updateTotals() {
        int incomeTotal = filteredData.stream()
            .filter(t -> t.getType().equals("収入"))
            .mapToInt(Transaction::getAmount)
            .sum();

        int expenseTotal = filteredData.stream()
            .filter(t -> t.getType().equals("支出"))
            .mapToInt(Transaction::getAmount)
            .sum();

        incomeTotalLabel.setText(String.format("%,d円", incomeTotal));
        expenseTotalLabel.setText(String.format("%,d円", expenseTotal));
    }
    
    private void filterTransactions() {
        Integer selectedYear = yearComboBox.getValue();
        Integer selectedMonth = monthComboBox.getValue();
        if (selectedYear == null || selectedMonth == null) {
            return;
        }

        filteredData.setPredicate(t -> {
            boolean matchesDate =
                t.getDate().getYear() == selectedYear &&
                t.getDate().getMonthValue() == selectedMonth;

            boolean matchesType =
                (incomeCheckBox.isSelected() && t.getType().equals("収入")) ||
                (expenseCheckBox.isSelected() && t.getType().equals("支出"));

            return matchesDate && matchesType;
        });

        updateBarChart();  

        amountColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item.toString());
                Transaction tx = getTableView().getItems().get(getIndex());
                setStyle("収入".equals(tx.getType())
                    ? "-fx-text-fill: blue;"
                    : "-fx-text-fill: red;");
            }
        });
    }
    
    private void updateFilter() {
        Integer selectedYear = yearComboBox.getValue();
        Integer selectedMonth = monthComboBox.getValue();
        boolean showIncome = incomeCheckBox.isSelected();
        boolean showExpense = expenseCheckBox.isSelected();

        filteredData.setPredicate(transaction -> {
            if (transaction == null) return false;

            // 年月フィルター
            LocalDate date = transaction.getDate();
            if (selectedYear != null && date.getYear() != selectedYear) return false;
            if (selectedMonth != null && date.getMonthValue() != selectedMonth) return false;

            // 支出・収入フィルター
            String type = transaction.getType();
            if (type.equals("収入") && !showIncome) return false;
            if (type.equals("支出") && !showExpense) return false;
            return true;
        });
        
        updateTotals();
        updateBarChart(); 
    }

    @FXML
    private void handleDeleteSelectedItem() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
        	masterData.remove(selected);
            updateBarChart(); // 削除後にグラフも更新
        } else {
            showAlert("削除エラー", "削除する項目を選んでください。");
        } 
    }
  
    @FXML  //csvに保存
    private void handleSaveButton() {
        if (masterData.isEmpty()) {
            showAlert("保存エラー", "保存するデータがありません。");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv"))) {
            // ヘッダーを書く
            writer.write("Type,Category,Amount,Date,Memo");
            writer.newLine();

            // 各取引データを書き込む
            for (Transaction t :masterData) {
                writer.write(String.format("%s,%s,%d,%s,%s",
                        t.getType(),
                        t.getCategory(),
                        t.getAmount(),
                        t.getDate(),
                        t.getMemo().replace(",", " ") // メモにカンマが入るとCSV崩れるので空白に
                ));
                writer.newLine();
            }

            showAlert("保存成功", "データを transactions.csv に保存しました！");
        } catch (IOException e) {
            showAlert("保存エラー", "ファイル保存中にエラーが発生しました。");
            e.printStackTrace();
        }
    }

    @FXML  //データ読み込み
    private void handleLoadButton() {
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))) {
            String line;
            masterData.clear(); // まず今のデータを全部クリア

            // 1行目（ヘッダー）をスキップ
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1); // カンマ区切りで分解（-1で空白も拾う）

                if (fields.length >= 5) {
                    String type = fields[0];
                    String category = fields[1];
                    int amount = Integer.parseInt(fields[2]);
                    LocalDate date = LocalDate.parse(fields[3]);
                    String memo = fields[4];

                    Transaction transaction = new Transaction(type, category, amount, date, memo);
                    masterData.add(transaction);

                }
            }

            showAlert("読込成功", "transactions.csv からデータを読み込みました！");
            updateBarChart(); // グラフも更新
        } catch (IOException | NumberFormatException e) {
            showAlert("読込エラー", "ファイル読み込み中にエラーが発生しました。");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAddButton(ActionEvent event) {
        String category = categoryField.getText();
        String amountText = amountField.getText();
        LocalDate date = datePicker.getValue();
        String memo = memoField.getText();
        String type = incomeRadio.isSelected() ? "収入" : "支出";
        

        if (category.isEmpty() || amountText.isEmpty() || date == null) {
            showAlert("入力エラー", "全ての項目を入力してください。");
            return;
        }

        try {
            int amount = Integer.parseInt(amountText);
            Transaction newTransaction = new Transaction(type, category, amount, date,  memo);

            // masterData に追加（→ transactionTable に即反映）
            masterData.add(newTransaction);

            // フィルター再適用（transactionTable2にも自動反映）
            updateFilter();
            updateBarChart();
        } catch (NumberFormatException e) {
            showAlert("入力エラー", "金額は数値で入力してください。");
        }
          finally {
        	showAlert("登録完了", "項目を追加しました！");
        	clearInputs();
          }
    }

    private void updateBarChart() {
        // カテゴリごとの支出合計を集計（filteredDataを使用）
        Map<String, Integer> categoryTotals = filteredData.stream()
            .filter(t -> t.getType().equals("支出")) // 支出のみに限定
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingInt(Transaction::getAmount)
            ));

        // データクリア
        barChart.getData().clear();

        // カラー用CSSクラス配列
        String[] colors = {
            "bar-color-1", "bar-color-2", "bar-color-3",
            "bar-color-4", "bar-color-5", "bar-color-6"
        };

        // シリーズ作成
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("カテゴリ別支出");

        // 各データに追加
        for (Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());

            // ツールチップ追加
            Tooltip tooltip = new Tooltip(entry.getKey() + ": " + entry.getValue() + "円");
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(data.getNode(), tooltip); // ← Platform.runLater内に移動するので今はまだ効果なし

            series.getData().add(data);
        }

        barChart.getData().add(series);

        // 色分けとツールチップをノードに後から適用（描画後に必要）
        Platform.runLater(() -> {
            int i = 0;
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    // 色クラスを追加
                    node.getStyleClass().add(colors[i % colors.length]);

                    // ツールチップを再適用（nodeがnullでなければ）
                    Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue().intValue() + "円");
                    tooltip.setShowDelay(Duration.millis(100));
                    Tooltip.install(node, tooltip);
                }
                i++;
            }
        });
    }


    
    private void clearInputs() {
        categoryField.clear();
        amountField.clear();
        memoField.clear();
        datePicker.setValue(null);
        expenseRadio.setSelected(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
