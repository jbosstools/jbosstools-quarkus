package com.redhat.devtools.quarkus.maven.multi.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.devtools.quarkus.maven.multi.dao.Product;
import com.redhat.devtools.quarkus.maven.multi.dao.ProductService;

@Path("/hello")
public class ProductResource {
	
	@Inject
	ProductService productService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> hello() {
        return productService.getProducts();
    }
}