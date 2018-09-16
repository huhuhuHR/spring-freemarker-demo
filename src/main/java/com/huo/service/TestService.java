package com.huo.service;

import org.springframework.stereotype.Service;

@Service("service1")
public class TestService implements TestServiceFatherOne, TestServiceFatherTwo {

    @Override
    public void testOne() {

    }

    @Override
    public void testTwo() {

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "TestService{}";
    }
}
