import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class TestMain {
    public String date;
    public TestMain()
    {
        
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm"));
    }
    public void afficher()
    {
        System.out.println(date);
    }
}

class Test{
    public static void main(String[] args)
    {
        TestMain date = new TestMain();
        date.afficher();
    }
}
