package SmileOne;

public class parseTest {
    public static void main(String[] args) {
        String input = "1266066872632";

        System.out.println(Long.parseLong(input));
        System.out.println(input.length());

        System.out.println("Player Id "+input.substring(0,9));
        System.out.println("Zone Id " +input.substring(9));
    }
}
