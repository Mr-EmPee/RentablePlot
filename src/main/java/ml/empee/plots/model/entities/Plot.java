package ml.empee.plots.model.entities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;

import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * Immutable Plot entity
 */

@With
@Value
@Builder
public class Plot implements Entity {

  Long id;

  Location start;
  Location end;
  Location hologramLocation;

  UUID owner;

  @Builder.Default
  Long expireEpoch = 0L;

  @Builder.Default
  List<UUID> members = Collections.emptyList();

  @Builder.Default
  Map<UUID, Integer> chests = Collections.emptyMap();

  public List<UUID> getMembers() {
    return Collections.unmodifiableList(members);
  }

  public Map<UUID, Integer> getChests() {
    return Collections.unmodifiableMap(chests);
  }

  public boolean isMember(UUID player) {
    return player.equals(owner) || members.contains(player);
  }

  public boolean isClaimed() {
    return owner != null;
  }

  public boolean isExpired() {
    return expireEpoch <= System.currentTimeMillis();
  }

  public Location getStart() {
    return start.clone();
  }

  public Location getEnd() {
    return end.clone();
  }

}
