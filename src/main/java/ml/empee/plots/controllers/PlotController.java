package ml.empee.plots.controllers;

import org.bukkit.entity.Player;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import lombok.RequiredArgsConstructor;
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

  private final PlotService plotService;

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "selector")
  public void giveSelector(Player sender) {
    sender.getInventory().addItem(ItemRegistry.PLOT_SLECTOR.build());
    Logger.log(sender, "&aPlot selector given");
  }

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "create")
  public void createPlot(Player sender) {
    var selection = plotService.getPlotSelector().getSelection(sender.getUniqueId()).orElse(null);
    if (selection == null || !selection.isValid()) {
      Logger.log(sender, "&cYou need to select a region first");
      return;
    }

    plotService.createPlot(selection.getStart(), selection.getEnd());
    Logger.log(sender, "&aPlot created");
  }

}
