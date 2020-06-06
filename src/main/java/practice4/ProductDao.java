package practice4;

import storage.Product;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ProductDao {
    private final Connection connection;
    private static ProductDao instance ;

    public static final String TABLE_NAME = "product";

    public ProductDao getInstance(){
        if(instance == null) instance = new ProductDao();
        return instance;
    }

    private ProductDao(){
        connection = DB.connect();
        createTable();
    }
    private void createTable(){
        try(final Statement createTable = connection.createStatement()){
            createTable.execute("create table if not exists 'product'('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' TEXT, 'price' DECIMAL(10,3))");
        } catch (SQLException e) {
            throw new RuntimeException("Can't create table", e);
        }
    }
    public Product create(String name, double price){
        try(final PreparedStatement insert= connection.prepareStatement(
                "insert into 'product' ('name', 'price') values (?,?)"
        , Statement.RETURN_GENERATED_KEYS)){

            insert.setString(1, name);
            insert.setDouble(2, price);
            insert.execute();

            ResultSet resultSet =  insert.getGeneratedKeys();
            Integer id;
            assert(resultSet.next());

            id = resultSet.getInt(1);

            System.out.println("Inserted " + id + " " + name);
            System.out.println();


            return new Product(id.intValue(), name, price);
        } catch (SQLException e ){
            throw new IllegalArgumentException("Can't insert :(");
        }
    }
    public void update(Product p){
        String updateStatement = "update "+TABLE_NAME+" set name = ?, price = ? where id = ?";
        try(final PreparedStatement update = connection.prepareStatement(updateStatement)){
            update.setString(1,p.getName());
            update.setDouble(2,p.getPrice());
            update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void delete(Product p){
        String deleteStatement = "delete from "+TABLE_NAME+" where id = ?";
        try(final PreparedStatement delete = connection.prepareStatement(deleteStatement)){
            delete.setInt(1,p.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  Product selectOneByName(String name) {
        String sqlQuery = "SELECT * FROM " + TABLE_NAME +  " WHERE name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)){

            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();
            Product p = null;
            if(resultSet.next()){
                p = new Product(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("price"), resultSet.getInt("quantity"));
            }
            return p;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    public  List<Product> select(int limit, int offset) {
        String sqlQuery = "SELECT * FROM " + TABLE_NAME +  " LIMIT ?, ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)){
            statement.setInt(1, offset);
            statement.setInt(2, limit);

            ResultSet resultSet =  statement.executeQuery(sqlQuery);
            return fromResultSet(resultSet);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }


    public  List<Product> selectAll() {
        String sqlQuery = "SELECT * FROM " + TABLE_NAME;

        try {
            Statement statement  = connection.createStatement();

            ResultSet resultSet =  statement.executeQuery(sqlQuery);
            return fromResultSet(resultSet);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }
    public List<Product> select(Criteria criteria){
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + criteria.getFilterQuery();

        try ( Statement statement  = connection.createStatement()){

            ResultSet resultSet = statement.executeQuery(sqlQuery);

           return  fromResultSet(resultSet);

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
    private List<Product> fromResultSet(ResultSet resultSet) throws SQLException {
        List<Product> products = new LinkedList<>();
        while(resultSet.next())
            products.add(new Product(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("price"), resultSet.getInt("quantity")));
        return products;
    }
    public void dropTable() {
        String sqlQuery = "DROP TABLE " + TABLE_NAME;

        try {
            Statement statement = connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + TABLE_NAME + " truncated");
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
