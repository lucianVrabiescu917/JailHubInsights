package ro.luci.jailhubinsights.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ro.luci.jailhubinsights.domain.Prison;

/**
 * Spring Data JPA repository for the Prison entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrisonRepository extends JpaRepository<Prison, Long>, JpaSpecificationExecutor<Prison> {}
