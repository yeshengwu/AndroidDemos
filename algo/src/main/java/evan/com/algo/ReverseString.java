package evan.com.algo;

/**
 * Created by shidu on 19/4/7.
 *
 */

public class ReverseString {

    public char[] reverse(char[] source) {
        int i = 0, j = source.length - 1;
        while (i < j) {
            char tmp = source[j];
            source[j] = source[i];
            source[i] = tmp;
            i++;
            j--;
        }
        return source;
    }

    public int strToInt(String str) {
        if (str == null || str.length() == 0)
            return 0;

        boolean isNegtive = str.charAt(0) == '-';
        int ret = 0;
        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);

            if (i == 0 && (item == '+' || item == '-')) //符号判定
                continue;
            if (item < '0' || item > '9')
                return 0;

            ret = ret * 10 + (item - '0');
        }

        return isNegtive ? -ret : ret;
    }

    public static void main(String[] args){
        final ReverseString example  = new ReverseString();
//        char[] srr = {'h','e','l','l','o'};
        char[] srr = {'H','a','n','n','a','h'};
        char[] out = example.reverse(srr);
        for (char i: out) {
            System.out.println(i);
        }
//        int strToInt = example.strToInt("4");
        int strToInt = example.strToInt("22344555676788854444");
        System.out.println(strToInt);

        int parseInt = Integer.parseInt("1");
        int parseInt2 = Integer.parseInt("21");
        System.out.println("parseInt = "+parseInt+" parseInt2 = "+parseInt2);

        System.out.println("取反 ~0 =  "+(~0)+" 取反 ~1= "+(~1));
    }
}
