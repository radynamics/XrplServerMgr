package com.radynamics.xrplservermgr.db;

import com.radynamics.xrplservermgr.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteException;
import org.sqlite.mc.SQLiteMCSqlCipherConfig;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Database {
    final static Logger log = LogManager.getLogger(Database.class);
    public static File dbFile = defaultFile();
    public static String password = null;

    public static File defaultFile() {
        return Paths.get(Configuration.root().toString(), "xrplservermgr.db").toFile();
    }

    public static Connection connect() {
        if (defaultFile().equals(dbFile)) {
            createDefaultDirectory();
        }

        Connection conn = null;
        try {
            conn = connect(password);
            if (conn == null) {
                log.error(String.format("Could not open db %s", dbFile));
                return null;
            }
            conn.setAutoCommit(false);
            createTables(conn);
            var m = new DbMigration(conn);
            m.migrateToLatest();

            return conn;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                if (conn != null) {
                    conn.rollback();
                    conn.close();
                }
            } catch (SQLException e2) {
                log.error(e2.getMessage(), e2);
            }
            return null;
        }
    }

    private static Connection connect(String password) throws SQLException {
        String url = String.format("jdbc:sqlite:file:%s", dbFile);
        try {
            return SQLiteMCSqlCipherConfig.getV4Defaults()/*.withKey(password)*/.build().createConnection(url);
        } catch (SQLiteException sle) {
            if (sle.getResultCode().name().equals("SQLITE_NOTADB")) {
                return null;
            }
            throw sle;
        }
    }

    private static void createDefaultDirectory() {
        var parent = defaultFile().getParentFile();
        if (!parent.exists()) {
            parent.mkdir();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        var sql = "CREATE TABLE IF NOT EXISTS config (\n"
                + "	    id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	    key text NOT NULL UNIQUE,\n"
                + "	    value text NOT NULL\n"
                + "   );";
        conn.createStatement().execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS server (\n"
                + "	   id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	   uuid text NOT NULL,\n"
                + "	   displayText text NOT NULL,\n"
                + "	   host text NOT NULL,\n"
                + "	   port text NOT NULL,\n"
                + "	   username text NOT NULL,\n"
                + "	   keyFile text NOT NULL\n"
                + ");";
        conn.createStatement().execute(sql);
    }

    public static Optional<String> singleString(ResultSet rs, String column) throws Exception {
        if (!rs.next()) {
            return Optional.empty();
        }
        var value = rs.getString("value");
        if (rs.next()) {
            throw new DbException(String.format("More than one record found for %s in %s", value, column));
        }
        return Optional.of(value);
    }

    public static void executeUpdate(PreparedStatement ps, int expectedRowsAffected) throws SQLException {
        var affected = ps.executeUpdate();
        if (affected != expectedRowsAffected) {
            throw new SQLException(String.format("%s rows affected but expected %s", affected, expectedRowsAffected));
        }
    }

    public static boolean exists() {
        return dbFile.exists();
    }

    public static Integer lastInsertRowId(Connection conn) throws SQLException {
        var ps = conn.prepareStatement("SELECT last_insert_rowid() AS rowId");
        var rs = ps.executeQuery();
        rs.next();
        return rs.getInt("rowId");
    }
}
