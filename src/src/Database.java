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
        // Return true if user id/pwd is correct
        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT ID FROM USER WHERE ID =? AND PASSWORD =?");
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.getFetchSize() == 1) {
                // If there is one rows corresponding to a user, this means that the user's information are correct
                closeStatement(statement, resultSet);
                return true;
            }
            closeStatement(statement, resultSet);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void forgetRight() {
        try {
            PreparedStatement statement = this.connection.prepareStatement("select * from cinesalles where NBPLACES >? and NOMCINE like ?");

            statement.setInt(1, 200);
            statement.setString(2, "L%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Cinema : " + resultSet.getString(1) + " - Salle : " + resultSet.getString(2));
            }

            closeStatement(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void displayCatalog() {

    }

    public void makeBid() {

    }

}
