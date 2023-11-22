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
  String plotType = "default";

  @Builder.Default
  Long expireEpoch = 0L;

  @Builder.Default
  List<UUID> members = Collections.emptyList();

  @Builder.Default
  Map<UUID, List<Location>> containers = Collections.emptyMap();

  public List<UUID> getMembers() {
    return Collections.unmodifiableList(members);
  }

  public List<Location> getContainers(UUID player) {
    return Collections.unmodifiableList(
        containers.getOrDefault(player, Collections.emptyList())
    );
  }

  public Map<UUID, List<Location>> getContainers() {
    return Collections.unmodifiableMap(containers);
  }

  public Integer getTotalContainers() {
    return containers.values().stream().mapToInt(List::size).sum();
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
