package ml.empee.plots.controllers;

import lombok.RequiredArgsConstructor;
import ml.empee.plots.config.PluginConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.services.PlotService;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
@RequiredArgsConstructor
public class PlotAPI {

  private final ItemRegistry itemRegistry;
  private final PlotService plotService;
  private final PluginConfig pluginConfig;

  public Integer convertCoinsToSeconds(ItemStack coins) {
    if (!itemRegistry.plotCoin().isPluginItem(coins)) {
      throw new IllegalArgumentException("The item is not a plot coin");
    }

    return coins.getAmount() * pluginConfig.getCoinValue();
  }

  /**
   * Add rent to a plot
   */
  public Plot addRent(Long plotId, int seconds) {
    var plot = plotService.findById(plotId).orElseThrow();
    if (!plot.isClaimed()) {
      throw new IllegalArgumentException("Plot " + plotId + " is not claimed");
    }

    return plotService.setExpiration(plotId, plot.getSecondsExpireEpoch() + seconds);
  }

  public Plot claimPlot(Long plotId, UUID owner) {
    if (isPlotClaimed(plotId)) {
      throw new IllegalArgumentException("Plot " + plotId + " is already claimed");
    }

    return plotService.claim(plotId, owner);
  }

  public boolean isPlotClaimed(Long plotId) {
    return plotService.findById(plotId).orElseThrow().isClaimed();
  }

}
