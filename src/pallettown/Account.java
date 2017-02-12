package pallettown;

/**
 * Created by Owner on 11/02/2017.
 */
public class Account {

    String dob;
    String user;
    String pass;
    public String Csrf;
    public String Country;
    public String CaptchaId;
    public String CaptchaResponse;
    public String email;

    public Account(String dob, String user, String pass, String email, String country){
        this.dob = dob;
        this.user = user;
        this.pass = pass;
        this.email = email;
        this.Country = country;
    }

}
