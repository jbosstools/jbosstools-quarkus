package com.redhat.devtools.quarkus.maven.multi.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.redhat.devtools.quarkus.maven.multi.dao.ProductService;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ProductServiceTest {
	
	@Inject
	ProductService service;
	
	@Test
	public void checkServiceIsAvailable() {
		assertNotNull(service);
	}
	
	@Test
	public void checkServiceIsRunning() {
		assertTrue(service.getProducts().isEmpty());
	}
}
