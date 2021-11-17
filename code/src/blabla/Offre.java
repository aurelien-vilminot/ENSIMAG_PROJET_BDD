//import java.util.ArrayList;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class Offre {
//	private float price;
//	private String date;
//	private int idProduct;
//	private String mail;
//
//	private Offre(float price, int idProduct, String mail) {
//		this.price = price;
//		this.idProduct =idProduct;
//		this.mail = mail;
//		this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"));
//	}
//	//Valider l'achat?
//	public static boolean newOffer(float price, int idProduct, String mail) throws OfferException,ProductNotAvailable{
//		Offre offer = new Offre(price, idProduct, mail);
//		ArrayList<String> products = Offres.getOffersInfos(offer.getIdProduct());
//		if(price>Float.parseFloat(products.get(0))) {
//			switch (Integer.parseInt(products.get(1))) {
//			case 5:
//				throw new ProductNotAvailable();
//			case 4:
//				Offres.ajoutOffre(offer);
//				return true;
//			default:
//				Offres.ajoutOffre(offer);
//				return false;
//			}
//		}
//		throw new OfferException();
//	}
//
//
//	public float getPrice() {
//		return price;
//	}
//
//
//	public String getDate() {
//		return date;
//	}
//
//	public int getIdProduct() {
//		return idProduct;
//	}
//
//	public String getMail() {
//		return mail;
//	}
//
//}
