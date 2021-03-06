package program;

import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    static Scanner in = new Scanner(System.in);
    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    public static void main(String[] args) {
        String strConn = "jdbc:mariadb://localhost:3306/javacrud1";
        //InsertIntoDB(strConn);
        SeedDBFacker(strConn);
        List<Product> list = SelectFromDB(strConn);
        PrintProductList(list);
//        UpdateForDB(strConn);
//        list = SelectFromDB(strConn);
//        PrintProductList(list);
//        DeleteFromDB(strConn);
//        PrintProductList(SelectFromDB(strConn));
    }

    private static void PrintProductList(List<Product> prods) {
        for (Product p : prods) {
            System.out.println(p.toString());
        }
    }

    private static void InsertIntoDB(String strConn) {
        boolean boolPrice = false;
        try (Connection con = DriverManager.getConnection(strConn, "root", "")) {
            System.out.println("Successful connection");
            String query = "INSERT INTO `products` (`name`, `price`, `description`) " +
                    "VALUES (?, ?, ?);";
            try (PreparedStatement stmt = con.prepareStatement(query)) {//Класс для ввода параметров
                String name, description;
                double price;
                System.out.print("Enter name: ");
                if (in.hasNextLine()) {
                    name = in.nextLine();
                    stmt.setString(1, name);
                } else
                    System.out.println("Ошибка ввода. Введите цену в строковом формате!");

                while (!boolPrice) {
                    try {
                        System.out.print("Введите цену : ");
                        price = Double.parseDouble(in.nextLine());
                        stmt.setBigDecimal(2, new BigDecimal(price));
                        boolPrice = true;
                    } catch (Exception e) {
                        System.out.println("Ошибка выбора операции. Введите целое число!");
                    }
                }

                System.out.print("Enter description: ");
                description = in.nextLine();
                stmt.setString(3, description);

                int rows = stmt.executeUpdate();//к-во измененіх рядочков в БД
                System.out.println("Update rows: " + rows);

            } catch (Exception ex) {
                System.out.println("Error statements: " + ex.getMessage());
            }

        } catch (Exception ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }

    }

    private static  void SeedDBFacker(String strConn) {
        Faker faker = new Faker(
                new Locale("ru"));
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            System.out.println("Successful connection");

            String selectSql = "SELECT * FROM products";
            try {
                PreparedStatement ps = con.prepareStatement(selectSql);//создаем стейтман и передаем команду
                ResultSet resultSet = ps.executeQuery();//создаем результат запроса
                List<Product> products = new ArrayList<>();//создаем лист с продуктами
                while (resultSet.next()) {
                    Product p = new Product(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getString("description"));
                    products.add(p);
                }
                if(products==null||products.size()<=0) {
                    String query = "INSERT INTO `products` (`name`, `price`, `description`) " +
                            "VALUES (?, ?, ?);";
                    int countProd=0;
                    System.out.print("Сколько продуктов закинуть в базу: ");
                    countProd= in.nextInt();
                    for (
                            int i=0;i<=countProd-1;i++){
                        try (PreparedStatement stmt = con.prepareStatement(query)) {
                            String name, description;
                            double price;

                            name = faker.food().vegetable();

                            price = getRandomNumber(10,200);

                            description = faker.food().ingredient();

                            stmt.setString(1, name);
                            stmt.setBigDecimal(2, new BigDecimal(price));
                            stmt.setString(3, description);

                            int rows = stmt.executeUpdate();
                            System.out.println("Update rows: " +rows);

                        }
                        catch (Exception ex) {
                            System.out.println("Error statements: " + ex.getMessage());
                        }
                    }
                }

            } catch (Exception ex) {
                System.out.println("Error executeQuery: " + ex.getMessage());
            }

        } catch (Exception ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }

    }

    private static List<Product> SelectFromDB(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            String selectSql = "SELECT * FROM products";
            try {
                PreparedStatement ps = con.prepareStatement(selectSql);//создаем стейтман и передаем команду
                ResultSet resultSet = ps.executeQuery();//создаем результат запроса
                List<Product> products = new ArrayList<>();//создаем лист с продуктами
                while (resultSet.next()) {
                    Product p = new Product(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getString("description"));
                    products.add(p);
                }
                return products;
            } catch (Exception ex) {
                System.out.println("Error executeQuery: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }
        return null;
    }

    private static void UpdateForDB(String strConn) {
        int id;
        boolean boolID = false;
        try (Connection con = DriverManager.getConnection(strConn, "root", "")) {
            String query = "UPDATE products SET name = ? WHERE id = ?";//запрос на обновление названия продукта по ID
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                while (!boolID) {
                    try {
                        System.out.print("Enter Id: ");
                        id = Integer.parseInt(in.nextLine());

                        stmt.setInt(2, id);
                        boolID = true;
                    } catch (Exception e) {
                        System.out.println("Ошибка выбора операции. Введите целое число!");
                    }
                }

                System.out.print("Enter new name: ");

                String name = in.nextLine();
                stmt.setString(1, name);//вносятся параметры

                int rows = stmt.executeUpdate();//выполняется запрос

                System.out.println("Successful update " + rows);
            } catch (Exception ex) {
                System.out.println("Error update:" + ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }
    }

    private static void DeleteFromDB(String strConn) {
        int id;
        boolean bool = false;
       try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            String query = "DELETE FROM products WHERE id = ?";
            try(PreparedStatement stmt = con.prepareStatement(query)) {
                while (!bool) {
                try {System.out.print("Enter Id: ");
                    id = Integer.parseInt(in.nextLine());

                stmt.setInt(1, id);
                    bool = true;}
                catch (Exception e) {
                    System.out.println("Ошибка выбора операции. Введите целое число!");}}
                int rows = stmt.executeUpdate();
                System.out.println("Successful delete " + rows);

            } catch (Exception ex) {
                System.out.println("Error delete: " + ex.getMessage());
            }

        } catch (Exception ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }

    }

}

