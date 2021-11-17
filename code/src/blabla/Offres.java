//import java.sql.*;
//import java.util.ArrayList;
//import src.Database;
//public class Offres {
//    private static void ajouterOffre(Database dataBase, String dateOffre, float prixOffre, int idProduit, String mail) {
//		try {
//
//			// Creation de la requete 1
//			PreparedStatement insertStmt = dataBase.connection.prepareStatement("insert into OFFRE values (?, ?, ?, ?)");
//			insertStmt.setString(1, dateOffre);
//			insertStmt.setFloat(2, prixOffre);
//			insertStmt.setInt(3, idProduit);
//			insertStmt.setString(4, mail);
//
//			ResultSet rset1 = insertStmt.executeQuery();
//
//			rset1.close();
//			insertStmt.close();
//
//			PreparedStatement updateStmt = dataBase.connection.prepareStatement("update PRODUIT set PrixCProd=? where IdProduit=?");
//			updateStmt.setFloat(1, prixOffre);
//			updateStmt.setInt(2, idProduit);
//
//			// Execution de la requete 2
//			ResultSet rset2 = updateStmt.executeQuery();
//
//			System.out.println("ajout d'une ligne dans la table OFFRE et modification du prix du produit correspondant dans la table PRODUIT");
//
//
//			// Fermeture
//			rset2.close();
//			updateStmt.close();
//			System.out.println("closing statements ok");
//		} catch (SQLException e) {
//		System.err.println("failed");
//		e.printStackTrace(System.err);
//		}
//	}
//
//	public static void ajoutOffre(Offre offer) {
//		Database dataBase = new Database();
//		Offre.ajouterOffre(dataBase,
//				offer.getDate(),offer.getPrice(), offer.getIdProduct(), offer.getMail());
//		dataBase.Close();
//	}
//
//
//	public static ArrayList<String> offerInfos(Database dataBase, int idProduit){
//		try {
//
//			// Creation de la requete
//			PreparedStatement stmt = dataBase.connection.prepareStatement("select p.PriCProd, COUNT(o.dateOffre,idProduit) as NbOffres FROM products p " +
//																	  "LEFT OUTER JOIN offre o on p.idproduit=o.idproduit WHERE p.idproduit = ? " +
//																	  "GROUP BY  p.PRIXCOURANT ");
//			stmt.setInt(1, idProduit);
//
//			// Execution de la requete
//			ResultSet rset = stmt.executeQuery();
//
//			// Affichage du resultat
//			ArrayList<String> result = new ArrayList<String>();
//			while(rset.next()) {
//				result.add(rset.getString(1));
//				result.add(rset.getString(2));
//			}
//			// Fermeture
//			rset.close();
//			stmt.close();
//			return result;
//		}
//		catch (SQLException e)
//		{
//			System.err.println("failed");
//			e.printStackTrace(System.err);
//			return null;
//		}
//	}
//
//	public static ArrayList<String> getOffersInfos(int idProduct){
//		Database dataBase = new Database();
//		ArrayList<String> result = offerInfos(dataBase, idProduct);
//		dataBase.Close();
//		return result;
//	}
//
//}
