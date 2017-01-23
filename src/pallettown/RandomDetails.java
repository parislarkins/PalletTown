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
    public static final Set<String> INITIAL_CONSONANTS = new HashSet<>(Arrays.asList(INITIAL));

    public static final String[] FINAL = new String[]{
            "ch", "ft", "nd", "sh", "nk", "pt", "st",
            "ng", "ct", "b", "d", "g", "f", "ss", "h",
            "k", "sp", "m", "l", "n", "sk", "p", "r",
            "t", "w", "v", "y", "z", "nt", "mp"};
    public static final Set<String> FINAL_CONSONANTS = new HashSet<>(Arrays.asList(FINAL));

    public static final String[] vowels = new String[]{
            "a","e","i","o","u"
    };
    static String randomBirthday() {
        GregorianCalendar gc = new GregorianCalendar();

        int year = randBetween(1900, 2000);

        gc.set(Calendar.YEAR, year);

        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

        gc.set(Calendar.DAY_OF_YEAR, dayOfYear);

        return (gc.get(Calendar.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH));
    }

    private static String randomUsername() {
        return randomWords(3);
    }

    private static String randomWords(int words) {
        String randomWords = "";
        for (int i = 0; i < words; i++) {
            randomWords += randomWord();
        }

        return randomWords;
    }

    private static String randomWord(){
        return "not implemented";
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
}
