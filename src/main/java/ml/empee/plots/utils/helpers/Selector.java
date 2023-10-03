package ml.empee.plots.utils.helpers;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.Data;

/**
 * Class that helps with selecting a region
 */

public class Selector {

  private Cache<UUID, Selection> selections = CacheBuilder.newBuilder()
      .expireAfterWrite(15, TimeUnit.MINUTES)
      .build();

  /**
   * Holds the start and end location of the selection
   */
  @Data
  public static class Selection {
    private Location start;
    private Location end;

    /**
     * FIFO selection, max 2 points
     */
    public void select(Location location) {
      if (start == null) {
        start = location;
      } else {
        if (end != null) {
          start = end;
        }

        end = location;
      }
    }

    public boolean isValid() {
      return start != null && end != null && start.getWorld().equals(end.getWorld());
    }
  }
  
  public Optional<Selection> getSelection(UUID player) {
    return Optional.ofNullable(selections.getIfPresent(player));
  }

  public Selection select(UUID player, Location location) {
    var selection = selections.getIfPresent(player);
    if (selection == null) {
      selection = new Selection();
    }

    selection.select(location);
    selections.put(player, selection);
    return selection;
  }

}
