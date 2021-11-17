import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Interface {
    // String menu template : {"• Title •", "element1", ""element2"}
    private final ArrayList<String> menuItems = new ArrayList<>(Arrays.asList("• MENU PRINCIPAL •", "Parcours catalogue", "Droit à l'oubli"));
    private final ArrayList<String> catalogeMenuItems = new ArrayList<>(Arrays.asList("• MENU PRODUITS •", "Catalague produits", "Catégories recommandées", "Retour"));
    private final ArrayList<String> yesNoItems = new ArrayList<>(Arrays.asList("• SUPPRESSION DÉFINITIVE •", "Oui", "Non"));

    private String userId;
    private String userPwd;

    private boolean isRunning;
    private final Database database;

    private ArrayList<String> pathOfCategorie = new ArrayList<>();

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

    public static String getInput() {
        Scanner userInput = new Scanner(System.in);
        return userInput.next();
    }

    public static void clearScreen() {
        // TODO: clear terminal
    }

    public void run() {
        while(isRunning) {
            menuUserInput();
        }
    }

    public static void menuShow(ArrayList<String> menuItems) {
        // Display title
        System.out.println(menuItems.get(0));
        // Display elements
        for (int i = 1; i < menuItems.size() ; ++i) {
            System.out.println( i +") " + menuItems.get(i));
        }
        System.out.println("Votre choix :");
    }

    public void menuUserInput() {
        menuShow(this.menuItems);

        switch (getInput()) {
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

        switch (getInput()) {
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

    public void displayCategories(String nameSubCategory) {
        while (true) {
            if (!this.pathOfCategorie.contains(nameSubCategory)) {
                // Add the id of current category to remember the path
                this.pathOfCategorie.add(nameSubCategory);
            }
            ArrayList<String> subCategories = this.database.getCatalog(nameSubCategory);
            if (subCategories == null) {
                displayProductList(nameSubCategory);
                return;
            }
            subCategories.add("Retour");
            int backId = subCategories.size() - 1;
            System.out.println("Votre choix :");

            try {
                int input = Integer.parseInt(getInput());
                if (input == backId) {
                    // Back to the precedent category
                    backToPrecCategory();
                    return;
                } else {
                    displayCategories(subCategories.get(input));
                }
            } catch (NumberFormatException e) {
                // If the input is not correct, re-display the same categories
                displayCategories(nameSubCategory);
            }
        }
    }

    public void backToPrecCategory() {
        int precCategoryID = this.pathOfCategorie.size() - 1;
        String nameSubCategory = this.pathOfCategorie.get(precCategoryID);
        this.pathOfCategorie.remove(precCategoryID);
        displayCategories(nameSubCategory);
    }

    public void displayProductList(String nameCategory) {
        // Display products
        ArrayList<String> productList = this.database.getProductList(nameCategory);
        ArrayList<String> menuProductList = new ArrayList<>();
        menuProductList.add("Veuillez sélectionner un produit :");
        menuProductList.addAll(productList);
        menuProductList.add("Retour");
        menuShow(menuProductList);
        try {
            int input = Integer.parseInt(getInput());
            if (input == menuProductList.size() - 1) {
                backToPrecCategory();
                return;
            }
            ArrayList<String> productDetails = this.database.getProduct(input);
            if (productDetails == null) {
                displayProductList(nameCategory);
            } else {
                displayProduct(productDetails);
            }
        } catch (NumberFormatException e) {
            // If the input is not correct, re-display the same products
            displayProductList(nameCategory);
        }
    }

    public void displayProduct(ArrayList<String> productDetails) {

    }

    public void catalogueMenuUserInput() {
        menuShow(this.catalogeMenuItems);

        switch (getInput()) {
            case "1" -> {
                // Display catalogue
                displayCategories(null);
            }
            case "2" -> {
                // Display catalogue with recommended categories
                ArrayList<String> recommendedCatagories = this.database.getRecommendedCategories();
                System.out.println("Votre choix :");
                this.database.getProductList(recommendedCatagories.get(Integer.parseInt(getInput())));
            }
            case "3" -> menuUserInput();
            case "quit" -> {
                System.out.println("Déconnection");
                this.isRunning = false;
                userConnection();
            }
            default -> catalogueMenuUserInput();
        }
    }

    public static void main(String[] args) {
        Database database = new Database();
        Interface menu = new Interface(database);
        menu.run();
        database.closeConnection();
    }
}
