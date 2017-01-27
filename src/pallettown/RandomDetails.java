package pallettown;

import java.util.*;

/**
 * Created by Paris on 23/01/2017.
 */
public class RandomDetails {

    public static final String[] INITIAL = new String[] {
        "fr","ch","gr","cl","bl","tr","sl","sh","sm",
        "br","st","cr","gl","fl","sp","pr","sk","b",
        "d","g","f","h","k","j","m","l","n","dr","p",
        "s","r","t","w","v","y","str","z","sw","pl","sn"};

    public static final String[] FINAL = new String[]{
        "ch", "ft", "nd", "sh", "nk", "pt", "st",
        "ng", "ct", "b", "d", "g", "f", "ss", "h",
        "k", "sp", "m", "l", "n", "sk", "p", "r",
        "t", "w", "v", "y", "z", "nt", "mp"};

    public static final String[] VOWELS = new String[]{
        "a","e","i","o","u"
    };

    public static final char[] SYMBOLS = new char[]{
        '#','?','!','@','$','%','^','&','>','<','+','`','*','(',')','-',']'
    };

    static String randomBirthday() {
        GregorianCalendar gc = new GregorianCalendar();

        int year = randBetween(1900, 2000);

        gc.set(Calendar.YEAR, year);

        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

        gc.set(Calendar.DAY_OF_YEAR, dayOfYear);

        return (gc.get(Calendar.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH));
    }

    static String randomUsername() {
        return randomWords(3);
    }

    static String randomPassword() {
        String base = randomWords(3);

        char[] baseArr = base.toCharArray();

        int capitalIndex = randBetween(0,base.length()-1);
        baseArr[capitalIndex] = Character.toUpperCase(baseArr[capitalIndex]);

        int numIndex = capitalIndex;
        while(numIndex == capitalIndex) {
            numIndex = randBetween(0, base.length() - 1);
        }
        baseArr[numIndex] = Character.forDigit(randBetween(0,9),10);

        int symbolIndex = numIndex;
        while(symbolIndex == numIndex || symbolIndex == capitalIndex) {
            symbolIndex = randBetween(0, base.length() - 1);
        }
        baseArr[symbolIndex] = SYMBOLS[randBetween(0,SYMBOLS.length-1)];

        return new String(baseArr);
    }

    private static String randomWords(int words) {
        String randomWords = "";
        for (int i = 0; i < words; i++) {
            randomWords += randomWord();
        }

        return randomWords;
    }

    private static String randomWord(){
        String init = INITIAL[randBetween(0,INITIAL.length-1)];
        String vowel = VOWELS[randBetween(0,VOWELS.length-1)];
        String fin = FINAL[randBetween(0,FINAL.length-1)];
        return init+vowel+fin;
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            String pw = randomPassword();
            System.out.println(randomUsername() + ":" + pw);
            System.out.println(PalletTown.validatePass(pw));
        }
    }
}
