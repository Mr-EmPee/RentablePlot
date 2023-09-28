package ml.empee.templateplugin.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import lombok.SneakyThrows;
import ml.empee.templateplugin.config.client.DbClient;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Repository for the demo table
 */

@Singleton
public class DemoRepository {

  private final DbClient client;

  public DemoRepository(DbClient client) {
    this.client = client;
    createTable();
  }

  @SneakyThrows
  private void createTable() {
    var query = "";
    query += "CREATE TABLE IF NOT EXISTS demo (";
    query += "  id STRING PRIMARY KEY,";
    query += ");";
    
    try (var stm = client.getJdbcConnection().createStatement()) {
      stm.executeUpdate(query);
    }
  }

  /**
   * Create or update a world-sate
   */
  public CompletableFuture<Void> save(String data) {
    return CompletableFuture.runAsync(() -> {
      var query = "INSERT OR REPLACE INTO demo (id) VALUES (?);";
      try (var stm = client.getJdbcConnection().prepareStatement(query)) {
        stm.setString(1, data);
        stm.executeUpdate();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }, client.getThreadPool());
  }

  @SneakyThrows
  private String parseResult(ResultSet rs) {
    return rs.getString("id");
  }

}
