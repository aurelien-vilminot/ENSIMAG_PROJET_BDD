import java.sql.Date;

public class Offer {
	private float newPrice;
	private int idProduit;
	private int idCompte;
	private Date date;
	private final static int NB_MAX_OFFER = 5;

	public Offer(float newPrice, int idProduct, int idCompte) {
		this.newPrice = newPrice;
		this.idProduit = idProduct;
		this.idCompte = idCompte;
		java.util.Date currentDate = new java.util.Date();
		this.date = new java.sql.Date(currentDate.getTime());
	}

	/** Insère une nouvelle offre dans la bdd
     *
	 * Offre.insertOffre(db, prix, produit, mail)
	 */
	public boolean insertOffre(Database db) {
		int nbOffers = db.nbOffers(this.idProduit);
		if (nbOffers == NB_MAX_OFFER - 1) {
			db.setOfferWin(this);
			return true;
		} else {
			db.addOffer(this);
			return false;
		}
	}

	public float getPrice() {
		return this.newPrice;
	}

	public int getIdProduct() {
		return this.idProduit;
	}

	public int getIdCompte() {
		return this.idCompte;
	}

	public Date getDate() {return this.date;}
}
