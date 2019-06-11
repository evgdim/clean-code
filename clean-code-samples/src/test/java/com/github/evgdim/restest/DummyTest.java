package com.github.evgdim.restest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.vavr.CheckedFunction1;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Function;
import java.util.function.Supplier;

public class DummyTest
{
    @Test
    public void one_shouldBeEqualToOne() {
        assertThat("1").isEqualTo("1");
    }

}
