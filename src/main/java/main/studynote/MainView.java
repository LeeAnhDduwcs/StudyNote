package main.studynote;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainView extends Application {
    public static String srcPath = "D:\\LAD\\Subject";
    public static String htmlPath = "D:\\Program Files\\Intellij\\My Program\\StudyNote\\src\\main\\java\\main\\studynote\\htmlPattern.txt";
    public static List<String> htmlStrings = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("StudyNote");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new File(MainView.htmlPath));
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append('\n');
            }
            scanner.close();
            int start = 0;
            int end = sb.indexOf("|");
            while (end != -1) {
                htmlStrings.add(sb.substring(start, end));
                start = end + 1;
                end = sb.indexOf("|", start + 1);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        launch();

        try {
            FileWriter fileWriter = new FileWriter(htmlPath);
            for (String s : htmlStrings) {
                fileWriter.write(s + '|');
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}