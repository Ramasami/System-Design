package org.example.design.patterns.decorator.pattern.toppings;

import org.example.design.patterns.decorator.pattern.base.pizza.BasePizza;

public class ExtraOlives extends  Toppings {

    public ExtraOlives(BasePizza pizza) {
        super(pizza);
    }

    @Override
    public int getCost() {
        return pizza.getCost() + 40;
    }
}
