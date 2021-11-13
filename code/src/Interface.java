import java.util.Scanner;

public class Interface {
    private final String[] menuItems = new String[]{"Parcours catalogue", "Droit à l'oubli"};
    private final String[] catalogeMenuItems = new String[]{"Catalague produits", "Catégories recommandées", "Retour"};
    private final String[] yesNoItems = new String[]{"Oui", "Non"};

    private String userId;
    private String userPwd;

    private boolean isRunning;
    private final Database database;

    public Interface(Database database) {
        this.isRunning = true;
        this.database = database;
        userConnection();
    }

    public void userConnection() {
        while (true) {
            System.out.println("• CONNEXION UTILISATEUR •");
            System.out.println("Saisir votre e-mail :");
            this.userId = getInput();
            System.out.println("Saisir votre mot de passse :");
            this.userPwd = getInput();
            if (this.database.userConnection(this.userId, this.userPwd)) {
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
            menuUserInput();
        }
    }

    public void menuShow(String [] menuItems) {
        int i = 1;
        for(String item : menuItems) {
            System.out.println( i +") "+item);
            i++;
        }
        System.out.println("Votre choix :");
    }

    public void menuUserInput() {
        System.out.println("• MENU •");
        menuShow(this.menuItems);

        switch (this.getInput()) {
            case "1" -> {
                // Display catalogue menu
                menuShow(this.catalogeMenuItems);
                catalogueMenuUserInput();
            }
            case "2" -> forgetRights();
            case "quit" -> {
                System.out.println("Déconnection");
                this.isRunning = false;
                userConnection();
            }
            default -> menuUserInput();
        }
    }

    public void forgetRights() {
        System.out.println("Voulez-vous vraiment appliquer le droit à l'oubli ?\n" +
                "Cela supprimera totalement vos informations personnelles.");
        menuShow(this.yesNoItems);

        switch (this.getInput()) {
            case "1" -> {
                // Delete user information
                this.database.forgetRight(this.userId);
                System.out.println("Suppression terminée. Déconnection");
                this.isRunning = false;
                userConnection();
            }
            case "2" -> menuUserInput();
            case "quit" -> {
                System.out.println("Déconnection");
                this.isRunning = false;
                userConnection();
            }
            default -> forgetRights();
        }
    }

    public void catalogueMenuUserInput() {
        menuShow(this.catalogeMenuItems);
        System.out.println("Votre choix :");

        switch (this.getInput()) {
            case "1" -> {
                // Display catalogue
                Integer idSubCategorie = null;
                while (true) {
                    boolean isSubCategories = this.database.getCatalog(idSubCategorie);
                    if (!isSubCategories) {
                        // Display products7
                        this.database.getProductList(idSubCategorie);
                        break;
                    }
                    System.out.println("Votre choix :");
                    idSubCategorie = Integer.valueOf(getInput());
                }
            }
            case "2" -> {
                // Display catalogue with recommended categories
                this.database.getRecommendedCategories();
                System.out.println("Votre choix :");
                int idCategory = Integer.parseInt(getInput());
                this.database.getProductList(idCategory);
            }
            case "3" -> {
                menuUserInput();
            }
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
