package ro.luci.jailhubinsights.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InmateMapperTest {

    private InmateMapper inmateMapper;

    @BeforeEach
    public void setUp() {
        inmateMapper = new InmateMapperImpl();
    }
}
