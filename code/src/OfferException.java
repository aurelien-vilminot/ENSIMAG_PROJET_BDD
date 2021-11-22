public class OfferException extends Exception{
    
    public OfferException()
    {
        super("Le prix proposé est inférieur au prix courant");
    }
}
