package com.microservice.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.productservice.dto.ProductRequest;
import com.microservice.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Dependencies to establish a test container for integration test
// a. Go to https://java.testcontainers.org/
// b. Include the following into POM
// - testContainer dependency
// - mongodb testContainer Dependency
// - Junit5 junit-jupiter testContainer Dependency



@SpringBootTest
@AutoConfigureMockMvc // 1a. Auto configure Mock MVC
@Testcontainers // 1b. Add the test container annotation
class ProductServiceApplicationTests {

	// 2. initialise a MongoDB container and annotate as container
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2"); // specify the docker base version image

	// 3a. Autowired MockMvc for mocking of requests
	@Autowired
	private MockMvc mockMvc;

	// 3b. Autowired ObjectMapper from textContainer to convert Object to string
	@Autowired
	private ObjectMapper objectMapper;

	// 3c. To call the the methods from repository for testing
	@Autowired
	private ProductRepository productRepository;

	// 4. Setting the uri of the mongoDBContainer dynamically after the mongoDBContainer is up
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	// 5. Start writing integration tests
	// 5a. Testing of Get All Products End-point
	@Test
	void shouldCreateProduct() throws Exception {
		// a. Method to create a mock product - see helper method below
		ProductRequest productRequest = getProductRequest();
		// b. Convert the product to String using Object mapper provided by testContainer
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		// c. Mock the request you are testing by passing in the uri and type of content that it returns
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product") // create product end point
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());

		
		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	// Helper method to create a mock product
	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("A solid phone!")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

}
