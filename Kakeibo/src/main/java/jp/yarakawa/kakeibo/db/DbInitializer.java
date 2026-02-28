package jp.yarakawa.kakeibo.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbInitializer {

    private DbInitializer() {}

    public static void init() {
        try (Connection con = Db.getConnection();
             Statement st = con.createStatement()) {

        	st.executeUpdate("""
        		    CREATE TABLE IF NOT EXISTS transactions (
        		        id          INTEGER PRIMARY KEY AUTOINCREMENT,
        		        date        TEXT    NOT NULL,
        		        type        TEXT    NOT NULL,
        		        category    TEXT    NOT NULL,
        		        amount      INTEGER NOT NULL,
        		        memo        TEXT,

        		        --  二重取り込み防止（同一内容は1件だけ）
        		        UNIQUE(date, type, category, amount, memo)
        		    );
        		""");
        	st.executeUpdate("""
        		    CREATE UNIQUE INDEX IF NOT EXISTS ux_transactions_dedup
        		    ON transactions(date, type, category, amount, memo);
        		""");

        } catch (SQLException e) {
            throw new IllegalStateException("DB初期化に失敗しました", e);
        }
    }
}