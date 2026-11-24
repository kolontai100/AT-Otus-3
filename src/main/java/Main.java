import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    enum Command {
        ADD, LIST, EDIT, FILTER, EXIT
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Добро пожаловать! Команды: add / list / edit / filter / exit");
        while (true) {
            Command command = readCommand();
            switch (command) {
                case ADD:
                    addAnimal();
                    break;
                case LIST:
                    printAnimals(loadAnimals(null));
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


    private static Animal createByType(String type) {
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

    private static String readAnimalType() {
        System.out.println("Какое животное? (cat/dog/duck)");
        String type = scanner.nextLine().trim().toLowerCase();
        if (createByType(type) == null) {
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

    private static void addAnimal() {
        String type = readAnimalType();
        if (type == null) {
            return;
        }
        Animal animal = createByType(type);
        animal.setType(type);
        fillFields(animal);

        String sql = "INSERT INTO animals (name, age, weight, color, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, animal.getName());
            statement.setInt(2, animal.getAge());
            statement.setInt(3, animal.getWeight());
            statement.setString(4, animal.getColor());
            statement.setString(5, animal.getType());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                animal.setId(keys.getInt(1));
            }
            System.out.println("Сохранено в базу с номером " + animal.getId());
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
            return;
        }
        animal.say();
    }

    private static List<Animal> loadAnimals(String type) {
        String sql = (type == null)
                ? "SELECT id, name, age, weight, color, type FROM animals ORDER BY id"
                : "SELECT id, name, age, weight, color, type FROM animals WHERE type = ? ORDER BY id";

        List<Animal> result = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (type != null) {
                statement.setString(1, type);
            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Animal animal = createByType(rs.getString("type"));
                if (animal == null) {
                    continue;
                }
                animal.setId(rs.getInt("id"));
                animal.setName(rs.getString("name"));
                animal.setAge(rs.getInt("age"));
                animal.setWeight(rs.getInt("weight"));
                animal.setColor(rs.getString("color"));
                result.add(animal);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка чтения из базы: " + e.getMessage());
        }
        return result;
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

    private static void editAnimal() {
        List<Animal> animals = loadAnimals(null);
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

        String sql = "UPDATE animals SET name = ?, age = ?, weight = ?, color = ? WHERE id = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, found.getName());
            statement.setInt(2, found.getAge());
            statement.setInt(3, found.getWeight());
            statement.setString(4, found.getColor());
            statement.setInt(5, id);
            statement.executeUpdate();
            System.out.println("Изменения сохранены в базу");
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении: " + e.getMessage());
        }
    }

    private static void filterAnimals() {
        String type = readAnimalType();
        if (type == null) {
            return;
        }
        printAnimals(loadAnimals(type));
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Нужно целое число, попробуйте ещё раз:");
            }
        }
    }
}
