package ml.empee.plots.controllers.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.CommandParseException;
import cloud.commandframework.exceptions.parsing.ParserException;
import lombok.RequiredArgsConstructor;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.controllers.PlotController;
import ml.empee.plots.model.entities.PlotType;
import ml.empee.plots.services.PlotService;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Controller for plots
 */

@Singleton
@RequiredArgsConstructor
public class PlotCommand implements Command {

  private final PlotController plotController;
  private final ItemRegistry itemRegistry;
  private final PlotService plotService;
  private final LangConfig langConfig;

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "selector")
  public void giveSelector(Player sender) {
    sender.getInventory().addItem(itemRegistry.plotSelector().build());
    Logger.log(sender, "&aPlot selector given");
  }

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "create <type>")
  public void createPlot(Player sender, @Argument(defaultValue = "default") PlotType type) {
    var selection = plotService.getSelection(sender.getUniqueId()).orElse(null);
    if (selection == null || !selection.isValid()) {
      Logger.log(sender, "&cYou need to select a region first");
      return;
    }

    plotService.create(sender.getLocation(), selection.getStart(), selection.getEnd(), type);
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

    plotService.moveHologram(plot.getId(), new Location(sender.getWorld(), x, y, z));
    Logger.log(sender, "&aHologram moved");
  }

  @CommandMethod(COMMAND_PREFIX + "members add <target>")
  public void addMember(Player sender, @Argument OfflinePlayer target) {
    var plot = plotService.findByLocation(sender.getLocation()).orElse(null);
    if (plot == null) {
      Logger.log(sender, langConfig.translate("plot.not-inside"));
      return;
    }

    plotController.addMember(sender, plot.getId(), target);
  }

  @CommandMethod(COMMAND_PREFIX + "members remove <target>")
  public void removeMember(Player sender, @Argument OfflinePlayer target) {
    var plot = plotService.findByLocation(sender.getLocation()).orElse(null);
    if (plot == null) {
      Logger.log(sender, langConfig.translate("plot.not-inside"));
      return;
    }

    plotController.removeMember(sender, plot.getId(), target);
  }

  @Parser(suggestions = "plotType")
  public PlotType parsePlotType(CommandContext<CommandSender> sender, Queue<String> input) {
    return plotService.findPlotType(input.poll()).orElseThrow(
        () -> new IllegalArgumentException("Unable to find plot type")
    );
  }

  @Suggestions("plotType")
  public List<String> getPlotTypeSuggestion(CommandContext<CommandSender> sender, String input) {
    return plotService.findAllPlotTypes().stream()
        .map(PlotType::getId)
        .filter(p -> p.toLowerCase().startsWith(input.toLowerCase()))
        .collect(Collectors.toList());
  }
}
