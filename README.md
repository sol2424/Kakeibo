# 家計簿アプリ製作

# 1.はじめに

Java Silver SE17の学習成果をアピールすること、  
そして自分自身の日常的な家計管理に役立てることを目的として、  
デスクトップ上で動作するGUI形式の家計簿アプリを開発しました。


## 使用技術    
- Java SE17  
- javafx-sdk-17.0.15  
- JavaFX Scene Builder 23.0.1

## デモ動画

https://github.com/user-attachments/assets/a1aabdec-323e-481d-8f3b-20a928b04f0a

## 利用方法  
1.家計簿アプリ.zipをダウンロード  

2.Gluon様公式サイト(https://gluonhq.com/products/javafx/) よりJavaFX SDK17のバージョンをダウンロード  

3.ダウンロードしたフォルダ内のlibフォルダとbinフォルダを家計簿アプリの  
  javafx-sdkフォルダ内へコピーしてください。  

4.以上の作業が完了しましたら家計簿アプリ.exeをクリックして起動してください。  

  
# 2.機能一覧(追加順）  

- 収入・支出の入力（項目・金額・日付）とリストの表示   
- csvによるデータ保存/読み込み機能  
- フィルター機能を用いた合計表示機能  
- グラフ表示機能  
- 右クリックでのリスト項目の削除機能  
- CSSを利用し背景画像を追加  
  - 使用画像：OKUMONO様より かわいい雲の柄背景　https://sozaino.site/archives/6411#google_vignette
<br>
<br>  

 - 本アプリの動作テストについて

   - 実際にアプリを動かしながら動作確認と修正を繰り返し、バグ対応を進めました。  
   Eclipseのエラー表示を手がかりに、エラーの原因を調査したり、ChatGPTなどのツールも活用することで、  
   問題の解決に取り組みました。  


# 3.工夫したこと  

- String型「type」を用いて「収入」、「支出」を識別する仕組みにしました。  
  ラジオボタンで「収入」または「支出」を選択し、その情報を登録できるようになっています。  
  これにより、収入と支出を符号で区別する必要がなく、入力のわかりやすさを重視しました。
```java:Controller
    @FXML
    private void handleAddButton(ActionEvent event) {
        String category = categoryField.getText();
        String amountText = amountField.getText();
        LocalDate date = datePicker.getValue();
        String memo = memoField.getText();
        String type = incomeRadio.isSelected() ? "収入" : "支出";
```  
- 表示面では、リストに反映される際に「収入」は青文字、「支出」は赤文字で表示することで、可読性を高めています。  
  
```java:Controller
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
```
 
- フィルター機能においても、typeの情報を参照して収入・支出を判別し、表示を切り替える仕組みにしています。   
 ```java:Controller
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
```
また、将来的には予算設定の機能を追加する際に「支出」で取得した数値をマイナスで返す処理を実装するために利用する予定となっています。  

# 4.今後の課題及び追加予定機能 

本アプリケーションは現在も開発を継続しており、以下の機能追加・改善を予定しています。

<details><summary>固定費の自動反映機能</summary>  

- ユーザーが一度固定費を登録すると、指定した日付に毎月自動的にリストへ反映される仕組みを追加予定。  
</details>  


<details><summary>間違えて項目を削除してしまった場合に元に戻す方法がない</summary>  

- Ctrl + Z によるUNDO（元に戻す）機能を実装し対応予定　　

</details>  


<details><summary>週・月ごとの予算設定機能</summary>  

- 予算オーバーを可視化し、より計画的な家計管理をサポートする。  　

</details>  

<details><summary>保存形式のデータベース化（SQLite等）</summary>  

- 現在のCSV保存からデータベース管理へ移行し、データの整合性・拡張性を向上させる  
（優先度は低めだが、データベースの学習を兼ねて検討中）。    　

</details>  

<details><summary>iOSアプリとしての再設計と開発</summary>  

- 最終的な目標として、Swiftを用いて本アプリと同様の機能を持つiPhone対応アプリを開発予定。  
Swiftの学習と、上記の機能実装が完了した段階で着手する予定。 　

</details>  



# 5.おわりに

実際にアプリ開発を行ったことで、日常的に利用しているアプリがいかに高度に作り込まれているかを実感しました。  
今回の開発では「正常にアプリが動作すること」を目標に取り組みましたが、今後はさらに知識を深め、  
品質・セキュリティ面などの観点にも意識を向けて開発を進めていきたいと考えています。
