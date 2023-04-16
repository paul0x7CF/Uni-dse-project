package msExchange;

public class Main {
    public static <String> void main(String[] args) {
        boolean duplicated=false;
        if ("-s".equals(args[0])) {
            duplicated = true;
        }
        MSExchange msExchange = new MSExchange(duplicated);
    }
}
