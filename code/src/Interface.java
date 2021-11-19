import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Interface {
    // String menu template : {"• Title •", "element1", ""element2"}
    private final ArrayList<String> menuItems = new ArrayList<>(Arrays.asList("• MENU PRINCIPAL •", "Parcours catalogue", "Droit à l'oubli"));
    private final ArrayList<String> catalogeMenuItems = new ArrayList<>(Arrays.asList("• MENU CATALOGUES •", "Catalague produits", "Catégories recommandées", "Retour"));
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
                System.out.println("Déconnection");
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

            // Init the title menu
            ArrayList<String> arrayToDisplay = new ArrayList<>();
            if (nameSubCategory != null) {
                arrayToDisplay.add("• Catégorie : " + nameSubCategory + " •");
            } else {
                arrayToDisplay.add("• CATÉGORIES/PRODUITS •");
            }

            ArrayList<String> subCategories = this.database.getCatalog(nameSubCategory);
            ArrayList<String> subCategoriesProducts = this.database.getProductList(nameSubCategory);
            if (subCategories.size() == 0) {
                displayProductList(nameSubCategory);
                return;
            }
            // Add products to the list
            subCategories.addAll(subCategoriesProducts);
            subCategories.add("Retour");
            arrayToDisplay.addAll(subCategories);
            menuShow(arrayToDisplay);
            int backId = arrayToDisplay.size() - 1;

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
        // If the user is at the first categories, back to catalogue menu
        if (this.pathOfCategorie.size() == 1) {
            catalogueMenuUserInput();
        }

        // Else back to the previous category
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
