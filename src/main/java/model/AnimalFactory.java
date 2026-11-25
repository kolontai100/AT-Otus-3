package model;

public class AnimalFactory {

    public static Animal create(String type) {
        switch (type) {
            case "cat":
                return new Cat();
            case "dog":
                return new Dog();
            case "duck":
                return new Duck();
            default:
                return null;
        }
    }
}