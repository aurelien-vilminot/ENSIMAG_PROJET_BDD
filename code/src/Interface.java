import java.util.*;
import java.util.stream.Collectors;

public class Interface {
    // String menu template : {"• Title •", "element1", ""element2"}
    private final ArrayList<String> menuItems = new ArrayList<>(Arrays.asList("• MENU PRINCIPAL •", "Parcours catalogue", "Droit à l'oubli"));
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
        while (true) {
            System.out.println("• CONNEXION UTILISATEUR •");
            System.out.println("Saisir votre e-mail :");
            this.userMail = getInput();
            System.out.println("Saisir votre mot de passse :");
            this.userPwd = getInput();
            if (this.database.userConnection(this.userMail, this.userPwd)) {
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
                this.database.forgetRight(this.userMail);
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
                    .stream().map(Database.ProductSummary::name).collect(Collectors.toList())); // get only the name
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
                } else if (input > nbSubCategories) {
                    // The user wants to display a product
                    // Backup the name of the product categorie
                    backupPath(nameSubCategory);
                    displayProduct(productList.get(input - nbSubCategories - 1).id());
                } else {
                    // The user input is indexed starting from 1:
                    // subtract 1 to get the correct category index
                    displayCategories(subCategories.get(input - 1));
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // If the input is not correct, re-display the same categories
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
        if (this.pathOfCategorie.size() == 1) {
            catalogueMenuUserInput();
            return;
        } else if (this.pathOfCategorie.size() == 2) {
            this.pathOfCategorie.remove(this.pathOfCategorie.size() - 1);
            displayCategories(null);
            return;
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
                .stream().map(Database.ProductSummary::name).collect(Collectors.toList()));
        menuProductList.add("Retour");
        menuShow(menuProductList);
        try {
            int input = Integer.parseInt(getInput());
            if (input == menuProductList.size()-1) {
                backToPrecCategory();
                return;
            }
            // User input is indexed starting at 1
            int productId = productList.get(input - 1).id();
            // Backup the name of the product categorie
            this.pathOfCategorie.add(nameCategory);
            displayProduct(productId);
        } catch (NumberFormatException |IndexOutOfBoundsException e) {
            // If the input is not correct, re-display the same products
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

        if (this.database.isProductAvailable(productId)) {
            menuShow(this.bidItems);
            switch (getInput()) {
                case "1" -> askForOffer(Float.parseFloat(product.get(1)), productId);
                case "2" -> backToPrecCategory();
                default -> displayProduct(productId);
            }
        } else {
            menuShow(new ArrayList<>(Arrays.asList("Retour", "Oui")));
            if ("1".equals(getInput())) {
                backToPrecCategory();
            } else {
                displayProduct(productId);
            }
        }

    }

    public void displayRecommanded() {
        ArrayList<String> recommendedCategories = this.database.getRecommendedCategories(idCompte);
        recommendedCategories.add("Retour");
        recommendedCategories.add(0, "• CATEGORIES RECOMMANDEES •");
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
                // TODO: gérer pour que le retour revienne dans cette fonction
                displayCategories(recommendedCategories.get(input-1));
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            // If the input is not correct, re-display the same categories
            displayRecommanded();
        }
    }

    public void askForOffer(float currentPrice, int productId) {
        while (true) {
            System.out.println("Entrez un prix:");
            try {
                float newPrice = Float.parseFloat(getInput());
                if (newPrice < currentPrice) {
                    System.out.println("Le prix proposé est inférieur au prix courant: " + currentPrice);
                } else {
                    Offer offer = new Offer(newPrice, productId, this.idCompte);
                    boolean isOfferWin = offer.insertOffre(this.database);
                    System.out.println("Bravo, l'enchère a été effectuée !");
                    if (isOfferWin) {
                        System.out.println("Bravo, vous êtes le gagnant de cette enchère !");
                    }
                    break;
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // Ask again
                askForOffer(currentPrice, productId);
            } catch (OfferException | ProductNotAvailable e) {
                System.out.println(e.getMessage());
            }
        }

        displayProduct(productId);
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
                displayRecommanded();
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
