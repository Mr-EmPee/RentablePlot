package ml.empee.plots.repositories;

import static ml.empee.plots.utils.ObjectConverter.parseCollection;
import static ml.empee.plots.utils.ObjectConverter.parseLocation;
import static ml.empee.plots.utils.ObjectConverter.parseMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ml.empee.plots.config.client.DbClient;
import ml.empee.plots.model.entities.Plot;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Persistence layer for plots
 */

@Singleton
public class PlotRepository extends AbstractRepository<Plot> {

  public PlotRepository(DbClient client) {
    super(client, "plots");
  }

  @Override
  protected List<String> schema() {
    return List.of(
      "id INTEGER PRIMARY KEY",
      "start TEXT NOT NULL",
      "end TEXT NOT NULL",
      "owner TEXT",
      "members TEXT NOT NULL",
      "expireTime INTEGER NOT NULL",
      "chests TEXT NOT NULL",
      "hologramLocation TEXT NOT NULL"
    );
  }

  @Override
  protected Plot parse(ResultSet rs) throws SQLException {
    var owner = rs.getString("owner");

    return Plot.builder()
        .id(rs.getLong("id"))
        .start(parseLocation(rs.getString("start")))
        .end(parseLocation(rs.getString("end")))
        .hologramLocation(parseLocation(rs.getString("hologramLocation")))
        .owner(owner == null ? Optional.empty() : Optional.of(UUID.fromString(owner)))
        .members(parseCollection(rs.getString("members"), UUID::fromString))
        .chests(parseMap(rs.getString("chests"), UUID::fromString, Integer::parseInt))
        .expireEpoch(rs.getLong("expireTime"))
        .build();
  }

  @Override
  public void prepareStatement(PreparedStatement stm, Plot data) throws SQLException {
    stm.setLong(1, data.getId());
    stm.setString(2, parseLocation(data.getStart()));
    stm.setString(3, parseLocation(data.getEnd()));
    stm.setString(4, data.getOwner().map(UUID::toString).orElse(null));
    stm.setString(5, parseCollection(data.getMembers(), UUID::toString));
    stm.setLong(6, data.getExpireEpoch());
    stm.setString(7, parseMap(data.getChests(), UUID::toString, Object::toString));
    stm.setString(8, parseLocation(data.getHologramLocation()));
  }

}
