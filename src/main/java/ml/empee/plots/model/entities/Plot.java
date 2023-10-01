package ml.empee.plots.model.entities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

  Location hologramLocation;

  Optional<UUID> owner;
  Long secondsExpireEpoch;

  List<UUID> members;
  Map<UUID, Integer> chests;

  public List<UUID> getMembers() {
    return Collections.unmodifiableList(members);
  }

  public Map<UUID, Integer> getChests() {
    return Collections.unmodifiableMap(chests);
  }

  public boolean isMember(UUID player) {
    return owner.map(o -> o.equals(player)).orElse(false) || members.contains(player);
  }

  public boolean isClaimed() {
    return owner.isPresent();
  }

  public boolean isExpired() {
    return TimeUnit.SECONDS.toMillis(secondsExpireEpoch) <= System.currentTimeMillis();
  }

  public Location getStart() {
    return start.clone();
  }

  public Location getEnd() {
    return end.clone();
  }

}
