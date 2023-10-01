package ml.empee.plots.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import ml.empee.plots.config.LangConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.handlers.PlotHologramHandler;
import ml.empee.plots.handlers.PlotSelectionHandler;
import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.model.events.PlotClaimEvent;
import ml.empee.plots.model.events.PlotExpireEvent;
import ml.empee.plots.repositories.cache.PlotCache;
import ml.empee.plots.utils.helpers.Selector.Selection;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Service layer for plots
 */

@Singleton
public class PlotService {

  private final PlotCache plotCache;
  private final PlotHologramHandler hologramHandler;
  private final PlotSelectionHandler selectionHandler;

  /**
   * Create a new PlotService
   */
  public PlotService(JavaPlugin plugin, ItemRegistry itemRegistry, PlotCache plotCache, LangConfig langConfig) {
    this.plotCache = plotCache;

    this.hologramHandler = new PlotHologramHandler(plugin, langConfig, this);
    this.selectionHandler = new PlotSelectionHandler(itemRegistry);

    plotCache.findAll().forEach(plot -> hologramHandler.spawnHologram(plot.getId(), plot.getHologramLocation()));

    Bukkit.getPluginManager().registerEvents(hologramHandler, plugin);
    Bukkit.getPluginManager().registerEvents(selectionHandler, plugin);

    Bukkit.getScheduler().runTaskTimer(plugin, t -> {
      for (var plot : plotCache.findAll()) {
        if (plot.isClaimed() && plot.isExpired()) {
          Bukkit.getPluginManager().callEvent(new PlotExpireEvent(plot));
        }
      }
    }, 0, 20 * 10);
  }

  public Optional<Selection> getSelection(UUID player) {
    return selectionHandler.getSelector().getSelection(player);
  }

  public void moveHologram(Plot plot, Location location) {
    hologramHandler.moveHologram(plot.getId(), location);
    plotCache.save(plot.withHologramLocation(location));
  }

  public Optional<Plot> findByLocation(Location location) {
    return plotCache.findAll().stream()
        .filter(p -> p.getStart().getWorld().equals(location.getWorld()))
        .filter(p -> location.toVector().isInAABB(p.getStart().toVector(), p.getEnd().toVector()))
        .findFirst();
  }

  public Optional<Plot> findById(Long id) {
    return plotCache.findAll().stream()
        .filter(p -> p.getId().equals(id))
        .findFirst();
  }

  /**
   * Create a plot
   */
  public Plot create(Location hologramLocation, Location start, Location end) {
    var plot = Plot.builder()
        .start(start)
        .end(end)
        .hologramLocation(hologramLocation)
        .owner(Optional.empty())
        .secondsExpireEpoch(0L)
        .members(Collections.emptyList())
        .chests(Collections.emptyMap())
        .build();

    plot = plotCache.save(plot);
    hologramHandler.spawnHologram(plot.getId(), plot.getHologramLocation());

    return plot;
  }

  public void setExpiration(Long plotId, Long secondsExpireEpoch) {
    var plot = findById(plotId).orElseThrow();
    if (plot.getOwner().isEmpty()) {
      throw new IllegalArgumentException("Plot must be claimed");
    }

    plotCache.save(plot.withSecondsExpireEpoch(secondsExpireEpoch));
  }

  public void claim(Long plotId, UUID owner) {
    var plot = findById(plotId).orElseThrow();
    plot = plotCache.save(plot.withOwner(Optional.of(owner)));
    Bukkit.getPluginManager().callEvent(new PlotClaimEvent(plot));
  }

  public void addMember(Long plotId, UUID member) {
    var plot = findById(plotId).orElseThrow();
    var members = new ArrayList<>(plot.getMembers());
    members.add(member);

    plotCache.save(plot.withMembers(members));
  }

  public void removeMember(Long plotId, UUID member) {
    var plot = findById(plotId).orElseThrow();
    var members = new ArrayList<>(plot.getMembers());
    members.remove(member);

    plotCache.save(plot.withMembers(members));
  }

  public void addChest(Long plotId, UUID player) {
    var plot = findById(plotId).orElseThrow();
    var chests = new HashMap<>(plot.getChests());
    chests.compute(player, (k, v) -> v == null ? 1 : v + 1);

    plotCache.save(plot.withChests(chests));
  }

  public void removeChest(Long plotId, UUID player) {
    var plot = findById(plotId).orElseThrow();
    var chests = new HashMap<>(plot.getChests());
    chests.compute(player, (k, v) -> v == null ? 0 : v - 1);

    plotCache.save(plot.withChests(chests));
  }

}
