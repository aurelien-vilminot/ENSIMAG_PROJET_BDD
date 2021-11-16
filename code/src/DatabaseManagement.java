import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseManagement {
    private final ArrayList<String> menuItems = new ArrayList<>(Arrays.asList(
            "• GESTION DE LA BASE DE DONNÉES •",
            "Supprimer les tables",
            "Remettre à zéro les tables",
            "Créer les tables",
            "Insérer un jeu de test"
    ));

    public static void main(String[] args) {
        DatabaseManagement databaseManagement = new DatabaseManagement();
        Database database = new Database();

        Interface.menuShow(databaseManagement.menuItems);
        switch (Interface.getInput()) {
            case "1" -> database.dropTables();
            case "2" -> database.resetTables();
            case "3" -> database.createTable();
            default -> main(args);
        }

        database.dropTables();
        database.createTable();
    }
}
