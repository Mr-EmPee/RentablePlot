package ml.empee.plots.handlers;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.event.HologramClickEvent;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.controllers.views.PlotRentMenu;
import ml.empee.plots.services.PlotService;
import ml.empee.plots.utils.Logger;

/**
 * Hologram manager for plots
 */

public class PlotHologramHandler implements Listener {

  private final BiMap<Long, Hologram> holograms = HashBiMap.create(new HashMap<>());

  private final JavaPlugin plugin;
  private final LangConfig langConfig;
  private final PlotService plotService;

  public PlotHologramHandler(JavaPlugin plugin, LangConfig langConfig, PlotService plotService) {
    this.plugin = plugin;
    this.langConfig = langConfig;
    this.plotService = plotService;

    Bukkit.getScheduler().runTaskTimer(plugin, t -> {
      refreshHolograms();
    }, 0, 10);
  }

  @EventHandler
  public void onClickAsync(HologramClickEvent event) {
    Bukkit.getScheduler().runTask(plugin, () -> onClickSync(event));
  }

  public void onClickSync(HologramClickEvent event) {
    var plotId = holograms.inverse().get(event.getHologram());
    var plot = plotService.findById(plotId).orElseThrow();
    var player = event.getPlayer();
    if (plot.isClaimed() && !plot.isMember(player.getUniqueId())) {
      Logger.log(player, langConfig.translate("plot.not-member"));
      return;
    }
    
    PlotRentMenu.open(event.getPlayer(), plotId);
  }

  public void spawnHologram(Long plotId, Location location) {
    var hologram = DHAPI.createHologram(UUID.randomUUID().toString(), location, false);
    hologram.setUpdateInterval(0);
    holograms.put(plotId, hologram);
  }

  public void moveHologram(Long plotId, Location location) {
    var hologram = holograms.get(plotId);
    DHAPI.moveHologram(hologram, location);
  }

  public void destroyHologram(Long plotId) {
    var hologram = holograms.get(plotId);

    DHAPI.removeHologram(hologram.getId());
    holograms.remove(plotId);
  }

  private void refreshHolograms() {
    holograms.forEach((plotId, hologram) -> {
      var plot = plotService.findById(plotId).orElseThrow();
      if (plot.isClaimed()) {
        var expireTime = Duration.between(Instant.now(), Instant.ofEpochSecond(plot.getSecondsExpireEpoch()));
        if (plot.isExpired()) {
          expireTime = Duration.ZERO;
        }
        
        var placeholders = new Object[] {
            Bukkit.getOfflinePlayer(plot.getOwner().get()).getName(),
            1, 5,
            expireTime.toDaysPart(), expireTime.toHoursPart(), expireTime.toMinutesPart()
        };

        DHAPI.setHologramLines(hologram, langConfig.translateBlock("hologram.claimed", placeholders));
      } else {
        var placeholders = new Object[] {
            5
        };

        DHAPI.setHologramLines(hologram, langConfig.translateBlock("hologram.unclaimed", placeholders));
      }
    });
  }

}
