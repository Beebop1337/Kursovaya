package org.example.kursovaya;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс, который создает новый сценарий.
 */
public class CreateNewScenario extends BaseWindow {
    private static final Logger logger = LogManager.getLogger(CreateNewScenario.class); // Логгер для записи действий
    private Stage stage;

    /**
     * Конструктор для создания нового сценария.
     *
     * @param stage Сцена главного окна.
     * @param baseDirectory Абсолютный путь к папке с файлами сценариев.
     */
    public CreateNewScenario(Stage stage, String baseDirectory) {
        super("", baseDirectory); // Пустой конструктор для инициализации родительского класса
        this.stage = stage;
    }

    /**
     * Отображает окно для создания нового сценария.
     */
    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField scenarioNameField = new TextField();
        scenarioNameField.setPromptText("Введите название нового сценария");



        Button createScenarioButton = new Button("Создать сценарий");
        createScenarioButton.setOnAction(e -> {
            String branchKey = scenarioNameField.getText();
            String branchText = " ";
            String nextKey = " ";

            if (!branchKey.isEmpty() && !branchText.isEmpty() && !nextKey.isEmpty()) {
                createNewScenario(branchKey);
            } else {
                showAlert("Ошибка", "Все поля должны быть заполнены!");
            }

        });

        root.getChildren().addAll(
                new Label("Введите данные для нового сценария:"),
                scenarioNameField,
                createScenarioButton
        );

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
    }

    /**
     * Создает новый сценарий, создавая папку и записывая файл с текстом в формате JSON.
     * @param  scenarioName Название сценария, которое выбирает пользователь
     *
     */
    public void createNewScenario(String scenarioName) {
        // Папка сценария
        File scenarioFolder = new File(baseDirectory + File.separator + scenarioName);
        if (!scenarioFolder.exists()) {
            if (scenarioFolder.mkdirs()) {
                logger.info("Папка для сценария создана: " + scenarioFolder.getAbsolutePath());
            } else {
                showAlert("Ошибка", "Не удалось создать папку для сценария!");
                return;
            }
        }

        JSONObject defaultNode = new JSONObject();
        defaultNode.put("text", "Сценарий пользователя.");
        defaultNode.put("choices", new JSONArray());

// Создаем главный JSON объект с ключом "start"
        JSONObject scenarioJSON = new JSONObject();
        scenarioJSON.put("start", defaultNode);

// Сохранение в файл
        File branchFile = new File(scenarioFolder, "scenario.txt");
        try (FileWriter writer = new FileWriter(branchFile)) {
            writer.write(scenarioJSON.toString(4));  // Записываем с отступами для читаемости
            logger.info("Сценарий успешно создан и записан в файл: " + branchFile.getAbsolutePath());
            showAlert("Успех", "Новая ветка сценария успешно создана!");
        } catch (IOException e) {
            logger.error("Ошибка при создании или записи файла сценария", e);
            showAlert("Ошибка", "Не удалось создать файл для сценария!");
        }
    }


    /**
     * Показывает окно с сообщением.
     *
     * @param title Заголовок окна.
     * @param message Сообщение для отображения.
     */
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
