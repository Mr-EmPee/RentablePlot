package ml.empee.plots.controllers.views;

import com.cryptomorin.xseries.XMaterial;
import lombok.RequiredArgsConstructor;
import ml.empee.itembuilder.ItemBuilder;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.controllers.PlotController;
import ml.empee.simplemenu.model.GItem;
import ml.empee.simplemenu.model.menus.DispenserMenu;
import mr.empee.lightwire.annotations.Instance;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.concurrent.TimeUnit;

/**
 * Menu to claim a cell
 */

@Singleton
@RequiredArgsConstructor
public class PlotRentMenu {

  @Instance
  private static PlotRentMenu instance;

  private final PlotController plotAPI;

  private final LangConfig langConfig;

  public static void open(Player player, Long plotId) {
    instance.create(player, plotId).open();
  }

  private Menu create(Player player, Long plotId) {
    return new Menu(player, plotId);
  }

  private class Menu extends DispenserMenu {
    private final Long plotId;

    public Menu(Player player, Long plotId) {
      super(player);

      this.plotId = plotId;
    }

    @Override
    public String title() {
      return " ";
    }

    @Override
    public void onOpen() {
      top().fill(background());
      top().setItem(1, 1, GItem.empty());
    }

    private GItem background() {
      return GItem.of(
          ItemBuilder.from(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem())
              .setName(" ")
              .build()
      );
    }

    @Override
    public void onClick(InventoryClickEvent event) {
      if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
        return;
      }

      boolean isPlayerInventory = event.getClickedInventory().getType() == InventoryType.PLAYER;

      if (!isPlayerInventory) {
        if (event.getSlot() == 4) {
          return;
        }
      } else {
        boolean isAir = event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR;
        boolean isCoin = plotAPI.isPlotCoin(event.getCurrentItem());
        if (isCoin || isAir) {
          return;
        }
      }

      event.setCancelled(true);
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
      event.setCancelled(true);
    }

    @Override
    public void onClose() {
      var coins = getContent(1, 1).orElse(null);
      if (coins == null) {
        return;
      }

      var plot = plotAPI.getPlot(plotId).orElseThrow();
      var secondsBought = plotAPI.convertCoinsToSeconds(coins);
      var expireEpoch = plot.getExpireEpoch() + TimeUnit.SECONDS.toMillis(secondsBought);
      if (plot.isClaimed()) {
        plotAPI.setPlotExpireEpoch(player, plotId, expireEpoch);
      } else {
        plotAPI.claimPlot(player, plotId, expireEpoch);
      }
    }
  }

}
