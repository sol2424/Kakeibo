module Kakeibo {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	requires javafx.graphics;
	requires java.sql;
	requires org.xerial.sqlitejdbc;
	
	opens application to javafx.graphics, javafx.fxml;
}
