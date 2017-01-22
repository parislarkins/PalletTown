package pallettown;

import javax.mail.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class GmailInbox {

    public static void fetch(String pop3Host, String storeType, String user,
                             String password) {
        try {
            // create properties field
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", pop3Host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            // create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");

            store.connect(pop3Host, user, password);

            // create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));

            // retrieve the messages from the folder in an array
            Message[] messages = emailFolder.getMessages();

            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                getMailText(message);
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