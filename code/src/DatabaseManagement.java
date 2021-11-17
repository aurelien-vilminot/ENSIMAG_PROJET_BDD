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
            case "1" -> {
                database.dropTables();
                main(args);
            }
            case "2" -> {
                database.resetTables();
                main(args);
            }
            case "3" -> {
                database.createTable();
                main(args);
            }
            default -> main(args);
        }
    }
}
