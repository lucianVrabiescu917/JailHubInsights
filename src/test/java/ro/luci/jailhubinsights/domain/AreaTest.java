package ro.luci.jailhubinsights.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ro.luci.jailhubinsights.web.rest.TestUtil;

class AreaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Area.class);
        Area area1 = new Area();
        area1.setId(1L);
        Area area2 = new Area();
        area2.setId(area1.getId());
        assertThat(area1).isEqualTo(area2);
        area2.setId(2L);
        assertThat(area1).isNotEqualTo(area2);
        area1.setId(null);
        assertThat(area1).isNotEqualTo(area2);
    }
}
