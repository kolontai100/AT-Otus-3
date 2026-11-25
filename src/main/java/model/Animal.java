package model;

public abstract class Animal {

    private int id;     // номер записи в базе
    private String name;
    private int age;
    private int weight;
    private String color;
    private String type;    // "cat" / "dog" / "duck"

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract void say();

    public void go() {
        System.out.println("Я иду");
    }

    public void drink() {
        System.out.println("Я пью");
    }

    public void eat() {
        System.out.println("Я ем");
    }

    @Override
    public String toString() {
        return "Привет! Меня зовут " + name + ", мне " + age + " " + getAgeWord(age)
                + ", я вешу - " + weight + " кг, мой цвет - " + color;
    }

    private static String getAgeWord(int age) {
        int lastDigit = age % 10;
        int lastTwo = age % 100;
        if (lastTwo >= 11 && lastTwo <= 14) {
            return "лет";
        }
        if (lastDigit == 1) {
            return "год";
        }
        if (lastDigit >= 2 && lastDigit <= 4) {
            return "года";
        }
        return "лет";
    }
}