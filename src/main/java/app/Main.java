package app;

import data.Command;
import db.AnimalsTable;
import model.Animal;
import model.AnimalFactory;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AnimalsTable animalsTable = new AnimalsTable();

    public static void main(String[] args) {
        System.out.println("Добро пожаловать! Команды: add / list / edit / filter / exit");
        while (true) {
            System.out.println("Введите команду (add / list / edit / filter / exit):");
            Command command = readCommand();
            switch (command) {
                case ADD:
                    addAnimal();
                    break;
                case LIST:
                    printAnimals(animalsTable.findAll());
                    break;
                case EDIT:
                    editAnimal();
                    break;
                case FILTER:
                    filterAnimals();
                    break;
                case EXIT:
                    System.out.println("До свидания!");
                    return;
            }
        }
    }

    private static Command readCommand() {
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Command.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Такой команды нет. Введите add, list, edit, filter или exit:");
            }
        }
    }

    private static void addAnimal() {
        String type = readAnimalType();
        if (type == null) {
            return;
        }
        Animal animal = AnimalFactory.create(type);
        animal.setType(type);
        fillFields(animal);

        int id = animalsTable.add(animal);
        if (id != -1) {
            animal.setId(id);
            System.out.println("Сохранено в базу с номером " + id);
            animal.say();
        }
    }

    private static String readAnimalType() {
        System.out.println("Какое животное? (cat/dog/duck)");
        String type = scanner.nextLine().trim().toLowerCase();
        if (AnimalFactory.create(type) == null) {
            System.out.println("Такого животного несуществует, возврат в меню");
            return null;
        }
        return type;
    }

    private static void fillFields(Animal animal) {
        System.out.println("Введите имя:");
        animal.setName(scanner.nextLine().trim());
        System.out.println("Введите возраст:");
        animal.setAge(readInt());
        System.out.println("Введите вес:");
        animal.setWeight(readInt());
        System.out.println("Введите цвет:");
        animal.setColor(scanner.nextLine().trim());
    }

    private static void editAnimal() {
        List<Animal> animals = animalsTable.findAll();
        if (animals.isEmpty()) {
            System.out.println("Редактировать нечего - база пуста");
            return;
        }
        printAnimals(animals);
        System.out.println("Введите номер (id) животного, которое редактируем:");
        int id = readInt();

        Animal found = null;
        for (Animal animal : animals) {
            if (animal.getId() == id) {
                found = animal;
            }
        }
        if (found == null) {
            System.out.println("Животного с номером " + id + " нет в базе");
            return;
        }

        System.out.println("Вводи новые данные (текущее значение - в скобках):");
        System.out.println("Имя (" + found.getName() + "):");
        found.setName(scanner.nextLine().trim());
        System.out.println("Возраст (" + found.getAge() + "):");
        found.setAge(readInt());
        System.out.println("Вес (" + found.getWeight() + "):");
        found.setWeight(readInt());
        System.out.println("Цвет (" + found.getColor() + "):");
        found.setColor(scanner.nextLine().trim());

        animalsTable.update(found);
        System.out.println("Изменения сохранены в базу");
    }

    private static void filterAnimals() {
        String type = readAnimalType();
        if (type == null) {
            return;
        }
        printAnimals(animalsTable.findByType(type));
    }

    private static void printAnimals(List<Animal> animals) {
        if (animals.isEmpty()) {
            System.out.println("Животных нет");
            return;
        }
        for (Animal animal : animals) {
            System.out.println("[" + animal.getId() + "] " + animal.toString());
        }
    }

    private static int readInt() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("Значение не может быть отрицательным, попробуйте ещё раз:");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Нужно целое число, попробуйте ещё раз:");
            }
        }
    }
}