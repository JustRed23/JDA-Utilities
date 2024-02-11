package dev.JustRed23.jdautils.data;

import dev.JustRed23.jdautils.JDAUtilities;
import org.jetbrains.annotations.CheckReturnValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum DataStore {

    GUILD("guild_%s_settings"),
    USER("user_%s_settings"),
    CUSTOM("custom_%s_settings");

    private final String tableName;

    DataStore(String unformattedTableName) {
        this.tableName = unformattedTableName;
    }

    public void createTable(long tableIdentifier) {
        String table = tableName.formatted(tableIdentifier);
        try (
                var connection = JDAUtilities.getDatabaseConnection();
                PreparedStatement stmt = connection.prepareStatement(Database.TABLE_CREATION_STRING.formatted(table))
        ) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Could not create table '" + table + "'", e);
        }
    }

    @CheckReturnValue
    public InteractionResult get(long tableIdentifier, String setting) {
        try (
                var connection = JDAUtilities.getDatabaseConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + tableName.formatted(tableIdentifier) + " WHERE setting = ?")
        ) {
            stmt.setString(1, setting);

            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next() ? InteractionResult.SUCCESS.setValue(resultSet.getString("value")) : InteractionResult.NOT_FOUND;
            }
        } catch (SQLException e) {
            return InteractionResult.ERROR.setError(e);
        }
    }

    @CheckReturnValue
    public InteractionResult insert(long tableIdentifier, String setting, String value) {
        try (
                var connection = JDAUtilities.getDatabaseConnection();
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + tableName.formatted(tableIdentifier) + " (setting, value) VALUES (?, ?)")
        ) {
            stmt.setString(1, setting);
            stmt.setString(2, value);

            return stmt.executeUpdate() > 0 ? InteractionResult.SUCCESS : InteractionResult.NO_CHANGE;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) return InteractionResult.DUPLICATE;
            return InteractionResult.ERROR.setError(e);
        }
    }

    @CheckReturnValue
    public InteractionResult update(long tableIdentifier, String setting, String newValue) {
        return null;
    }

    @CheckReturnValue
    public InteractionResult delete(long tableIdentifier, String setting) {
        return null;
    }

    public InteractionResult insertOrUpdate(long tableIdentifier, String setting, String value) {
        if (has(tableIdentifier, setting))
            return update(tableIdentifier, setting, value);
        return insert(tableIdentifier, setting, value);
    }

    public boolean has(long tableIdentifier, String setting) {
        return get(tableIdentifier, setting) != InteractionResult.NOT_FOUND;
    }
}
