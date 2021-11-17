import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseManagement {
    private final ArrayList<String> menuItems = new ArrayList<>(Arrays.asList(
            "• GESTION DE LA BASE DE DONNÉES •",
            "Supprimer les tables",
            "Remettre à zéro les tables",
            "Créer les tables",
            "Insérer un jeu de test (suppresion, création, insertion)",
            "Quitter"
    ));

    public static void main(String[] args) {
        DatabaseManagement databaseManagement = new DatabaseManagement();
        Database database = new Database();

        Interface.menuShow(databaseManagement.menuItems);
        switch (Interface.getInput()) {
            case "1" -> {
                database.dropTables();
                main(args);
            }
            case "2" -> {
                database.resetTables();
                main(args);
            }
            case "3" -> {
                database.createTables();
                main(args);
            }
            case "4" -> {
                database.dropTables();
                database.createTables();
                database.fillTables();
                main(args);
            }
            case "5" -> System.exit(0);
            default -> main(args);
        }
    }
}
