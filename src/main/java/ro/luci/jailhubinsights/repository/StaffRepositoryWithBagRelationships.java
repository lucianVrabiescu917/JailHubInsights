package ro.luci.jailhubinsights.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import ro.luci.jailhubinsights.domain.Staff;

public interface StaffRepositoryWithBagRelationships {
    Optional<Staff> fetchBagRelationships(Optional<Staff> staff);

    List<Staff> fetchBagRelationships(List<Staff> staff);

    Page<Staff> fetchBagRelationships(Page<Staff> staff);
}
