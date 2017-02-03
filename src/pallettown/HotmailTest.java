package pallettown;

import javax.mail.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Owner on 2/02/2017.
 */
public class HotmailTest {

    static Properties mailServerProperties = new Properties();
    static Session getMailSession;

    public static void main(String[] args) {
//        fetch("imap-mail.outlook.com","miminpari@hotmail.com","mynovskeybringsem9");

        EmailVerifier.verify("miminpari@hotmail.com","mynovskeybringsem9",1,5);
    }

    public static void fetch(String host, String user,
                             String password) {
        try {
            // create properties field
            mailServerProperties.put("mail.imap.host", host);
            mailServerProperties.put("mail.imap.port", "993");
            mailServerProperties.put("mail.imap.starttls.enable", "true");
            getMailSession = Session.getDefaultInstance(mailServerProperties, null);

            // create the POP3 store object and connect with the pop server
            Store store = getMailSession.getStore("imaps");

            store.connect(host, user, password);

            // create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));

            // retrieve the messages from the folder in an array
            Message[] messages = emailFolder.getMessages();

            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                System.out.println(getMailText(message));
                String line = reader.readLine();
                if ("QUIT".equals(line)) {
                    break;
                }
            }

            // close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * This method checks for content-type
    * based on which, it processes and
    * fetches the content of the message
    */
    public static String getMailText(Part p) throws Exception {

        //check if the content is plain text
        if (p.isMimeType("text/plain")) {
            return((String) p.getContent());
        }
        //check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
                return getMailText(mp.getBodyPart(i));
        }

        return "";
    }
}
