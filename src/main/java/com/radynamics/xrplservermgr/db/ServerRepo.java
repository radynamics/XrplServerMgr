package com.radynamics.xrplservermgr.db;

import com.radynamics.xrplservermgr.db.dto.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerRepo implements AutoCloseable {
    private final Connection conn;

    private ServerRepo(Connection conn) {
        if (conn == null) throw new IllegalArgumentException("Parameter 'conn' cannot be null");
        this.conn = conn;
    }

    public ServerRepo() {
        this(Database.connect());
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }

    public List<Server> list() throws SQLException {
        var ps = conn.prepareStatement("SELECT * FROM server");
        var rs = ps.executeQuery();

        var list = new ArrayList<Server>();
        while (rs.next()) {
            var s = new Server();
            list.add(s);
            s.id(rs.getInt("id"));
            s.uuid(UUID.fromString(rs.getString("uuid")));
            s.displayText(rs.getString("displayText"));
            s.host(rs.getString("host"));
            s.port(rs.getInt("port"));
            s.username(rs.getString("username"));
            s.keyFile(rs.getString("keyFile"));
        }
        return list;
    }

    public void saveOrUpdate(Server server) throws Exception {
        String sql = "INSERT OR REPLACE INTO server (id, uuid, displayText, host, port, username, keyFile)"
                + "	    VALUES ((SELECT id FROM server WHERE id = ?), ?, ?, ?, ?, ?, ?);";
        var ps = conn.prepareStatement(sql);
        ps.setInt(1, server.id());
        ps.setString(2, server.uuid().toString());
        ps.setString(3, server.displayText());
        ps.setString(4, server.host());
        ps.setInt(5, server.port());
        ps.setString(6, server.username());
        ps.setString(7, server.keyFile() == null ? "" : server.keyFile());

        ps.executeUpdate();
        if (server.isNew()) {
            server.id(Database.lastInsertRowId(conn));
        }
    }

    public void delete(Server server) throws SQLException {
        String sql = "DELETE FROM server WHERE id = ?;";
        var ps = conn.prepareStatement(sql);
        ps.setInt(1, server.id());

        ps.executeUpdate();
    }

    public void commit() throws SQLException {
        conn.commit();
    }
}
