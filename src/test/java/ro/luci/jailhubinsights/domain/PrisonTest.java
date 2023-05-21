package ro.luci.jailhubinsights.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ro.luci.jailhubinsights.web.rest.TestUtil;

class PrisonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Prison.class);
        Prison prison1 = new Prison();
        prison1.setId(1L);
        Prison prison2 = new Prison();
        prison2.setId(prison1.getId());
        assertThat(prison1).isEqualTo(prison2);
        prison2.setId(2L);
        assertThat(prison1).isNotEqualTo(prison2);
        prison1.setId(null);
        assertThat(prison1).isNotEqualTo(prison2);
    }
}
