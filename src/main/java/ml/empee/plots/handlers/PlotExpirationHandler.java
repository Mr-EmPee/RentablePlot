package ml.empee.plots.handlers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ml.empee.plots.config.LangConfig;
import ml.empee.plots.services.PlotService;
import ml.empee.plots.utils.Logger;

/**
 * Periodically check and unclaim expired plots
 */

public class PlotExpirationHandler {

  private final PlotService plotService;
  private final LangConfig langConfig;

  public PlotExpirationHandler(JavaPlugin plugin, LangConfig langConfig, PlotService plotService) {
    this.plotService = plotService;
    this.langConfig = langConfig;
    
    Bukkit.getScheduler().runTaskTimer(plugin, this::unclaimExpiredPlots, 0, 20 * 10);
  }

  private void unclaimExpiredPlots() {
    for (var plot : plotService.findAll()) {
      if (!plot.isClaimed() || !plot.isExpired()) {
        continue;
      }

      var owner = Bukkit.getOfflinePlayer(plot.getOwner().orElseThrow());
      if (owner.isOnline()) {
        Logger.log(owner.getPlayer(), langConfig.translate("plot.expired"));
      }

      plotService.unclaim(plot.getId());
    }
  }

}
