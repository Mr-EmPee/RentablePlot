package ml.empee.plots.model.entities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * Immutable Plot entity
 */

@Value
@With
@Builder
public class Plot {

  Long id;

  Location start;
  Location end;

  UUID owner;
  List<UUID> members;

  Long expireTime;
  Map<UUID, Integer> chests;

  public List<UUID> getMembers() {
    return Collections.unmodifiableList(members);
  }

  public Map<UUID, Integer> getChests() {
    return Collections.unmodifiableMap(chests);
  }

  public Location getStart() {
    return start.clone();
  }

  public Location getEnd() {
    return end.clone();
  }

}
