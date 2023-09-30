package ml.empee.plots.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;

import lombok.RequiredArgsConstructor;
import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.repositories.cache.PlotCache;
import ml.empee.plots.utils.helpers.Selector;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Service layer for plots
 */

@Singleton
@RequiredArgsConstructor
public class PlotService {

  private final PlotCache plotCache;
  private final Selector selector = new Selector();

  public Selector getPlotSelector() {
    return selector;
  }

  public Plot createPlot(Location start, Location end) {
    var plot = Plot.builder()
        .start(start)
        .end(end)
        .hologramLocation(hologram)
        .owner(Optional.empty())
        .expireTime(0L)
        .members(Collections.emptyList())
        .chests(Collections.emptyMap())
        .build();

    return plotCache.save(plot);
  }

  public void claimPlot(Plot plot, UUID owner, Long expireTime) {
    plotCache.save(
        plot.withOwner(Optional.of(owner))
            .withExpireTime(expireTime)
    );
  }

  public void addMember(Plot plot, UUID member) {
    var members = new ArrayList<>(plot.getMembers());
    members.add(member);

    plotCache.save(plot.withMembers(members));
  }

  public void removeMember(Plot plot, UUID member) {
    var members = new ArrayList<>(plot.getMembers());
    members.remove(member);

    plotCache.save(plot.withMembers(members));
  }

  public void addChest(Plot plot, UUID player) {
    var chests = new HashMap<>(plot.getChests());
    chests.compute(player, (k, v) -> v == null ? 1 : v + 1);

    plotCache.save(plot.withChests(chests));
  }

  public void removeChest(Plot plot, UUID player) {
    var chests = new HashMap<>(plot.getChests());
    chests.compute(player, (k, v) -> v == null ? 0 : v - 1);

    plotCache.save(plot.withChests(chests));
  }

}
