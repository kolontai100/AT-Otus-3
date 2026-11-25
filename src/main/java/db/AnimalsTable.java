package db;

import model.Animal;
import model.AnimalFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnimalsTable {

    public int add(Animal animal) {
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
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
        return -1;
    }

    public List<Animal> findAll() {
        String sql = "SELECT id, name, age, weight, color, type FROM animals ORDER BY id";
        return query(sql, null);
    }

    public List<Animal> findByType(String type) {
        String sql = "SELECT id, name, age, weight, color, type FROM animals WHERE type = ? ORDER BY id";
        return query(sql, type);
    }

    public void update(Animal animal) {
        String sql = "UPDATE animals SET name = ?, age = ?, weight = ?, color = ? WHERE id = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, animal.getName());
            statement.setInt(2, animal.getAge());
            statement.setInt(3, animal.getWeight());
            statement.setString(4, animal.getColor());
            statement.setInt(5, animal.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении: " + e.getMessage());
        }
    }


    private List<Animal> query(String sql, String type) {
        List<Animal> result = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (type != null) {
                statement.setString(1, type);
            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Animal animal = AnimalFactory.create(rs.getString("type"));
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
}