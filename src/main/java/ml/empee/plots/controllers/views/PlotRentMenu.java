package ml.empee.plots.controllers.views;

import com.cryptomorin.xseries.XMaterial;
import lombok.RequiredArgsConstructor;
import ml.empee.itembuilder.ItemBuilder;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.controllers.PlotAPI;
import ml.empee.plots.utils.Logger;
import ml.empee.simplemenu.model.GItem;
import ml.empee.simplemenu.model.menus.DispenserMenu;
import mr.empee.lightwire.annotations.Instance;
import mr.empee.lightwire.annotations.Singleton;
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

  private final PlotAPI plotCommand;

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
      if (event.getSlot() == 4) {
        return;
      }

      if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
        return;
      }

      event.setCancelled(true);
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
      event.setCancelled(true);
    }

    @Override
    public void onClose() {
      var coins = getContent(0, 0).orElse(null);
      if (coins == null) {
        return;
      }

      var secondsBought = plotCommand.convertCoinsToSeconds(coins);
      if (plotCommand.isPlotClaimed(plotId)) {
        plotCommand.addRent(plotId, secondsBought);
        Logger.log(player, langConfig.translate("plot.rent.add", TimeUnit.SECONDS.toHours(secondsBought)));
      } else {
        plotCommand.claimPlot(plotId, player.getUniqueId());
        plotCommand.addRent(plotId, secondsBought);
        Logger.log(player, langConfig.translate("plot.claimed"));
      }

    }
  }

}
