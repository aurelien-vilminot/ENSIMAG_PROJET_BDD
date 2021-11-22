import java.util.*;
import java.util.stream.Collectors;

public class Interface {
    // String menu template : {"• Title •", "element1", ""element2"}
    private final ArrayList<String> menuItems = new ArrayList<>(Arrays.asList("• MENU PRINCIPAL •", "Parcours catalogue", "Droit à l'oubli"));
    private final ArrayList<String> catalogeMenuItems = new ArrayList<>(Arrays.asList("• MENU CATALOGUES •", "Catalogue produits", "Catégories recommandées", "Retour"));
    private final ArrayList<String> yesNoItems = new ArrayList<>(Arrays.asList("• SUPPRESSION DÉFINITIVE •", "Oui", "Non"));
    private final ArrayList<String> bidItems = new ArrayList<>(Arrays.asList("Voulez-vous faire une offre sur ce produit ?", "Oui", "Non"));

    private String userId;
    private String userPwd;
    private int idCompte;

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
                this.idCompte = this.database.getIdCompte(this.userMail);
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
            List<String> subCategoriesProducts =
                    this.database.getProductList(nameSubCategory)
                            .stream().map(Database.ProductSummary::name) // get only the name
                            .collect(Collectors.toList());
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
                    // The user input is indexed starting from 1:
                    // subtract 1 to get the correct category index
                    displayCategories(subCategories.get(input - 1));
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
        this.pathOfCategorie.remove(this.pathOfCategorie.size() - 1);
        int precCategoryID = this.pathOfCategorie.size() - 1;
        String nameSubCategory = this.pathOfCategorie.get(precCategoryID);
        displayCategories(nameSubCategory);
    }

    public void displayProductList(String nameCategory) {
        // Display products
        System.out.println(this.pathOfCategorie);
        List<Database.ProductSummary> productList = this.database.getProductList(nameCategory);
        ArrayList<String> menuProductList = new ArrayList<>();
        menuProductList.add("Veuillez sélectionner un produit :");
        menuProductList.addAll(productList
                .stream().map(Database.ProductSummary::name).collect(Collectors.toList()));
        menuProductList.add("Retour");
        menuShow(menuProductList);
        try {
            int input = Integer.parseInt(getInput());
            if (input == menuProductList.size() - 1) {
                backToPrecCategory();
                return;
            }
            // User input is indexed starting at 1
            int productId = productList.get(input - 1).id();
            ArrayList<String> product = this.database.getProduct(productId);
            displayProduct(product);
        } catch (NumberFormatException e) {
            // If the input is not correct, re-display the same products
            displayProductList(nameCategory);
        }
    }

    public void displayProduct(ArrayList<String> productDetails) {
        // same format as in database
        System.out.println("• Produit: "+ productDetails.get(0)+" •");
        System.out.println("Prix: "+ productDetails.get(1));
        System.out.println("Description: "+ productDetails.get(2));
        System.out.println("URL: " + productDetails.get(3));
        System.out.println("Catégorie: " + productDetails.get(4));
        menuShow(this.bidItems);
        switch (getInput()) {
            case "1" -> {
                System.out.println("Faire une offre");
                //TODO: appeler la fonction d'enchère
            }
            case "2" -> backToPrecCategory();
        }
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
                ArrayList<String> recommendedCategories = this.database.getRecommendedCategories(idCompte);
                System.out.println("Votre choix :");
                this.database.getProductList(recommendedCategories.get(Integer.parseInt(getInput())));
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
