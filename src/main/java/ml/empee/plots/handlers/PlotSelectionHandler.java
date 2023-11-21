package ml.empee.plots.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.utils.Logger;
import ml.empee.plots.utils.helpers.Selector;

/**
 * Handler for plot selection
 */

@RequiredArgsConstructor
public class PlotSelectionHandler implements Listener {

  @Getter
  private final Selector selector = new Selector();
  private final ItemRegistry itemRegistry;

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
    } else if (!itemRegistry.plotSelector().isPluginItem(event.getItem())) {
      return;
    }

    var loc = event.getClickedBlock().getLocation();
    Logger.log(
        event.getPlayer(), "&aCorner X: %d, Y: %d Z: %d selected",
        loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()
    );

    selector.select(event.getPlayer().getUniqueId(), loc);
  }
  
}
