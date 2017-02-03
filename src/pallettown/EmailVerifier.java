package pallettown;

import javax.mail.*;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.Properties;

import static pallettown.GUI.Log;

/**
 * Created by Paris on 20/01/2017.
 */

public class EmailVerifier {

    static Properties mailServerProperties = new Properties();
    static Session getMailSession;

    public static Store store;

    public static Folder trash;

    private static final String GMAIL_HOST = "imap.avMail.com";
//    private static final String GMAIL_PORT = "993";

    private static final String HOTMAIL_HOST = "imap-mail.outlook.com";
//    private static final String HOTMAIL_PORT = ""
    private static Folder inbox;

    private static final Flags deleted = new Flags(Flags.Flag.DELETED);

    public static void verify(String email, String emailPass, int accounts, int retries) {

        String host;
//        String port;

        if(email.contains("@avMail.com")){
            host = GMAIL_HOST;
//            port = "993";
        }else if (email.contains("@hotmail.com")){
            host = HOTMAIL_HOST;
        }else{
            Log("invalid email, please use hotmail or avMail");
            return;
        }

        mailServerProperties.put("mail.imap.host", host);
        mailServerProperties.put("mail.imap.port", "993");
        mailServerProperties.put("mail.imap.starttls.enable", "true");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);

        try {
            store = getMailSession.getStore("imaps");
            store.connect(host, email, emailPass);

            // opens the inbox folder
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            if(host.equals(GMAIL_HOST))
                trash = store.getFolder("[Gmail]/Trash");
            else
                trash = store.getFolder("Deleted");
            trash.open(Folder.READ_WRITE);


            Log("logged in to: " + email);

            // creates a search criterion
            SearchTerm searchCondition = new SearchTerm() {
                @Override
                public boolean match(Message message) {
                    try {
                        if (message.getSubject().contains("Activation")) {
                            return true;
                        }
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            };

            // performs search through the folder
            Message[] foundMessages = inbox.search(searchCondition);

            processMail(foundMessages);

            if(foundMessages.length < accounts){
                //max 5 retries
                Log("Not all verification emails received in time");
                Log("Waiting another 3 minutes then trying again");
                inbox.close(true);
                store.close();

                Thread.sleep(180000);

                if(retries > 0 )
                    verify(email,emailPass,accounts - foundMessages.length, retries - 1);
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static void processMail(Message[] foundMessages) {
        Log("Processing " +foundMessages.length + " emails");


        if(foundMessages.length == 0){
            Log("no emails found");
            return;
        }

        for (Message message : foundMessages) {
            try {
//                assert message.getFrom().equals("noreply@pokemon.com");

                String content = getMailText(message);

                int validkey_index = content.indexOf("https://club.pokemon.com/us/pokemon-trainer-club/activated/");

                if(validkey_index != -1){
                    String validlink = content.substring(validkey_index,validkey_index+93);
                    validlink = validlink.replace("\r","");
                    validlink = validlink.replace("\n","");
                    validlink = validlink.replace(">","");
                    validlink = validlink.replace("=","");

                    Log(validlink);
                    String validate_response = "Failed";

                    while(validate_response.equals("Failed")){
                        validate_response = UrlUtil.openUrl(validlink, true);
                    }

                    Log(validate_response);

                    Log("Verified email and trashing, validate key: " + validlink.substring(60) + "\n");

                    Message[] messageArr = new Message[] {message};

                    inbox.copyMessages(messageArr,trash);

                    inbox.setFlags(messageArr,deleted,true);

                }

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

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
