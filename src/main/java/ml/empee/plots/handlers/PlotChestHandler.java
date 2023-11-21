package ml.empee.plots.handlers;

import com.cryptomorin.xseries.XBlock;
import lombok.RequiredArgsConstructor;
import ml.empee.plots.controllers.PlotController;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@Singleton
@RequiredArgsConstructor
public class PlotChestHandler implements Listener {

  private final PlotController plotController;

  @EventHandler(ignoreCancelled = true)
  public void onPlace(BlockPlaceEvent event) {
    if (!XBlock.isContainer(event.getBlock())) {
      return;
    }

    var plot = plotController.findByLocation(event.getBlock().getLocation());
    if (plot.isEmpty()) {
      return;
    }

    var result = plotController.addContainer(
        event.getPlayer(), plot.get().getId(), event.getBlock().getLocation()
    );

    if (result == null) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onBreak(BlockBreakEvent event) {
    if (!XBlock.isContainer(event.getBlock())) {
      return;
    }

    var plot = plotController.findByLocation(event.getBlock().getLocation());
    if (plot.isEmpty()) {
      return;
    }

    plotController.removeContainer(event.getPlayer(), plot.get().getId(), event.getBlock().getLocation());
  }

}
