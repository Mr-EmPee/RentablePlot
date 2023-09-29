package ml.empee.plots.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import lombok.RequiredArgsConstructor;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.services.PlotService;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Handler for plot selection
 */

@Singleton
@RequiredArgsConstructor
public class PlotSelectionHandler implements Listener {

  private final PlotService plotService;

  @EventHandler(ignoreCancelled = true)
  public void onBlockClick(PlayerInteractEvent event) {
    if (event.getHand() == EquipmentSlot.OFF_HAND) {
      return;
    } else if (event.getItem() == null) {
      return;
    } else if (event.getClickedBlock() == null) {
      return;
    } else if (event.getAction().name().contains("LEFT")) {
      return;
    } else if (!event.getPlayer().hasPermission(Permissions.ADMIN)) {
      return;
    } else if (!ItemRegistry.PLOT_SLECTOR.isPluginItem(event.getItem())) {
      return;
    }

    plotService.getPlotSelector().select(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
  }
  
}
