package SmileOne;

public class parseTest {
    public static void main(String[] args) {
        String input = "1266066872632";
        String input2 = "126606687263";

//        System.out.println(Long.parseLong(input));
//        System.out.println(input.length());

        System.out.println("Player Id "+input2.substring(0, input2.length()-4));
        System.out.println("Zone Id " +input2.substring(input2.length()-4));
    }
}
