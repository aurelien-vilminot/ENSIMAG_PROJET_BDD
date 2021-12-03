import java.sql.Date;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.math.*;
import java.nio.channels.spi.SelectorProvider;

public class Offer {
	private float newPrice;
	private int idProduit;
	private int idCompte;
	private Date date;
	public final static int NB_MAX_OFFER = 5;

	public Offer(float newPrice, int idProduct, int idCompte) {
		this.newPrice = newPrice;
		this.idProduit = idProduct;
		this.idCompte = idCompte;
		java.util.Date currentDate = new java.util.Date();
		this.date = new java.sql.Date(currentDate.getTime());
	}

	public Offer(float newPrice, int idProduct, int idCompte, int delay) {
		this(newPrice, idProduct, idCompte);
		java.util.Date currentDate = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.SECOND, delay);
		currentDate = c.getTime();
		this.date = new java.sql.Date(currentDate.getTime());
	}

	public boolean insertOffre(Database db) throws IllegalAccessError {
		return db.addOffer(this);
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

	public static void addTestOffers(Database database) {
		System.out.println("Remplissage des offres\n...");
		Iterator<ArrayList<Float>> iterator = database.jsonParse.parseFloatArray("fakeOffers");
		int delay = 0;
		while (iterator.hasNext()) {
			ArrayList<Float> aList = iterator.next();
			Iterator<Float> it = aList.iterator();
			float newPrice = ((Number) it.next()).floatValue();
			int idProduct = Math.round(((Number) it.next()).floatValue());
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			int idCompte = Math.round(((Number) it.next()).floatValue());
            (new Offer(newPrice, idProduct, idCompte, delay++)).insertOffre(database);
        }
		System.out.println("Remplissage des offres terminé");
	}
}
