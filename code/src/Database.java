import java.sql.*;
import java.util.*;

public class Database {
    private static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    private static final String USER = "vilminoa";
    private static final String PASSWD = "vilminoa";

    private Connection connection;
    public JSONParse jsonParse;

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

    private void commit() {
        try {
            this.connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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

            resultSet.close();
            statement.close();
            commit();
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

            resultSet.close();
            statement.close();
            commit();
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
            statement.close();
            commit();
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
                resultSet.close();
                statement.close();
                commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            // Renvoie un tableau de String s'il y a des catégories contenues dans la catégorie nameCategorie
            try {
                PreparedStatement statement = this.connection.prepareStatement(
                        "SELECT FILLECATEGORIE FROM APOURMERE WHERE APOURMERE.MERECATEGORIE =?"
                );
                statement.setString(1, nameCategorie);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(resultSet.getString(1));
                }

                resultSet.close();
                statement.close();
                commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return result;
    }

    public ArrayList<String> getRecommendedCategories(int idCompte) {
        // Tri par ordre décroissant du nombre d'offres et par ordre alphabétique
        // 1) Recommandation personnalisée
        //      |_ catégories pour lequelles l'utilisateur a fait des offres qui ne sont pas des achats, classées par orde décroissant du nombre d'offres par produit
        // 2) Recommandations générales
        //      |_ catégories pour lesquelles il y a le plus d'offres en moyenne par produit (avec ou sans achat), classées par ordre décroissant du nombre moyen d'offres par produit

        ArrayList<String> result = new ArrayList<>();
        
        // Recommandations personnalisées et générales dans cet ordre
        try {
            // Voir commentaire de "recommandations.sql"
            PreparedStatement statement = this.connection.prepareStatement(
                "SELECT p.nomCategorie AS nomCategorie, count(o.dateOffre) AS nb, 0 AS union_order " +
                "FROM Offre o, Produit p " +
                "WHERE o.idProd = p.idProd " +
                "AND o.idCompte = ? " +
                "AND NOT EXISTS (SELECT * " +
                "                FROM OffreGagnante og " +
                "                WHERE o.dateOffre = og.dateOffre " +
                "                AND o.idProd = og.idProd) " +
                "GROUP BY p.nomCategorie " +
                "UNION " +
                "SELECT p.nomCategorie AS nomCategorie, count(o.dateOffre)/count(DISTINCT o.idProd) AS nb, 1 AS union_order " +
                "FROM Offre o, Produit p  " +
                "WHERE o.idProd = p.idProd " +
                "AND p.nomCategorie NOT IN (SELECT p.nomCategorie " +
                "FROM Offre o, Produit p " +
                "WHERE o.idProd = p.idProd " +
                "AND o.idCompte = ? " +
                "AND NOT EXISTS (SELECT * " +
                "                FROM OffreGagnante og " +
                "                WHERE o.dateOffre = og.dateOffre " +
                "                AND o.idProd = og.idProd)) " +
                "GROUP BY p.nomCategorie " +
                "ORDER BY union_order, nb DESC, nomCategorie"
            );
            statement.setInt(1, idCompte);
            statement.setInt(2, idCompte);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }

            resultSet.close();
            statement.close();
            commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
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
                    "SELECT p.IDPROD, p.NOMPROD, COUNT(o.IDPROD) as NBOFFRE " +
                        "FROM PRODUIT p, OFFRE o " +
                        "WHERE p.NOMCATEGORIE =? " +
                        "AND o.IDPROD = p.IDPROD " +
                        "AND p.IDPROD NOT IN (SELECT IDPROD FROM OFFREGAGNANTE) " +
                        "GROUP BY p.IDPROD, p.NOMPROD " +
                        "UNION " +
                        "SELECT p.IDPROD, p.NOMPROD, NULL as NBOFFRE " +
                        "FROM PRODUIT p " +
                        "WHERE p.NOMCATEGORIE =? " +
                        "AND p.IDPROD NOT IN (SELECT OFFRE.IDPROD FROM OFFRE) " +
                        "ORDER BY NBOFFRE DESC NULLS LAST, NOMPROD"
            );
            statement.setString(1, nameCategory);
            statement.setString(2, nameCategory);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new ProductSummary(
                        resultSet.getInt(1), resultSet.getString(2))
                );
            }

            resultSet.close();
            statement.close();
            commit();
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
                for (int i = 1; i < 6; ++i) {
                    result.add(resultSet.getString(i));
                }
            }

            resultSet.close();
            statement.close();
            commit();
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

            resultSet.close();
            statement.close();
            commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return caracs;
    }

    public void addOffer(Offer offer) {
        try {
            PreparedStatement insertStmt = this.connection.prepareStatement("INSERT INTO OFFRE VALUES (?, ?, ?, ?)");
            insertStmt.setInt(1, offer.getIdProduct());
            insertStmt.setDate(2, offer.getDate());
            insertStmt.setFloat(3, offer.getPrice());
            insertStmt.setInt(4, offer.getIdCompte());
            insertStmt.executeUpdate();

            PreparedStatement updateStmt = this.connection.prepareStatement(
                    "UPDATE PRODUIT SET PrixCProd=? WHERE IDPROD=?"
            );
            updateStmt.setFloat(1, offer.getPrice());
            updateStmt.setInt(2, offer.getIdProduct());
            updateStmt.executeUpdate();

            // Commit and close statements
            insertStmt.close();
            updateStmt.close();
            commit();
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public void setOfferWin(Offer offer) {
        try {
            PreparedStatement insertStmt = this.connection.prepareStatement(
                    "INSERT INTO OFFREGAGNANTE VALUES (?, ?)"
            );
            insertStmt.setDate(1, offer.getDate());
            insertStmt.setInt(2, offer.getIdProduct());
            insertStmt.executeUpdate();
            insertStmt.close();

            // Add the offer to offer table and commit at the end of the function
            addOffer(offer);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public int nbOffers(int idProduit) {
        int result = 0;

        try {
            PreparedStatement stmt = this.connection.prepareStatement(
                    "SELECT COUNT(IDPROD) as NbOffres " +
                        "FROM OFFRE " +
                        "WHERE IDPROD =?"
            );
            stmt.setInt(1, idProduit);
            ResultSet rset = stmt.executeQuery();

            while (rset.next()) {
                result = rset.getInt(1);
            }

            rset.close();
            stmt.close();
            commit();
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return result;
    }

    public void dropTables() {
        System.out.println("Suppression définitive des tables\n...");
        Iterator<String> iterator = this.jsonParse.parseArray("dropTables");
        while (iterator.hasNext()) {
            String query = iterator.next();
            try {
                // Delete the specified table
                PreparedStatement statement = this.connection.prepareStatement(query);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        // Commit at the end off drop tables
        commit();
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
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        // Commit at the end off reset tables
        commit();
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
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        // Commit at the end off tables creation
        commit();
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
                statement.close();
                commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        System.out.println("Remplissage terminé");
    }

    public void fillOffers() {
        Offer.addTestOffers(this);
    }

}
