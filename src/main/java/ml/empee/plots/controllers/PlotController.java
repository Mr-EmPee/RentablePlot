package ml.empee.plots.controllers;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import lombok.RequiredArgsConstructor;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.config.PluginConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.services.PlotService;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Controller for plots
 */

@Singleton
@RequiredArgsConstructor
public class PlotController implements Controller {

  private final ItemRegistry itemRegistry;
  private final PluginConfig pluginConfig;
  private final PlotService plotService;
  private final LangConfig langConfig;

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "selector")
  public void giveSelector(Player sender) {
    sender.getInventory().addItem(itemRegistry.plotSelector().build());
    Logger.log(sender, "&aPlot selector given");
  }

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "create")
  public void createPlot(Player sender) {
    var selection = plotService.getSelection(sender.getUniqueId()).orElse(null);
    if (selection == null || !selection.isValid()) {
      Logger.log(sender, "&cYou need to select a region first");
      return;
    }

    plotService.create(sender.getLocation(), selection.getStart(), selection.getEnd());
    Logger.log(sender, "&aPlot created");
  }

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "move-hologram <x> <y> <z>")
  public void moveHologramPlot(Player sender, @Argument Double x, @Argument Double y, @Argument Double z) {
    var plot = plotService.findByLocation(sender.getLocation()).orElse(null);
    if (plot == null) {
      Logger.log(sender, "&cYou need to be in a plot");
      return;
    }

    plotService.moveHologram(plot, new Location(sender.getWorld(), x, y, z));
    Logger.log(sender, "&aHologram moved");
  }

  /**
   * Add rent to a plot
   */
  public void addRent(Player source, Long plotId, ItemStack coins) {
    if (!itemRegistry.plotCoin().isPluginItem(coins)) {
      throw new IllegalArgumentException("The item is not a plot coin");
    }

    var secondsBought = coins.getAmount() * pluginConfig.getCoinValue();
    var plot = plotService.findById(plotId).orElseThrow();
    if (plot.isClaimed()) {
      if (!plot.isMember(source.getUniqueId())) {
        throw new IllegalArgumentException("Only plot members can add rent");
      }

      plotService.setExpiration(plotId, plot.getSecondsExpireEpoch() + secondsBought);
    } else {
      plotService.claim(plotId, source.getUniqueId());
      plotService.setExpiration(plotId, Instant.now().getEpochSecond() + secondsBought);

      Logger.log(source, langConfig.translate("plot.claimed"));
    }

    Logger.log(source, langConfig.translate("plot.rent.add", TimeUnit.SECONDS.toHours(secondsBought)));
  }

}
