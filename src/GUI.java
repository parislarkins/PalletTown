import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Paris on 20/01/2017.
 */
public class GUI extends Application{


    private static final int VIEWER_WIDTH = 500;
    private static final int VIEWER_HEIGHT = 500;

    private Group root = new Group();

    static Group controls = new Group();
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Pikaptcha GUI");
        Scene scene = new Scene(root);

        root.getChildren().add(controls);

        makeControls();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void makeControls() {

        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.setPadding(new Insets(15,15,15,15));
        vb.setAlignment(Pos.CENTER);

        Label plusMaillabel = new Label("Email:");

        final TextField plusMail = new TextField();
        plusMail.setPromptText("Enter plusmail compatible email (no gmail)");
        plusMail.setPrefWidth(350);

        HBox mail = new HBox();
        mail.setAlignment(Pos.CENTER_RIGHT);
        mail.getChildren().addAll(plusMaillabel, plusMail);
        mail.setSpacing(10);

        vb.getChildren().add(mail);

        Label userLabel = new Label("Username:");

        final TextField userName = new TextField();
        userName.setPromptText("Enter account username");
        userName.setPrefWidth(350);
        HBox user = new HBox();
        user.setAlignment(Pos.CENTER_RIGHT);
        user.getChildren().addAll(userLabel, userName);
        user.setSpacing(10);

        vb.getChildren().add(user);

        Label passLabel = new Label("Password:");

        final TextField password = new TextField();
        password.setPrefWidth(350);
        password.setPromptText("Enter account password to use");
        HBox pass = new HBox();
        pass.setAlignment(Pos.CENTER_RIGHT);
        pass.getChildren().addAll(passLabel,password);
        pass.setSpacing(10);

        vb.getChildren().add(pass);

        Label numLabel = new Label("Number of accounts:");
        final TextField numAccounts = new TextField();
        numAccounts.setPrefWidth(350);
        numAccounts.setPromptText("Number of accounts to create.");
        numAccounts.setText("1");
        HBox num = new HBox();
        num.setAlignment(Pos.CENTER_RIGHT);
        num.getChildren().addAll(numLabel,numAccounts);
        num.setSpacing(10);

        vb.getChildren().add(num);

        Label startLabel = new Label("Start number:");
        final TextField startNum = new TextField();
        startNum.setPrefWidth(350);
        startNum.setPromptText("Starting number");
        HBox start = new HBox();
        start.setAlignment(Pos.CENTER_RIGHT);
        start.getChildren().addAll(startLabel,startNum);
        start.setSpacing(10);

        vb.getChildren().add(start);

        Label captchaLabel = new Label("2Captcha Key:");
        final TextField captchaKey = new TextField();
        captchaKey.setPrefWidth(350);
        captchaKey.setPromptText("Enter 2Captcha Key");
        HBox captcha = new HBox();
        captcha.setAlignment(Pos.CENTER_RIGHT);
        captcha.getChildren().addAll(captchaLabel,captchaKey);
        captcha.setSpacing(10);

        vb.getChildren().add(captcha);

        CheckBox autoVerify = new CheckBox("Auto Verify Accounts");
        vb.getChildren().add(autoVerify);

        Label gmailLabel = new Label("Gmail Account:");

        final TextField gmail = new TextField();
        gmail.setPromptText("Gmail account for auto verification");
        gmail.setPrefWidth(350);

        HBox gm = new HBox();
        gm.setAlignment(Pos.CENTER_RIGHT);
        gm.getChildren().addAll(gmailLabel, gmail);
        gm.setSpacing(10);

        vb.getChildren().add(gm);

        Label gmPassLabel = new Label("Gmail Password:");

        final TextField gmailPass = new TextField();
        gmailPass.setPromptText("Gmail account password for auto verification");
        gmailPass.setPrefWidth(350);

        HBox gmPw = new HBox();
        gmPw.setAlignment(Pos.CENTER_RIGHT);
        gmPw.getChildren().addAll(gmPassLabel, gmailPass);
        gmPw.setSpacing(10);

        vb.getChildren().add(gmPw);

        CheckBox acceptTos = new CheckBox("Accept account TOS");
        vb.getChildren().add(acceptTos);

        ArrayList<String> extentions = new ArrayList<>();
        extentions.add("*.txt");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select output file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text File", extentions)
        );
        fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));

        Label outputLabel = new Label("Output File:");

        final TextField outputFile = new TextField();
        outputFile.setPrefWidth(350);

        HBox output = new HBox();
        output.setAlignment(Pos.CENTER_RIGHT);
        output.getChildren().addAll(outputLabel, outputFile);
        output.setSpacing(10);

        vb.getChildren().add(output);

        File[] file = new File[1];

        outputFile.setOnMouseClicked(event -> {
            file[0] = fileChooser.showOpenDialog(primaryStage);
            if (file[0] != null) {
                outputFile.setText(file[0].getAbsolutePath());
            }
        });

        Button submit = new Button("Create accounts");
        submit.setOnAction(event -> Pikaptcha.Start());
        vb.getChildren().add(submit);

        controls.getChildren().add(vb);
    }
}
