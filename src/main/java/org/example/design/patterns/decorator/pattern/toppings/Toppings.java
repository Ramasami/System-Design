package org.example.design.patterns.decorator.pattern.toppings;

import lombok.AllArgsConstructor;
import org.example.design.patterns.decorator.pattern.base.pizza.BasePizza;

@AllArgsConstructor
public abstract class Toppings implements BasePizza {
    BasePizza pizza;
}
