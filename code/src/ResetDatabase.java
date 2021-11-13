public class ResetDatabase {
    public static void main(String[] args) {
        Database database = new Database();
        database.resetTable();
        database.fulfillTable();
    }
}
