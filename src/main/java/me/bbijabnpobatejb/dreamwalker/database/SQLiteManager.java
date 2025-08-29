package me.bbijabnpobatejb.dreamwalker.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.util.Vec3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class SQLiteManager {

    private Connection connection;
    private final ExecutorService dbExecutor = Executors.newFixedThreadPool(2);

    public void connect(String databasePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            createTables();
        } catch (Exception e) {
            e.printStackTrace(); // можно заменить на логгер
        }
    }

    @SneakyThrows
    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS chat_messages (" + "player_id TEXT NOT NULL, " + "player_name TEXT NOT NULL, " + "message TEXT NOT NULL, " + "timestamp INTEGER NOT NULL, " + "x REAL NOT NULL, " + "y REAL NOT NULL, " + "z REAL NOT NULL, " + "dimension INTEGER NOT NULL" + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveChatMessage(UUID playerId, String playerName, String message, long timestamp, Vec3 pos, int dimension) {
        dbExecutor.execute(() -> {
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO chat_messages (player_id, player_name, message, timestamp, x, y, z, dimension) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, playerId.toString());
                stmt.setString(2, playerName);
                stmt.setString(3, message);
                stmt.setLong(4, timestamp);
                stmt.setDouble(5, pos.xCoord);
                stmt.setDouble(6, pos.yCoord);
                stmt.setDouble(7, pos.zCoord);
                stmt.setInt(8, dimension);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Получить сообщения игрока за период времени.
     */
    public CompletableFuture<List<String>> getMessagesByPlayer(UUID playerId, long sinceTimestamp) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> messages = new ArrayList<>();
            String query = "SELECT message FROM chat_messages WHERE player_id = ? AND timestamp > ? ORDER BY timestamp DESC";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, playerId.toString());
                stmt.setLong(2, sinceTimestamp);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        messages.add(rs.getString("message"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return messages;
        }, dbExecutor);
    }

    /**
     * Получить сообщения от ВСЕХ игроков в радиусе `radius` от заданной позиции и по времени.
     */
    public CompletableFuture<List<ChatMessageEntry>> getMessagesNearby(Vec3 center, int dim, double radius, long sinceTimestamp) {
        return CompletableFuture.supplyAsync(() -> {
            List<ChatMessageEntry> result = new ArrayList<>();
            String query = "SELECT player_id, player_name, message, timestamp, x, y, z, dimension " + "FROM chat_messages WHERE dimension = ? AND timestamp > ? " + "AND ABS(x - ?) <= ? AND ABS(y - ?) <= ? AND ABS(z - ?) <= ?";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, dim);
                stmt.setLong(2, sinceTimestamp);
                stmt.setDouble(3, center.xCoord);
                stmt.setDouble(4, radius);
                stmt.setDouble(5, center.yCoord);
                stmt.setDouble(6, radius);
                stmt.setDouble(7, center.zCoord);
                stmt.setDouble(8, radius);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(new ChatMessageEntry(UUID.fromString(rs.getString("player_id")), rs.getString("player_name"), rs.getString("message"), rs.getLong("timestamp"), Vec3.createVectorHelper(rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")), rs.getInt("dimension")));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return result;
        }, dbExecutor);
    }

    public void shutdown() {
        dbExecutor.shutdownNow();
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Data
    @AllArgsConstructor
    public static class ChatMessageEntry {
        UUID playerId;
        String playerName;
        String message;
        long timestamp;
        Vec3 position;
        int dimension;
    }
}