import java.util.Scanner;

public class Buscador {
    public static void main(String[] args) {
        
        DatabaseManager.initializeDatabase();

        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese el título del libro que desea buscar:");
        String bookTitle = scanner.nextLine();

        
        BookService.searchAndRegisterBook(bookTitle);
    }
}
