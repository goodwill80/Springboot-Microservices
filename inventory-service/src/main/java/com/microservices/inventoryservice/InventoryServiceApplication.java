package com.microservices.inventoryservice;

import com.microservices.inventoryservice.model.Inventory;
import com.microservices.inventoryservice.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	@Autowired
	InventoryRepository inventoryRepository;

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@PostConstruct
	public void loadData() {
		// Supplier from commandlineRunner
			Inventory inventory = new Inventory();
			inventory.setSkuCode("i_phone_13");
			inventory.setQuantity(100);
			inventoryRepository.save(inventory);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("i_phone_15");
			inventory1.setQuantity(0);
			inventoryRepository.save(inventory1);

	}
}
