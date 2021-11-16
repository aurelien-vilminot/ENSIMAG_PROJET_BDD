import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class Database {
    private static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    private static final String USER = "vilminoa";
    private static final String PASSWD = "vilminoa";

    private Connection connection;
    private JSONParse jsonParse;

    public Database() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            this.connection = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            e.printStackTrace(System.err);
        }

        this.jsonParse = new JSONParse();
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void closeStatement(PreparedStatement statement, ResultSet resultSet) {
        try {
            resultSet.close();
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean userConnection(String email, String password) {
        return true;
//        // Return true if user id/pwd is correct
//        try {
//            PreparedStatement statement = this.connection.prepareStatement("SELECT ID FROM USER WHERE ID =? AND PASSWORD =?");
//            statement.setString(1, email);
//            statement.setString(2, password);
//            ResultSet resultSet = statement.executeQuery();
//
//            if (resultSet.getFetchSize() == 1) {
//                // If there is one rows corresponding to a user, this means that the user's information are correct
//                closeStatement(statement, resultSet);
//                return true;
//            }
//            closeStatement(statement, resultSet);
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return false;
    }

    public void forgetRight(String email) {
        System.out.println("Suppression en cours...");
        System.out.println("TODO: requete SQL");
//        try {
//            PreparedStatement statement = this.connection.prepareStatement("DELETE ID FROM USER WHERE ID =?");
//            statement.setString(1, email);
//            ResultSet resultSet = statement.executeQuery();
//            closeStatement(statement, resultSet);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }

    public ArrayList<String> getCatalog(Integer idCategorie) {
        // Renvoie un tableau de String s'il y a encore des sous-caractéristques dispo, null sinon (il n'y que des produits à afficher)
        if (idCategorie == null) {
            // Renvoie un tableau de String s'il y a des catégories mères
        } else {
            // Renvoie un tableau de String s'il y a des catégories contenues dans la catégorie
        }
        return null;
    }

    public void getRecommendedCategories() {

    }

    public ArrayList<String> getProductList(int idCategory) {
        return new ArrayList<>(Arrays.asList("product1", "product2", "product3"));
    }

    public ArrayList<String> getProduct(int idProduct) {
        // Renvoie null si le produit est inconnu
        return null;
    }

    public void makeBid() {

    }

    public void dropTables() {
        // Get all table names
        Iterator<String> iterator = this.jsonParse.parseArray("tables");
        while (iterator.hasNext()) {
            String table = iterator.next();
            try {
                // Delete the specified table
                PreparedStatement statement = this.connection.prepareStatement("DROP TABLE ?");
                statement.setString(1, table);
                ResultSet resultSet = statement.executeQuery();
                closeStatement(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void resetTables() {
        // Get all table names
        Iterator<String> iterator = this.jsonParse.parseArray("tables");
        while (iterator.hasNext()) {
            String table = iterator.next();
            try {
                // Reset the specified table
                // TRUNCATE is used to reset auto-increment
                PreparedStatement statement = this.connection.prepareStatement("TRUNCATE TABLE ?");
                statement.setString(1, table);
                ResultSet resultSet = statement.executeQuery();
                closeStatement(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void createTable() {
        Iterator<String> iterator = this.jsonParse.parseArray("createTable");
        while (iterator.hasNext()) {
            String createQuery = iterator.next();
            try {
                PreparedStatement statement = this.connection.prepareStatement(createQuery);
                ResultSet resultSet = statement.executeQuery();
                closeStatement(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
