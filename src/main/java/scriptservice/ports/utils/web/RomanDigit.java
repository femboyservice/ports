package scriptservice.ports.utils.web;

// from https://stackoverflow.com/questions/48356999/java-conversion-of-arabic-numbers-to-roman-numerals -> slightly edited
public class RomanDigit {
    public static String romanDigit(int n, String one, String five, String ten){
        if (n >= 1) {
            if(n == 1) {
                return one;
            } else if (n == 2) {
                return one + one;
            } else if (n == 3) {
                return one + one + one;
            } else if (n==4) {
                return one + five;
            } else if (n == 5) {
                return five;
            } else if (n == 6) {
                return five + one;
            } else if (n == 7) {
                return five + one + one;
            } else if (n == 8) {
                return five + one + one + one;
            } else if (n == 9) {
                return one + ten;
            }

        }
        return "";
    }

    public static String convert(int number) {
        if (number < 0 || number > 3999) {
            return "?"; // cannot be converted
        }

        String romanOnes = romanDigit(number % 10, "I", "V", "X");
        number /= 10;

        String romanTens = romanDigit(number % 10, "X", "L", "C");
        number /= 10;

        String romanHundreds = romanDigit(number % 10, "C", "D", "M");
        number /= 10;

        String romanThousands = romanDigit(number % 10, "M", "", "");
        // number /= 10;

        return (romanThousands + romanHundreds + romanTens + romanOnes);
    }

}