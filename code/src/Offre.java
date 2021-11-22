import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Offre {
	private float prix;
	private String date;
	private int idProduit;
	private int idCompte;

	public Offre(float price, int idProduct, int idCompte) {
		this.prix = price;
		this.idProduit = idProduct;
		this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"));
		this.idCompte = idCompte;
	}

	/** Insère une nouvelle offre dans la bdd
	 * @throws OfferException Une offre ne peut pas être fait sur ce produit
	 * @throws ProductNotAvailable Le produit est invalide
     *
	 * Offre.insertOffre(db, prix, produit, mail)
	 */
	public static void insertOffre(Database db, float price, int idProduit, int idCompte) throws OfferException, ProductNotAvailable {
		Offre offre = new Offre(price, idProduit, idCompte);
		ArrayList<String> products = db.offerInfos(offre.getIdProduct());
		if (offre.getPrice() > Float.parseFloat(products.get(0))) {
			switch (Integer.parseInt(products.get(1))) {
			case 5:
				throw new ProductNotAvailable();
			case 4:
				db.setOfferWin(offre);
			default:
				db.addOffer(offre);
			}
		}
		throw new OfferException(); // Le prix proposé est inférieur au prix courant
	}

	public float getPrice() {
		return this.prix;
	}

	public String getDate() {
		return this.date;
	}

	public int getIdProduct() {
		return this.idProduit;
	}

	public int getIdCompte() {
		return this.idCompte;
	}
}
