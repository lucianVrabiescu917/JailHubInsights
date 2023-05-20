package ro.luci.jailhubinsights.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.luci.jailhubinsights.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
