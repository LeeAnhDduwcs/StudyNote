package main.studynote;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ControlMainView implements Initializable {
    private String selectionSubject;
    private String selectionTopic;

    @FXML
    private ListView<String> subjectList;
    private void updateSubjectList() {
        File file = new File(MainView.srcPath);
        subjectList.getItems().clear();
        subjectList.getItems().addAll(file.list());
    }

    @FXML
    private ListView<String> topicList;
    private void updateTopicList(String subject) {
        File file = new File(MainView.srcPath + '/' + subject);
        topicList.getItems().clear();
        topicList.getItems().addAll(file.list());
    }
    @FXML
    void chooseSubject(MouseEvent event) {
        if (event.getClickCount() == 2) {
            selectionSubject = subjectList.getSelectionModel().getSelectedItem();
            if (selectionSubject != null) {
                updateTopicList(selectionSubject);
            }
        }
    }

    @FXML
    void createNewSubject() {
        newNameStage("");
        if (!newName.isEmpty()) {
            if (!FileFunction.createFolder(MainView.srcPath, newName)) {
                alertWarning("Error in creating subject '" + newName + "'!");
            } else {
                updateSubjectList();
            }

        }
    }

    @FXML
    void deleteSubject() {
        String s = subjectList.getSelectionModel().getSelectedItem();
        if (s != null) {
            if (alertConfirm(s)) {

                if (new File(MainView.srcPath + '/' + s).delete()) {
                    alertWarning("Error in delete subject '" + s + "'!");
                } else {
                    subjectList.getItems().remove(s);
                }
            }
        } else {
            alertWarning("Please choose the subject to delete!");
        }
    }

    @FXML
    void renameSubject() {
        String s = subjectList.getSelectionModel().getSelectedItem();
        if (s != null) {
            newNameStage(s);
            if (!newName.isEmpty()) {
                if (!new File(MainView.srcPath + '/' + s).renameTo(new File(MainView.srcPath + '/' + newName))) {
                    alertWarning("Error in renaming subject '" + s + "'!");
                } else {
                    updateSubjectList();
                }
            }
        }
    }

    private String newName;

    public void newNameStage(String s) {
        newName = "";
        Stage stage = new Stage();
        stage.setTitle("Set new Name");
        TextField field = new TextField(s);
        field.setFont(new Font(15));
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                newName = field.getText();
                stage.close();
            }
        });
        Scene scene = new Scene(field);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public static Boolean alertConfirm(String object) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you want to change '" + object + "'?", ButtonType.YES, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get().getText().equals("Yes");
    }

    public static void alertWarning(String warning) {
        Alert alert = new Alert(Alert.AlertType.WARNING,
                warning);
        alert.show();
    }

    @FXML
    private TabPane contentTabPane;

    public void openTab(File topicFile) {
        if (topicFile.exists()) {
            StringBuilder sb = new StringBuilder();
            try {
                Scanner scanner = new Scanner(topicFile);
                while (scanner.hasNextLine()) {
                    sb.append(scanner.nextLine()).append('\n');
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Label label = new Label(topicFile.getPath());
            TextArea textArea = new TextArea(sb.toString());
            textArea.setStyle("-fx-border-color: blue;");
            textArea.setWrapText(true);
            VBox vBox = new VBox(label, textArea);
            VBox.setVgrow(textArea, Priority.ALWAYS);
            Tab tab = new Tab(topicFile.getName(), vBox);
            tab.setOnSelectionChanged(event -> {
                contentWeb.getEngine().load(topicFile.toURI().toString());
            });
            tab.setOnCloseRequest(event -> {
//                Tab tab = contentTabPane.getSelectionModel().getSelectedItem();
//                if (tab != null) {
                    try {
                        VBox content = (VBox) tab.getContent();
                        FileWriter fileWriter = new FileWriter(((Label) content.getChildren().get(0)).getText());
                        fileWriter.write(((TextArea) content.getChildren().get(1)).getText());
                        fileWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//                    contentWeb.getEngine().load(new File(MainView.srcPath + '/' + selectionSubject + '/' + tab.getText()).toURI().toString());
//                }
            });
            contentTabPane.getTabs().add(tab);
            contentTabPane.getSelectionModel().selectLast();
//            System.out.println(selectionTopic);
//            selectionTopic = contentTabPane.getSelectionModel().getSelectedItem().getText();
        }
    }

    @FXML
    void chooseTopic(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (selectionSubject != null) {
                String s = topicList.getSelectionModel().getSelectedItem();
                if (s != null) {
                    openTab(new File(MainView.srcPath + '/' + selectionSubject + '/' + s));
                }
            }
        }
    }

    @FXML
    void createNewTopic() throws IOException {
        if (selectionSubject != null) {
            newNameStage("");
            if (!newName.isEmpty()) {
                if (!FileFunction.createNewFile(MainView.srcPath + '/' + selectionSubject, newName)) {
                    alertWarning("Error in creating topic '" + newName + "'!");
                } else {
                    updateTopicList(selectionSubject);
                }
            }
        }

    }
    @FXML
    void deleteTopic() {
        String s = topicList.getSelectionModel().getSelectedItem();
        if (s != null) {
            if (alertConfirm(s)) {
                if (!new File(MainView.srcPath + '/' + selectionSubject + '/' + s).delete()) {
                    alertWarning("Error in delete topic '" + s + "'!");
                } else {
                    topicList.getItems().remove(s);
                }
            }
        } else {
            alertWarning("Please choose the topic to delete!");
        }
    }
    @FXML
    void renameTopic() {
        String s = topicList.getSelectionModel().getSelectedItem();
        if (s != null) {
            newNameStage(s);
            if (!newName.isEmpty()) {
                String path = new File(MainView.srcPath + '/' + selectionSubject + '/' + s).getPath();
                if (!new File(MainView.srcPath + '/' + selectionSubject + '/' + s).renameTo(
                        new File(MainView.srcPath + '/' + selectionSubject + '/' + newName))) {
                    alertWarning("Error in renaming topic '" + s + "'!");
                } else {
                    updateTopicList(selectionSubject);
                    for (int i = 0; i < contentTabPane.getTabs().size(); i++) {
                        if (Objects.equals(((Label) ((VBox) contentTabPane.getTabs().get(i).getContent()).getChildren().get(0)).getText(), path)) {
                            contentTabPane.getTabs().remove(i);
                            openTab(new File(MainView.srcPath + '/' + selectionSubject + '/' + newName));
                            break;
                        }
                    }
                }
            }
        }
    }

    @FXML
    private WebView contentWeb = new WebView();

    @FXML
    void showAndSaveTopicContent() {
        Tab tab = contentTabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            try {
                VBox vBox = (VBox) tab.getContent();
                FileWriter fileWriter = new FileWriter(((Label) vBox.getChildren().get(0)).getText());
                fileWriter.write(((TextArea) vBox.getChildren().get(1)).getText());
                fileWriter.close();
                contentWeb.getEngine().load(new File(((Label) vBox.getChildren().get(0)).getText()).toURI().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML ListView<String> htmlList;

    @FXML
    void chooseHTML(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String s = htmlList.getSelectionModel().getSelectedItem();
            if (s != null) {
                TextArea textArea = (TextArea) ((VBox) contentTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(1);
                if (textArea != null) {
                    String a = textArea.getSelectedText();
                    textArea.deleteText(textArea.getSelection().getStart(), textArea.getSelection().getEnd());
                    int i = textArea.getCaretPosition();
                    textArea.insertText(i, s);
                    textArea.positionCaret(textArea.getText().indexOf('>',i) + 1);
                    textArea.insertText(textArea.getCaretPosition(), a);
                    textArea.requestFocus();
                }
            }
        }
    }

    @FXML
    void createNewHTML(MouseEvent event) {
        newHTML("");
        if (!newName.isEmpty()) {
            htmlList.getItems().add(newName);
            MainView.htmlStrings.add(newName);
        }
    }
    @FXML
    void deleteHTML(MouseEvent event) {
        String s = htmlList.getSelectionModel().getSelectedItem();
        if (s != null) {
            if (alertConfirm(s)) {
                htmlList.getItems().remove(s);
                MainView.htmlStrings.remove(s);
            }
        } else {
            alertWarning("Please choose the HTML pattern to delete!");
        }
    }
    @FXML
    void renameHTML(MouseEvent event) {
        String s = htmlList.getSelectionModel().getSelectedItem();
        if (s != null) {
            newHTML(s);
            if (!newName.isEmpty()) {
                int i = htmlList.getSelectionModel().getSelectedIndex();
                htmlList.getItems().set(i, newName);
                MainView.htmlStrings.set(i, newName);
            }
        }
    }

    public void newHTML(String s) {
        newName = "";
        Stage stage = new Stage();
        stage.setTitle("Set new HTML");
        TextArea textArea = new TextArea(s);
        textArea.setFont(new Font(17));
        Button button = new Button("Change");
        button.setOnMouseClicked(event -> {
            newName = textArea.getText();
            stage.close();
        });
        VBox vBox = new VBox(textArea, button);
        vBox.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateSubjectList();
        htmlList.getItems().addAll(MainView.htmlStrings);
    }

}