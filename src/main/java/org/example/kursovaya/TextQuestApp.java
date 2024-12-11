package org.example.kursovaya;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Основной класс приложения для текстового квеста.
 * Предоставляет пользовательский интерфейс для выбора и прохождения сценариев.
 */
public class TextQuestApp extends Application {
    private final List<String> scenarioNames = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(TextQuestApp.class); // Логгер для log4j
    private String baseDirectory = "scenarios"; // Директория для сценариев
    private ListView<String> scenarioList; // Список сценариев

    /**
     * Запускает приложение.
     * Это основной метод для запуска JavaFX приложения.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Инициализирует основное окно приложения и отображает его.
     * В этом методе создается интерфейс для выбора сценариев.
     *
     * @param primaryStage Основная сцена приложения.
     */
    @Override
    public void start(Stage primaryStage) {
        logger.info("Приложение запущено.");
        try {
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));

            // Заголовок
            Label titleLabel = new Label("Выберите сценарий:");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            VBox topSection = new VBox(titleLabel);
            topSection.setPadding(new Insets(10, 0, 10, 0));

            // Список сценариев
            scenarioList = new ListView<>();
            loadScenarioFromFolder(new File(baseDirectory));
            scenarioList.getItems().addAll(scenarioNames);

            // Блок кнопок
            Button startButton = new Button("Начать");
            startButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            startButton.setOnAction(e -> {
                String selectedScenario = scenarioList.getSelectionModel().getSelectedItem();
                if (selectedScenario != null) {
                    launchScenario(primaryStage, selectedScenario);
                } else {
                    showAlert("Ошибка", "Выберите сценарий!");
                }
            });

            Button createScenarioButton = new Button("Создать новый сценарий");
            createScenarioButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            createScenarioButton.setOnAction(e -> openCreateScenarioWindow(primaryStage));

            Button exitButton = new Button("Выход");
            exitButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            exitButton.setOnAction(e -> primaryStage.close());

            Button chooseFolderButton = new Button("Выбрать папку");
            chooseFolderButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            chooseFolderButton.setOnAction(e -> chooseFolder());

            HBox buttonBox = new HBox(10, startButton, createScenarioButton, exitButton, chooseFolderButton);
            buttonBox.setPadding(new Insets(10));
            buttonBox.setStyle("-fx-alignment: center;");

            root.setTop(topSection);
            root.setCenter(scenarioList);
            root.setBottom(buttonBox);

            Scene scene = new Scene(root, 600, 400);
            primaryStage.setTitle("Текстовый квест");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Ошибка запуска приложения: {}", e.getMessage(), e);
            showAlert("Критическая ошибка", "Ошибка запуска приложения. Посмотрите app.log для деталей.");
        }
    }

    /**
     * Загружает сценарии из указанной папки.
     */
    private void loadScenarioFromFolder(File folder) {
        scenarioNames.clear();
        if (folder.exists() && folder.isDirectory()) {
            for (File subFolder : folder.listFiles()) {
                if (subFolder.isDirectory() && new File(subFolder, "scenario.txt").exists()) {
                    scenarioNames.add(subFolder.getName());
                }
            }
        }
    }

    /**
     * Обновляет список сценариев.
     */
    private void refreshScenarioList() {
        scenarioList.getItems().clear();
        loadScenarioFromFolder(new File(baseDirectory));
        scenarioList.getItems().addAll(scenarioNames);
    }

    /**
     * Открывает окно для создания нового сценария.
     */
    private void openCreateScenarioWindow(Stage primaryStage) {
        CreateNewScenario createNewScenarioWindow = new CreateNewScenario(primaryStage, baseDirectory);
        createNewScenarioWindow.show();
        refreshScenarioList(); // Обновить список сценариев после создания
    }

    /**
     * Открывает диалог выбора папки, и загружает сценарии из выбранной папки.
     */
    private void chooseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            baseDirectory = selectedDirectory.getAbsolutePath();
            refreshScenarioList();
            if (scenarioNames.isEmpty()) {
                showAlert("Ошибка", "Не найдены сценарии в выбранной папке.");
            }
        }
    }

    /**
     * Запускает окно игры для выбранного сценария.
     */
    private void launchScenario(Stage stage, String scenarioName) {
        logger.info("Загрузка сценария: {}", scenarioName);
        AddScenario gameWindow = new AddScenario(stage, scenarioName, baseDirectory);
        gameWindow.show();
    }

    /**
     * Отображает диалоговое окно с указанным заголовком и сообщением.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
