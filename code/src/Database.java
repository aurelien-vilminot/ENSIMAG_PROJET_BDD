import java.sql.*;

public class Database {
    private static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    private static final String USER = "vilminoa";
    private static final String PASSWD = "vilminoa";

    private Connection connection;

    public Database() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            this.connection = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            e.printStackTrace(System.err);
        }
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

    public boolean getCatalog(Integer idCategorie) {
        // Renvoie True s'il y a encore des sous-caractéristques dispo, False sinon (il n'y que des produits à afficher)
        if (idCategorie == null) {
            // Affiche les catégories mères
        } else {
            // Affiche les catégories contenues dans la catégorie
        }
        return true;
    }

    public void getRecommendedCategories() {

    }

    public void getProductList(int idCategory) {

    }

    public void getProduct(int idProduct) {

    }

    public void makeBid() {

    }

    public void resetTable() {

    }

    public void fulfillTable() {

    }

}
