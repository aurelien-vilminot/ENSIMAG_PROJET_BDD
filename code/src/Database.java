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
            this.connection.setTransactionIsolation(this.connection.TRANSACTION_SERIALIZABLE);
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

    public int getIdCompte(String userMail) {
        int idCompte = 0;

        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT IDCOMPTE FROM UTILISATEUR WHERE MAILUTIL =?"
            );
            statement.setString(1, userMail);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            idCompte = resultSet.getInt(1);
            closeStatement(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return idCompte;
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
            PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT p.nomProd " +
                            "FROM Offre o, Produit p " +
                            "WHERE o.idCompte = ? " +
                            "AND o.idProd = p.idProd " +
                            "AND NOT EXISTS (SELECT * " +
                                            "FROM OffreGagnante og " +
                                            "WHERE o.dateOffre = og.dateOffre " +
                                            "AND o.idProd = og.idProd)"
            );
            statement.setInt(1, idCompte);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public record ProductSummary(int id, String name) {}
    /**
     * Returns a list of product ids & product names, useful to display the list.
     * The product id is useful to get more info about the product later
     */
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
                        resultSet.getInt(1), resultSet.getString(2))
                );
            }
            closeStatement(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> getProduct(int idProduct) {
        //TODO : trier l'affichage des produits

        /*
         * SELECT p.idProd, p.nomProd, COALESCE(t.offercount, 0) AS offercount
         * FROM Produit p
         * LEFT JOIN
         * (
         *      SELECT idProd, count(*) as offercount
         *      FROM OFFRE
         *      GROUP BY idProd
         * ) t
         *   ON p.idProd = t.idProd
         * WHERE p.nomcategorie = 'Chaussures de ville'
         * order by p.nomprod
         */

        ArrayList<String> result = new ArrayList<>();

        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT NOMPROD, PRIXCPROD, DESCPROD, URLPROD, NOMCATEGORIE FROM PRODUIT WHERE IDPROD =?"
            );
            statement.setInt(1, idProduct);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                for (int i = 1; i < 6; ++i) {
                    result.add(resultSet.getString(i));
                }
            }
            closeStatement(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public HashMap<String, String> getCaracProd(int idProduct) {
        HashMap<String, String> caracs = new HashMap<>();

        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT CARACPROD, VALEURPROD FROM CARACPRODUIT WHERE IDPROD =?"
            );
            statement.setInt(1, idProduct);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                caracs.put(resultSet.getString(1), resultSet.getString(2));
            }
            closeStatement(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return caracs;
    }

    public void addOffer(Offre offre) {
        try {
            PreparedStatement insertStmt = this.connection.prepareStatement("INSERT INTO OFFRE VALUES (?, ?, ?, ?)");
            insertStmt.setInt(1, offre.getIdProduct());
            insertStmt.setString(2, offre.getDate());
            insertStmt.setFloat(3, offre.getPrice());
            insertStmt.setInt(4, offre.getIdCompte());
            insertStmt.executeUpdate();

            PreparedStatement updateStmt = this.connection.prepareStatement(
                    "UPDATE PRODUIT SET PrixCProd=? WHERE IDPROD=?"
            );
            updateStmt.setFloat(1, offre.getPrice());
            updateStmt.setInt(2, offre.getIdProduct());
            updateStmt.executeUpdate();

            // Commit and close statements
            this.connection.commit();
            insertStmt.close();
            updateStmt.close();

            System.out.println("ajout d'une ligne dans la table OFFRE et modification du prix du produit correspondant dans la table PRODUIT");
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public ArrayList<String> offerInfos(int idProduit) {
        ArrayList<String> result = new ArrayList<>();

        try {
            // Creation de la requete
            PreparedStatement stmt = this.connection.prepareStatement(
                    "SELECT p.PRIXCPROD, COUNT(dateOffre, o.IDPROD) as NbOffres " +
                            "FROM PRODUIT p, OFFRE o " +
                            "WHERE p.IDPROD = o.IDPROD " +
                            "AND p.IDPROD = ? " +
                            "GROUP BY  p.PrixCProd "
            );
            stmt.setInt(1, idProduit);

            // Execution de la requete
            ResultSet rset = stmt.executeQuery();

            // Affichage du resultat
            while (rset.next()) {
                result.add(rset.getString(1));
                result.add(rset.getString(2));
            }

            closeStatement(stmt, rset);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return result;
    }

    public void setOfferWin(Offre offre) {
        try {
            PreparedStatement insertStmt = this.connection.prepareStatement("INSERT INTO OFFREGAGNANTE VALUES (?, ?)");
            insertStmt.setString(1, offre.getDate());
            insertStmt.setInt(2, offre.getIdProduct());
            insertStmt.executeUpdate();
            closeStatementAndCommit(insertStmt, null);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
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
