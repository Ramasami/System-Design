package org.example.design.patterns.decorator.pattern.toppings;

import org.example.design.patterns.decorator.pattern.base.pizza.BasePizza;

public class ExtraCheese extends  Toppings {

    public ExtraCheese(BasePizza pizza) {
        super(pizza);
    }

    @Override
    public int getCost() {
        return pizza.getCost() + 20;
    }
}
