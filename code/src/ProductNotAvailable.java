public class ProductNotAvailable extends Exception{
    
    public ProductNotAvailable()
    {
        super("Le produit est déja vendu ");
    }
}
