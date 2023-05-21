package ro.luci.jailhubinsights.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ro.luci.jailhubinsights.domain.Inmate;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class InmateRepositoryWithBagRelationshipsImpl implements InmateRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Inmate> fetchBagRelationships(Optional<Inmate> inmate) {
        return inmate.map(this::fetchActivities);
    }

    @Override
    public Page<Inmate> fetchBagRelationships(Page<Inmate> inmates) {
        return new PageImpl<>(fetchBagRelationships(inmates.getContent()), inmates.getPageable(), inmates.getTotalElements());
    }

    @Override
    public List<Inmate> fetchBagRelationships(List<Inmate> inmates) {
        return Optional.of(inmates).map(this::fetchActivities).orElse(Collections.emptyList());
    }

    Inmate fetchActivities(Inmate result) {
        return entityManager
            .createQuery("select inmate from Inmate inmate left join fetch inmate.activities where inmate is :inmate", Inmate.class)
            .setParameter("inmate", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Inmate> fetchActivities(List<Inmate> inmates) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, inmates.size()).forEach(index -> order.put(inmates.get(index).getId(), index));
        List<Inmate> result = entityManager
            .createQuery(
                "select distinct inmate from Inmate inmate left join fetch inmate.activities where inmate in :inmates",
                Inmate.class
            )
            .setParameter("inmates", inmates)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
