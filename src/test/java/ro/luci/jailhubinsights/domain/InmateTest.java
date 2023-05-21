package ro.luci.jailhubinsights.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ro.luci.jailhubinsights.web.rest.TestUtil;

class InmateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Inmate.class);
        Inmate inmate1 = new Inmate();
        inmate1.setId(1L);
        Inmate inmate2 = new Inmate();
        inmate2.setId(inmate1.getId());
        assertThat(inmate1).isEqualTo(inmate2);
        inmate2.setId(2L);
        assertThat(inmate1).isNotEqualTo(inmate2);
        inmate1.setId(null);
        assertThat(inmate1).isNotEqualTo(inmate2);
    }
}
