public class ProductNotAvailable extends Exception{
    
    public ProductNotAvailable()
    {
        super("Le produit n'est plus disponible \n");
    }
}
