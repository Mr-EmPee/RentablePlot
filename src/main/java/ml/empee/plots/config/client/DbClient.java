package ml.empee.plots.config.client;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.SneakyThrows;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Database client
 */

@Singleton
public class DbClient {

  @Getter
  private final ExecutorService threadPool = Executors.newFixedThreadPool(1);
  private final String dbUrl;

  private Connection jdbcConnection;

  public DbClient(JavaPlugin plugin) {
    File dbFile = new File(plugin.getDataFolder(), "db.sqlite");
    this.dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

    dbFile.getParentFile().mkdirs();
  }

  @SneakyThrows
  public Connection getJdbcConnection() {
    if (jdbcConnection == null || jdbcConnection.isClosed()) {
      jdbcConnection = DriverManager.getConnection(dbUrl);
      jdbcConnection.setAutoCommit(true);
    }

    return jdbcConnection;
  }

  @SneakyThrows
  public void closeConnections() {
    Logger.info("Shutting down db connections (Forced stop in 60seconds)");

    threadPool.shutdown();
    threadPool.awaitTermination(60, TimeUnit.SECONDS);

    jdbcConnection.close();
  }

}
