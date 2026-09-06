package com.codingshuttle.projects.airBnb.strategy;

import com.codingshuttle.projects.airBnb.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
