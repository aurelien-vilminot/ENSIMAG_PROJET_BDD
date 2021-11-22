import java.sql.*;
import java.util.*;

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

    private void closeStatement(PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void closeStatementAndCommit(PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {
        closeStatement(preparedStatement, resultSet);
        this.connection.commit();
    }

    public boolean userConnection(String email, String password) {
        // Return true if user id/pwd is correct
        boolean returnState = false;
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT COUNT(*) FROM UTILISATEUR WHERE MAILUTIL =? AND MDPUTIL =?"
            );
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) == 1) {
                // If the result of row count is 1, this means that the user's information are correct
                returnState = true;
            }
            closeStatement(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnState;
    }

    public String getIdCompte(String userMail) {
        // TODO: return idCompte corresponding to userMail
        return null;
    }

    public void forgetRight(String email) {
        System.out.println("Suppression en cours\n...");
        try {
            PreparedStatement statement = this.connection.prepareStatement("DELETE FROM UTILISATEUR WHERE MAILUTIL=?");
            statement.setString(1, email);
            statement.executeUpdate();
            closeStatementAndCommit(statement, null);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Suppression des données utilisateur terminée");
    }

    public ArrayList<String> getCatalog(String nameCategorie) {
        // Renvoie un tableau de String s'il y a encore des sous-caractéristques dispo
        // Si le tableau est vide, il n'y que des produits à afficher
        ArrayList<String> result = new ArrayList<>();

        if (nameCategorie == null) {
            // Renvoie un tableau de String s'il y a des catégories mères
            try {
                PreparedStatement statement = this.connection.prepareStatement(
                        "SELECT NOMCATEGORIE FROM CATEGORIE " +
                                "WHERE NOMCATEGORIE NOT IN (SELECT FILLECATEGORIE FROM APOURMERE)"
                );
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(resultSet.getString(1));
                }
                closeStatement(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            // Renvoie un tableau de String s'il y a des catégories contenues dans la catégorie
            try {
                PreparedStatement statement = this.connection.prepareStatement(
                        "SELECT FILLECATEGORIE FROM APOURMERE WHERE APOURMERE.MERECATEGORIE =?"
                );
                statement.setString(1, nameCategorie);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(resultSet.getString(1));
                }
                closeStatement(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return result;
    }

    public ArrayList<String> getRecommendedCategories(int idCompte) {
        // Tri par ordre décroissant du nombre d'offres et par ordre alphabétique

        // Les recommandations de catégories se basent sur l’historique d’offre et d’achat des utilisateurs. En priorité, 
        // les recommandations vont concerner les catégories pour lesquelles l’utilisateur a fait le plus d’offres sur des 
        // produits sans réussir à les acheter (par ordre décroissant du nombre d’offres). Ensuite, les recommandations 
        // concerneront  les catégories pour lesquelles il y a eu le plus d’offres en moyenne  par produit  (avec ou sans 
        // achat) et ce quel que soit l’utilisateur (par ordre décroissant du nombre moyen d’offres par produit). 

        // 1) Recommandation personnalisée
        //      |_ historique d'offre qui ne sont pas des achats de l'utilisateur [récupérer son idCompte]
        // 2) Recommandations générales
        //      |_ catégories pour lesquelles il y a le plus d'offres en moyenne par produit (avec ou sans achat), classées par ordre décroissant du nombre moyen d'offres par produit
        
        // Recommandations personnalisées
        // TODO
        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT p.nomProd FROM Offre as o, Produit as p WHERE o.idCompte = ? AND o.idProd = p.idProd AND NOT EXISTS (SELECT * FROM OffreGagnante as og WHERE o.dateOffre = og.dateOffre AND o.idProd = og.idProd)");
            statement.setInt(1, idCompte);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public record ProductSummary(int id, String name) {}
    /** Returns a list of product ids & product names, useful to display the list.
     * The product id is useful to get more info about the product later */
    public List<ProductSummary> getProductList(String nameCategory) {
        List<ProductSummary> result = new ArrayList<>();

        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT IDPROD, NOMPROD FROM PRODUIT WHERE NOMCATEGORIE=?"
            );
            statement.setString(1, nameCategory);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new ProductSummary(
                        resultSet.getInt(1), resultSet.getString(2)));
            }
            closeStatement(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> getProduct(int idProduct) {
        ArrayList<String> result = new ArrayList<>();

        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT NOMPROD, PRIXCPROD, DESCPROD, URLPROD, NOMCATEGORIE FROM PRODUIT WHERE IDPROD =?"
            );
            statement.setInt(1, idProduct);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                for (int i = 1 ; i < 6 ; ++i) {
                    result.add(resultSet.getString(i));
                }
            }
            closeStatement(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public void makeBid() {
        //TODO
    }

    public void dropTables() {
        System.out.println("Suppression définitive des tables\n...");
        Iterator<String> iterator = this.jsonParse.parseArray("dropTables");
        while (iterator.hasNext()) {
            String query = iterator.next();
            try {
                // Delete the specified table
                PreparedStatement statement = this.connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                closeStatement(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        System.out.println("Suppression terminée");
    }

    public void resetTables() {
        System.out.println("Remise à zéro des tables\n...");
        Iterator<String> iterator = this.jsonParse.parseArray("resetTables");
        while (iterator.hasNext()) {
            String query = iterator.next();
            try {
                // Reset the specified table
                // TRUNCATE is used to reset auto-increment
                PreparedStatement statement = this.connection.prepareStatement(query);
                statement.executeUpdate();
                closeStatement(statement, null);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        System.out.println("Remise à zéro terminée");
    }

    public void createTables() {
        System.out.println("Création des tables\n...");
        Iterator<String> iterator = this.jsonParse.parseArray("createTable");
        while (iterator.hasNext()) {
            String createQuery = iterator.next();
            try {
                PreparedStatement statement = this.connection.prepareStatement(createQuery);
                statement.executeUpdate();
                closeStatement(statement, null);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        System.out.println("Création terminée");
    }

    public void fillTables() {
        System.out.println("Remplissage des tables\n...");
        Iterator<String> iterator = this.jsonParse.parseArray("fillTable");
        while (iterator.hasNext()) {
            String fillQuery = iterator.next();
            try {
                PreparedStatement statement = this.connection.prepareStatement(fillQuery);
                statement.executeUpdate();
                closeStatementAndCommit(statement, null);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        System.out.println("Remplissage terminé");
    }

}
