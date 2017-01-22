package pallettown;

import javax.mail.*;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Paris on 20/01/2017.
 */

public class GmailVerifier {

    static Properties mailServerProperties = new Properties();
    static Session getMailSession;

    public static Store store;

    public static Folder trash;

    private static final String HOST = "imap.gmail.com";
    private static Folder inbox;

    public static void verify(String gmail, String gmailPass) {

        mailServerProperties.put("mail.imap.host", HOST);
        mailServerProperties.put("mail.imap.port", "993");
        mailServerProperties.put("mail.imap.starttls.enable", "true");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);

        // Step3
        System.out.println("\n\n 3rd ===> Get Session and Send mail");
        try {
            store = getMailSession.getStore("imaps");
            store.connect(HOST, gmail, gmailPass);

            // opens the inbox folder
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            trash = store.getFolder("[Gmail]/Trash");
            trash.open(Folder.READ_WRITE);


            System.out.println("logged in to: " + gmail);

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

            inbox.close(true);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }

    private static void processMail(Message[] foundMessages) {
        System.out.println("Processing " +foundMessages.length + " emails");

        if(foundMessages.length == 0){
            System.out.println("no emails found");
            return;
        }


        for (Message message : foundMessages) {
            try {
//                assert message.getFrom().equals("noreply@pokemon.com");

                String content = GmailInbox.getMailText(message);

                int validkey_index = content.indexOf("https://club.pokemon.com/us/pokemon-trainer-club/activated/");

                if(validkey_index != -1){
                    String validlink = content.substring(validkey_index,validkey_index+93);
                    validlink = validlink.replace("\r","");
                    validlink = validlink.replace("\n","");
                    validlink = validlink.replace(">","");
                    validlink = validlink.replace("=","");

                    System.out.println(validlink);
                    String validate_response = "Failed";

                    while(validate_response.equals("Failed")){
                        validate_response = UrlUtil.openUrl(validlink, true);
                    }

                    System.out.println(validate_response);

                    System.out.println("Verified email and trashing, validate key: " + validlink.substring(60) + "\n");

                    inbox.copyMessages(new Message[] {message},trash);

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
}
