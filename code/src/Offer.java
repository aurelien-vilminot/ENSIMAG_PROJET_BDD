import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Offer {
	private float newPrice;
	private Date date;
	private int idProduit;
	private int idCompte;

	public Offer(float newPrice, int idProduct, int idCompte) {
		this.newPrice = newPrice;
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
	public boolean insertOffre(Database db) throws OfferException, ProductNotAvailable {
		int nbOffers = db.nbOffers(this.idProduit);
		if (nbOffers == this.NB_MAX_OFFER) {
			throw new ProductNotAvailable();
		} else if (nbOffers == NB_MAX_OFFER - 1) {
			db.setOfferWin(this);
			return true;
		}
		db.addOffer(this);
		return false;
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
