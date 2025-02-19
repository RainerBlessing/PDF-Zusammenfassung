package com.aimitjava;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class HelloWorldTest {
    @Test
    void shouldPassBasicTest() {
        assertThat(true, is(true));
    }
}

