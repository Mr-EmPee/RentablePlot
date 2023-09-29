package ml.empee.plots.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.SneakyThrows;
import ml.empee.plots.config.client.DbClient;
import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.utils.ObjectConverter;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Persistence layer for plots
 */

@Singleton
public class PlotRepository {

  private final DbClient client;

  public PlotRepository(DbClient client) {
    this.client = client;
    createTable();
  }

  @SneakyThrows
  private void createTable() {
    var query = "";
    query += "CREATE TABLE IF NOT EXISTS plots (";
    query += "  id INTEGER PRIMARY KEY,";
    query += "  start TEXT NOT NULL,";
    query += "  end TEXT NOT NULL,";
    query += "  owner TEXT,";
    query += "  members TEXT,";
    query += "  expireTime INTEGER,";
    query += "  chests TEXT";
    query += ");";

    try (var stm = client.getJdbcConnection().createStatement()) {
      stm.executeUpdate(query);
    }
  }

  /**
   * Find all plots
   */
  public CompletableFuture<List<Plot>> findAll() {
    return CompletableFuture.supplyAsync(() -> {
      var query = "SELECT * FROM plots;";
      try (var stm = client.getJdbcConnection().createStatement()) {
        var rs = stm.executeQuery(query);
        var plots = new ArrayList<Plot>();
        while (rs.next()) {
          plots.add(parseResult(rs));
        }

        return plots;
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }, client.getThreadPool());
  }

  /**
   * Create or update a world-sate
   */
  public CompletableFuture<Void> save(Plot data) {
    return CompletableFuture.runAsync(() -> {
      var query = "";
      query += "INSERT OR REPLACE INTO plots (";
      query += "  id, start, end, owner, members, expireTime, chests";
      query += ") VALUES (?, ?, ?, ?, ?, ?, ?);";

      try (var stm = client.getJdbcConnection().prepareStatement(query)) {
        stm.setLong(1, data.getId());
        stm.setString(2, ObjectConverter.parseLocation(data.getStart()));
        stm.setString(3, ObjectConverter.parseLocation(data.getEnd()));
        stm.setString(4, data.getOwner() == null ? null : data.getOwner().toString());
        stm.setString(5, data.getMembers() == null ? null : ObjectConverter.parseCollection(data.getMembers(), UUID::toString));
        stm.setLong(6, data.getExpireTime());
        stm.setString(7, data.getChests() == null ? null : ObjectConverter.parseMap(data.getChests(), UUID::toString, Object::toString));
        stm.executeUpdate();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }, client.getThreadPool());
  }

  @SneakyThrows
  private Plot parseResult(ResultSet rs) {
    var id = rs.getLong("id");
    var start = rs.getString("start");
    var end = rs.getString("end");
    var owner = rs.getString("owner");
    var members = rs.getString("members");
    var expireTime = rs.getLong("expireTime");
    var chests = rs.getString("chests");

    return Plot.builder()
        .id(id)
        .start(ObjectConverter.parseLocation(start))
        .end(ObjectConverter.parseLocation(end))
        .owner(owner == null ? null : UUID.fromString(owner))
        .members(members == null ? null : ObjectConverter.parseCollection(members, UUID::fromString))
        .chests(chests == null ? null : ObjectConverter.parseMap(chests, UUID::fromString, Integer::parseInt))
        .expireTime(expireTime)
        .build();
  }

}
