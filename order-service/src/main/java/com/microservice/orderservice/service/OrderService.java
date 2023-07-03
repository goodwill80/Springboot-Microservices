package com.microservice.orderservice.service;

import com.microservice.orderservice.dto.InventoryResponse;
import com.microservice.orderservice.dto.OrderLineItemsDto;
import com.microservice.orderservice.dto.OrderRequest;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.model.OrderLineItems;
import com.microservice.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

       List<OrderLineItems> orderLineItems =  orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

       order.setOrderLineItemsList(orderLineItems);

       // Get list of sku-codes from order
        List<String> skuCodeStrings = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

       // API Call to inventory service to check product inventory before placing order
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodeStrings).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class) // return data type from web-flux - to enable reading of the return data
                .block(); // To allow synchronous request to the end point
        assert inventoryResponseArray != null;
        // Check if all inventory is inStock
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        if(allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

    }

    // Helper
    public OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
