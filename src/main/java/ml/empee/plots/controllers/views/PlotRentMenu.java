package ml.empee.plots.controllers.views;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import lombok.RequiredArgsConstructor;
import ml.empee.plots.controllers.PlotController;
import ml.empee.plots.model.entities.Plot;
import ml.empee.simplemenu.model.menus.ChestMenu;
import mr.empee.lightwire.annotations.Instance;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Menu to claim a cell
 */

@Singleton
@RequiredArgsConstructor
public class PlotRentMenu {

  @Instance
  private static PlotRentMenu instance;

  private final PlotController plotController;

  public static void open(Player player, Long plotId) {
    instance.create(player, plotId).open();
  }

  private Menu create(Player player, Long plotId) {
    return new Menu(player, plotId);
  }

  private class Menu extends ChestMenu {
    private final Long plotId;
    
    public Menu(Player player, Long plotId) {
      super(player, 3, "Put your coins :)");
      this.plotId = plotId;
    }

    @Override
    public void onOpen() {
      //Nothing
    }

    @Override
    public void onClick(InventoryClickEvent event) {
      if (event.getSlot() == 0) {
        return;
      }
      
      if (event.getClickedInventory() == null) {
        return;
      }
      
      if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
        return;
      }

      event.setCancelled(true);
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
      event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
      var coins = event.getInventory().getItem(0);
      if (coins == null) {
        return;
      }

      plotController.addRent(player, plotId, coins);
    }
  }

}
