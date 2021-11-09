import java.util.Scanner;

public class Interface {
    private boolean isRunning;
    private final String[] menuItems;
    private Database database;

    public Interface(Database database) {
        this.isRunning = true;
        this.menuItems = new String[]{"Parcours catalogue", "Faire une enchère", "Droit à l'oubli"};
        this.database = database;
        userConnection();
    }

    public void userConnection() {
        while (true) {
            System.out.println("• CONNEXION UTILISATEUR •");
            System.out.println("Saisir votre e-mail :");
            String userId = getInput();
            System.out.println("Saisir votre mot de passse :");
            String userPassword = getInput();
            if (this.database.userConnection(userId, userPassword)) {
                System.out.println("Connexion réussie !");
                break;
            }
            System.out.println("! Échec de la connexion !");
        }

        clearScreen();
    }

    private String getInput() {
        Scanner userInput = new Scanner(System.in);
        return userInput.next();
    }

    private void clearScreen() {
        // TODO: clear terminal
    }

    public void run() {
        while(isRunning) {
            menuShow();
            menuUserInput();
        }
    }

    public void menuShow() {
        System.out.println("• MENU •");
        int i = 1;
        for(String item : menuItems) {
            System.out.println( i +") "+item);
            i++;
        }
    }

    public void menuUserInput()
    {
        System.out.println("Votre choix :");

        switch (this.getInput()) {
            case "1" -> this.database.displayCatalog();
            case "2" -> this.database.makeBid();
            case "3" -> this.database.forgetRight();
            case "quit" -> {
                System.out.println("Déconnection");
                this.isRunning = false;
                userConnection();
            }
        }
    }

    public static void main(String[] args) {
        Database database = new Database();
        Interface menu = new Interface(database);
        menu.run();
        database.closeConnection();
    }
}
