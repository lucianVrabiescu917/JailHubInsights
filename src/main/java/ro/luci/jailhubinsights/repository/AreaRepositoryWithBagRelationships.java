package ro.luci.jailhubinsights.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import ro.luci.jailhubinsights.domain.Area;

public interface AreaRepositoryWithBagRelationships {
    Optional<Area> fetchBagRelationships(Optional<Area> area);

    List<Area> fetchBagRelationships(List<Area> areas);

    Page<Area> fetchBagRelationships(Page<Area> areas);
}
