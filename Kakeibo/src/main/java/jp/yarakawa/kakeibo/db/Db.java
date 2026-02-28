package jp.yarakawa.kakeibo.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {

    private static final String APP_DIR_NAME = ".kakeibo";
    private static final String DB_FILE_NAME = "kakeibo.db";

    private Db() {}

    public static Connection getConnection() throws SQLException {
        ensureDbDirectory();
        String url = "jdbc:sqlite:" + dbPath().toAbsolutePath();
        return DriverManager.getConnection(url);
    }

    private static Path dbPath() {
        return appDir().resolve(DB_FILE_NAME);
    }

    private static Path appDir() {
        return Paths.get(System.getProperty("user.home"), APP_DIR_NAME);
    }

    private static void ensureDbDirectory() {
        try {
            Files.createDirectories(appDir());
        } catch (Exception e) {
            throw new IllegalStateException("DBフォルダ作成失敗", e);
        }
    }
}