package ml.empee.plots.handlers;

import lombok.RequiredArgsConstructor;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.controllers.PlotController;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@Singleton
@RequiredArgsConstructor
public class PlotProtectionHandler implements Listener {

  private final PlotController plotController;

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onInteract(PlayerInteractEvent event) {
    var block = event.getClickedBlock();
    if (block == null) {
      return;
    }

    if (canModify(event.getPlayer(), block.getLocation())) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onBreak(BlockPlaceEvent event) {
    if (canModify(event.getPlayer(), event.getBlock().getLocation())) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlace(BlockBreakEvent event) {
    if (canModify(event.getPlayer(), event.getBlock().getLocation())) {
      return;
    }

    event.setCancelled(true);
  }

  public boolean canModify(Player sender, Location location) {
    if (sender.hasPermission(Permissions.ADMIN)) {
      return true;
    }

    var plot = plotController.findByLocation(location);
    if (plot.isEmpty()) {
      return true;
    }

    return plotController.canBuild(sender, plot.get().getId());
  }

}
