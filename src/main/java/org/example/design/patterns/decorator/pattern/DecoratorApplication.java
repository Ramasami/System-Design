package org.example.design.patterns.decorator.pattern;

import org.example.design.patterns.decorator.pattern.base.pizza.BasePizza;
import org.example.design.patterns.decorator.pattern.base.pizza.FarmhouseBasePizza;
import org.example.design.patterns.decorator.pattern.base.pizza.MargheritaBasePizza;
import org.example.design.patterns.decorator.pattern.base.pizza.PeppyPaneerBasePizza;
import org.example.design.patterns.decorator.pattern.toppings.ExtraCheese;
import org.example.design.patterns.decorator.pattern.toppings.ExtraOlives;
import org.example.design.patterns.decorator.pattern.toppings.ExtraOnions;

public class DecoratorApplication {

    public static void main(String[] args) {
        BasePizza farmhouseBasePizza = new FarmhouseBasePizza();
        BasePizza margheritaBasePizza = new MargheritaBasePizza();
        BasePizza peppyPaneerBasePizza = new PeppyPaneerBasePizza();

        BasePizza pizzaWithToppings = new ExtraCheese(new ExtraOlives(new ExtraOnions(new ExtraCheese(farmhouseBasePizza))));
        System.out.println(pizzaWithToppings.getCost());
    }
}
