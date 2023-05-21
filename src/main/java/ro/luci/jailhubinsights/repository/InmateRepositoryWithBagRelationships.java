package ro.luci.jailhubinsights.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import ro.luci.jailhubinsights.domain.Inmate;

public interface InmateRepositoryWithBagRelationships {
    Optional<Inmate> fetchBagRelationships(Optional<Inmate> inmate);

    List<Inmate> fetchBagRelationships(List<Inmate> inmates);

    Page<Inmate> fetchBagRelationships(Page<Inmate> inmates);
}
