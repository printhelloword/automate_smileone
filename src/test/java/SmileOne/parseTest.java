package SmileOne;

public class parseTest {
    public static void main(String[] args) {
        String input = "12345678901234";
//        String input2 = "126606687263";

//        System.out.println(Long.parseLong(input));
//        System.out.println(input.length());

        if (input.length()<14){
            System.out.println("Player Id "+input.substring(0, input.length()-4));
            System.out.println("Zone Id " +input.substring(input.length()-4));
        }else if (input.length()>13){
            System.out.println("Player Id "+input.substring(0, input.length()-5));
            System.out.println("Zone Id " +input.substring(input.length()-5));
        }


    }
}
