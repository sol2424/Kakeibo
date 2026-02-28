package jp.yarakawa.kakeibo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.Transaction;
import jp.yarakawa.kakeibo.db.Db;

public class TransactionDao {

    // 追加：Transactionを受け取り、採番されたidを返す
    public long insert(Transaction t) {
        String sql = """
            INSERT INTO transactions(date, type, category, amount, memo)
            VALUES(?, ?, ?, ?, ?)
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getDate().toString());
            ps.setString(2, t.getType());
            ps.setString(3, t.getCategory());
            ps.setInt(4, t.getAmount());
            ps.setString(5, t.getMemo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            return -1;

        } catch (SQLException e) {
            throw new IllegalStateException("insert失敗", e);
        }
    }

    // ★ここが重要：Rowではなく Transaction を返す
    public List<Transaction> findAll() {
        String sql = """
            SELECT id, date, type, category, amount, memo
            FROM transactions
            ORDER BY date DESC, id DESC
        """;

        List<Transaction> list = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                String type = rs.getString("type");
                String category = rs.getString("category");
                int amount = rs.getInt("amount");
                String memo = rs.getString("memo");

                list.add(new Transaction(id, type, category, amount, date, memo));
            }
            return list;

        } catch (SQLException e) {
            throw new IllegalStateException("findAll失敗", e);
        }
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("delete失敗 id=" + id, e);
        }
    }
 // 全件削除（取り込み前にDBを空にしたいとき用）
    public void deleteAll() {
        String sql = "DELETE FROM transactions";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("deleteAll失敗", e);
        }
    }

    // 一括INSERT（トランザクション + バッチで高速）
    public int insertBatch(java.util.List<application.Transaction> list) {
        String sql = """
            INSERT OR IGNORE INTO transactions(date, type, category, amount, memo)
            VALUES(?, ?, ?, ?, ?)
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);

            for (application.Transaction t : list) {
                ps.setString(1, t.getDate().toString());
                ps.setString(2, t.getType());
                ps.setString(3, t.getCategory());
                ps.setInt(4, t.getAmount());
                ps.setString(5, t.getMemo() == null ? "" : t.getMemo());
                ps.addBatch();
            }

            int inserted = 0;
            int[] results = ps.executeBatch();
            // SQLite JDBC: INSERT成功=1、無視=0 が多い
            for (int r : results) {
                if (r > 0) inserted++;
            }

            con.commit();
            con.setAutoCommit(true);

            return inserted;

        } catch (SQLException e) {
            throw new IllegalStateException("insertBatch失敗", e);
        }
    }
}

