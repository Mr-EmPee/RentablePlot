package ml.empee.plots.services;

import ml.empee.plots.config.LangConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.handlers.PlotExpirationHandler;
import ml.empee.plots.handlers.PlotHologramHandler;
import ml.empee.plots.handlers.PlotSelectionHandler;
import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.repositories.memory.PlotMemoryCache;
import ml.empee.plots.utils.helpers.Selector.Selection;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for plots
 */

@Singleton
public class PlotService {

  private final PlotMemoryCache plotCache;
  private final PlotHologramHandler hologramHandler;
  private final PlotSelectionHandler selectionHandler;
  private final PlotExpirationHandler expirationHandler;

  /**
   * Create a new PlotService
   */
  public PlotService(JavaPlugin plugin, ItemRegistry itemRegistry, PlotMemoryCache plotCache, LangConfig langConfig) {
    this.plotCache = plotCache;

    this.hologramHandler = new PlotHologramHandler(plugin, langConfig, this);
    this.expirationHandler = new PlotExpirationHandler(plugin, langConfig, this);
    this.selectionHandler = new PlotSelectionHandler(itemRegistry);

    plotCache.findAll().forEach(plot -> hologramHandler.spawnHologram(plot.getId(), plot.getHologramLocation()));

    Bukkit.getPluginManager().registerEvents(hologramHandler, plugin);
    Bukkit.getPluginManager().registerEvents(selectionHandler, plugin);
  }

  public Collection<Plot> findAll() {
    return plotCache.findAll();
  }

  public Optional<Selection> getSelection(UUID player) {
    return selectionHandler.getSelector().getSelection(player);
  }

  public Plot moveHologram(Long plotId, Location location) {
    var plot = findById(plotId).orElseThrow();
    hologramHandler.moveHologram(plotId, location);
    return plotCache.save(plot.withHologramLocation(location));
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

  public Plot create(Location hologramLocation, Location start, Location end) {
    var plot = plotCache.save(
        Plot.builder()
            .start(start)
            .end(end)
            .hologramLocation(hologramLocation)
            .build()
    );

    hologramHandler.spawnHologram(plot.getId(), plot.getHologramLocation());
    return plot;
  }

  public Plot setExpireEpoch(Long plotId, Long expireEpoch) {
    var plot = findById(plotId).orElseThrow();
    return plotCache.save(
        plot.withExpireEpoch(expireEpoch)
    );
  }

  public Plot claim(Long plotId, UUID owner) {
    var plot = findById(plotId).orElseThrow();
    return plotCache.save(
        plot.withOwner(owner)
    );
  }

  public Plot unclaim(Long plotId) {
    var plot = findById(plotId).orElseThrow();

    return plotCache.save(
        plot.withOwner(null)
            .withExpireEpoch(0L)
            .withMembers(Collections.emptyList())
            .withChests(Collections.emptyMap())
    );
  }

  public Plot addMember(Long plotId, UUID member) {
    var plot = findById(plotId).orElseThrow();
    var members = new ArrayList<>(plot.getMembers());
    members.add(member);

    return plotCache.save(plot.withMembers(members));
  }

  public Plot removeMember(Long plotId, UUID member) {
    var plot = findById(plotId).orElseThrow();
    var members = new ArrayList<>(plot.getMembers());
    members.remove(member);

    return plotCache.save(plot.withMembers(members));
  }

  public Plot addChest(Long plotId, UUID player) {
    var plot = findById(plotId).orElseThrow();
    var chests = new HashMap<>(plot.getChests());
    chests.compute(player, (k, v) -> v == null ? 1 : v + 1);

    return plotCache.save(plot.withChests(chests));
  }

  public Plot removeChest(Long plotId, UUID player) {
    var plot = findById(plotId).orElseThrow();
    var chests = new HashMap<>(plot.getChests());
    chests.compute(player, (k, v) -> v == null ? 0 : v - 1);

    return plotCache.save(plot.withChests(chests));
  }

}
