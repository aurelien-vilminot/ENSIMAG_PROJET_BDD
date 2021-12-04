import java.util.*;

public class Interface {
    // String menu template : {"• Title •", "element1", ""element2"}
    private final ArrayList<String> menuItems = new ArrayList<>(Arrays.asList("• MENU PRINCIPAL •", "Parcours catalogue", "Droit à l'oubli", "Quitter"));
    private final ArrayList<String> catalogeMenuItems = new ArrayList<>(Arrays.asList("• MENU CATALOGUES •", "Catalogue produits", "Catégories recommandées", "Retour"));
    private final ArrayList<String> yesNoItems = new ArrayList<>(Arrays.asList("• SUPPRESSION DÉFINITIVE •", "Oui", "Non"));
    private final ArrayList<String> bidItems = new ArrayList<>(Arrays.asList("Voulez-vous faire une offre sur ce produit ?", "Oui", "Non"));

    private String userMail;
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
        clearScreen();
        this.pathOfCategorie.clear();
        while (true) {
            System.out.println("• CONNEXION UTILISATEUR •");
            System.out.println("Saisir votre e-mail :");
            this.userMail = getInput();
            System.out.println("Saisir votre mot de passse :");
            this.userPwd = getInput();
            if (this.database.userConnection(this.userMail, this.userPwd)) {
                this.idCompte = this.database.getIdCompte(this.userMail);
                break;
            }
            clearScreen();
            System.out.println("! Échec de la connexion !");
        }
        clearScreen();
    }

    public static String getInput() {
        Scanner userInput = new Scanner(System.in);
        return userInput.next();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
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
        clearScreen();
        this.pathOfCategorie.clear();
        menuShow(this.menuItems);

        switch (getInput()) {
            case "1" -> {
                // Display catalogue menu
                catalogueMenuUserInput();
            }
            case "2" -> forgetRights();
            case "3" -> {
                clearScreen();
                System.out.println("Déconnection");
                this.isRunning = false;
                database.closeConnection();
                System.exit(0);
            }
            default -> menuUserInput();
        }
    }

    public void forgetRights() {
        clearScreen();
        System.out.println("Voulez-vous vraiment appliquer le droit à l'oubli ?\n" +
                "Cela supprimera totalement vos informations personnelles.");
        menuShow(this.yesNoItems);

        switch (getInput()) {
            case "1" -> {
                // Delete user information
                this.database.forgetRight(this.userMail);
                clearScreen();
                System.out.println("Déconnection");
                this.isRunning = false;
                userConnection();
                menuUserInput();
            }
            case "2" -> menuUserInput();
            default -> forgetRights();
        }
    }

    public void displayCategories(String nameSubCategory) {
        while (true) {
            backupPath(nameSubCategory);

            // Init the title menu
            ArrayList<String> arrayToDisplay = new ArrayList<>();
            if (nameSubCategory != null) {
                arrayToDisplay.add("• Catégorie : " + nameSubCategory + " •");
            } else {
                arrayToDisplay.add("• CATÉGORIES/PRODUITS •");
            }

            ArrayList<String> subCategories = this.database.getCatalog(nameSubCategory);
            int nbSubCategories = subCategories.size();
            // Get product list
            List<Database.ProductSummary> productList = this.database.getProductList(nameSubCategory);

            if (subCategories.size() == 0) {
                displayProductList(nameSubCategory);
                return;
            }

            // Add products to the list
            subCategories.addAll(productList
                    .stream().map(Database.ProductSummary::name).toList()); // get only the name
            subCategories.add("Retour");
            arrayToDisplay.addAll(subCategories);
            menuShow(arrayToDisplay);
            int backId = arrayToDisplay.size() - 1;

            try {
                int input = Integer.parseInt(getInput());
                if (input == backId) {
                    // Back to the precedent category
                    clearScreen();
                    backToPrecCategory();
                    return;
                } else if (input > nbSubCategories) {
                    // The user wants to display a product
                    // Backup the name of the product category
                    int productId = productList.get(input - nbSubCategories - 1).id();
                    backupPath(String.valueOf(productId));
                    clearScreen();
                    displayProduct(productId);
                } else {
                    // The user input is indexed starting from 1:
                    // subtract 1 to get the correct category index
                    clearScreen();
                    displayCategories(subCategories.get(input - 1));
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // If the input is not correct, re-display the same categories
                clearScreen();
                displayCategories(nameSubCategory);
            }
        }
    }

    private void backupPath(String categoryName) {
        if (categoryName == null || categoryName.equals("")) {
            categoryName = "menu";
        }
        
        if (!this.pathOfCategorie.contains(categoryName)) {
            // Add the name of current category to remember the path
            this.pathOfCategorie.add(categoryName);
        }
    }

    public void backToPrecCategory() {
        // If the user is at the first categories, back to catalogue menu
        if (this.pathOfCategorie.get(0).equals("menu")) {
            if (this.pathOfCategorie.size() == 1) {
                catalogueMenuUserInput();
                return;
            } else if (this.pathOfCategorie.size() == 2) {
                this.pathOfCategorie.remove(this.pathOfCategorie.size() - 1);
                displayCategories(null);
                return;
            }
        } else if (this.pathOfCategorie.get(0).equals("recommandations")) {
            if (this.pathOfCategorie.size() <= 2) {
                this.pathOfCategorie.remove(this.pathOfCategorie.size() - 1);
                displayRecommanded();
                return;
            }
        }

        // Else back to the previous category
        this.pathOfCategorie.remove(this.pathOfCategorie.size() - 1);
        displayCategories(this.pathOfCategorie.get(this.pathOfCategorie.size() - 1));
    }

    public void displayProductList(String nameCategory) {
        backupPath(nameCategory);
        // Display products
        List<Database.ProductSummary> productList = this.database.getProductList(nameCategory);
        ArrayList<String> menuProductList = new ArrayList<>();
        menuProductList.add("Veuillez sélectionner un produit :");
        menuProductList.addAll(productList
                .stream().map(Database.ProductSummary::name).toList());
        menuProductList.add("Retour");
        menuShow(menuProductList);
        try {
            int input = Integer.parseInt(getInput());
            if (input == menuProductList.size()-1) {
                clearScreen();
                backToPrecCategory();
                return;
            }
            // User input is indexed starting at 1
            int productId = productList.get(input - 1).id();
            // Backup the name of the product categorie
            this.pathOfCategorie.add(nameCategory);
            clearScreen();
            displayProduct(productId);
        } catch (NumberFormatException |IndexOutOfBoundsException e) {
            // If the input is not correct, re-display the same products
            clearScreen();
            displayProductList(nameCategory);
        }
    }

    public void displayProduct(int productId) {
        ArrayList<String> product = this.database.getProduct(productId);
        HashMap<String, String> caracProd = this.database.getCaracProd(productId);
        System.out.println("• Produit : "+ product.get(0)+" •");
        System.out.println("Prix: "+ product.get(1));
        System.out.println("Description: "+ product.get(2));
        System.out.println("URL: " + product.get(3));
        System.out.println("Catégorie: " + product.get(4));
        System.out.println("Caractéristiques :");
        caracProd.forEach((key, value) -> System.out.println("\t" + key + " : " + value));

        menuShow(this.bidItems);
        switch (getInput()) {
            case "1" -> askForOffer(productId, Float.parseFloat(product.get(1)));
            case "2" -> {
                clearScreen();
                backToPrecCategory();
            }
            default -> {
                clearScreen();
                displayProduct(productId);
            }
        }
    }

    public void displayRecommanded() {
        clearScreen();
        ArrayList<String> recommendedCategories = this.database.getRecommendedCategories(idCompte);
        recommendedCategories.add("Retour");
        recommendedCategories.add(0, "• CATÉGORIES RECOMMANDÉES •");
        menuShow(recommendedCategories);
        int backId = recommendedCategories.size() - 1;
        try {
            int input = Integer.parseInt(getInput());
            if (input == backId) {
                // Return
                catalogueMenuUserInput();
                return;
            } else {
                // The user input is indexed starting from 1:
                // subtract 1 to get the correct category index
                backupPath("recommandations");
                displayCategories(recommendedCategories.get(input));
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            // If the input is not correct, re-display the same categories
            clearScreen();
            displayRecommanded();
        }
    }

    public void askForOffer(int productId, float currentPrice) {
        while (true) {
            try {
                System.out.println("Entrez un prix:");
                float newPrice = Float.parseFloat(getInput());
                if (newPrice <= currentPrice) {
                    clearScreen();
                    System.out.println("Le prix proposé est inférieur au prix courant: " + currentPrice);
                } else {
                    try {
                        Offer offer = new Offer(newPrice, productId, this.idCompte);
                        boolean isOfferWin = offer.insertOffre(this.database);
                        clearScreen();
                        System.out.println("Bravo, l'enchère a été effectuée !");
                        if (isOfferWin) {
                            System.out.println("Bravo, vous êtes le gagnant de cette enchère !");
                            backToPrecCategory();
                            return;
                        }
                        break;
                    } catch (IllegalAccessError e) {
                        System.out.println("Vous ne pouvez pas faire un enchère, le produit n'existe plus !");
                        backToPrecCategory();
                        return;
                    } catch (IllegalStateException e) {
                        clearScreen();
                        System.out.println("Une enchère a été réalisée entre temps, veuillez réessayer !");
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                // Ask again
                askForOffer(productId, currentPrice);
                return;
            }
        }
        displayProduct(productId);
    }

    public void catalogueMenuUserInput() {
        clearScreen();
        this.pathOfCategorie.clear();
        menuShow(this.catalogeMenuItems);

        switch (getInput()) {
            case "1" -> {
                // Display catalogue
                clearScreen();
                displayCategories(null);
            }
            case "2" -> {
                // Display catalogue with recommended categories
                clearScreen();
                displayRecommanded();
            }
            case "3" -> {
                clearScreen();
                menuUserInput();
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
