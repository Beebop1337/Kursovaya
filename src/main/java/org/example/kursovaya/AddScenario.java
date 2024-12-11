package org.example.kursovaya;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс, представляющий окно добавления сценариев.
 */
public class AddScenario extends BaseWindow {
    private static final Logger logger = LogManager.getLogger(AddScenario.class); // Логгер для записи действий
    private Stage stage;
    private String currentSceneKey = "start"; // Ключ текущей сцены

    /**
     * Конструктор для окна добавления сценариев.
     *
     * @param stage Сцена главного окна.
     * @param scenarioName Имя редактируемого сценария.
     * @param baseDirectory Абсолютный путь к папке с файлами сценариев.
     */
    public AddScenario(Stage stage, String scenarioName, String baseDirectory) {
        super(scenarioName, baseDirectory);  // Инициализация родительского класса с абсолютным путем
        this.stage = stage;
    }

    /**
     * Отображает окно добавления сценариев с текущими данными.
     */
    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextArea sceneText = new TextArea();
        sceneText.setWrapText(true);  // Включить перенос текста
        sceneText.setEditable(false);  // Сделать текстовое поле только для чтения
        sceneText.setMaxWidth(350);    // Ограничить ширину
        sceneText.setPrefHeight(200);  // Установить предпочтительную высоту

        VBox choicesBox = new VBox(5);

        // Кнопка для добавления новой ветки
        Button addBranchButton = new Button("Добавить ветку");
        addBranchButton.setOnAction(e -> {
            logger.info("Пользователь нажал кнопку 'Добавить ветку'");
            openAddBranchDialog(sceneText, choicesBox);
        });

        // Кнопка для выхода на начальную сцену
        Button exitToStartButton = new Button("Выход на начало");
        exitToStartButton.setOnAction(e -> {
            logger.info("Пользователь нажал кнопку 'Выход на начало'");
            currentSceneKey = "start";  // Сброс на начальную сцену
            updateScene(sceneText, choicesBox);  // Обновить сцену
        });

        // Обновить интерфейс с текущими данными сцены
        updateScene(sceneText, choicesBox);

        root.getChildren().addAll(sceneText, choicesBox, addBranchButton, exitToStartButton);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
    }

    /**
     * Обновляет сцену, загружая данные сценария и обновляя элементы интерфейса.
     *
     * @param sceneText Текстовое поле для отображения текста сцены
     * @param choicesBox VBox для отображения выборов для сцены
     */
    private void updateScene(TextArea sceneText, VBox choicesBox) {
        reloadScenario(); // Перезагрузка сценария

        choicesBox.getChildren().clear();

        if (scenarioData != null && scenarioData.has(currentSceneKey)) {
            JSONObject scene = scenarioData.getJSONObject(currentSceneKey);
            sceneText.setText(scene.optString("text", "Текст сцены отсутствует."));

            JSONArray choices = scene.optJSONArray("choices");
            if (choices != null) {
                choices.forEach(choice -> {
                    JSONObject choiceObj = (JSONObject) choice;
                    Button choiceButton = new Button(choiceObj.optString("option", "Без названия"));
                    choiceButton.setOnAction(e -> {
                        logger.info("Пользователь выбрал вариант: " + choiceObj.optString("option"));
                        currentSceneKey = choiceObj.optString("next", "end");
                        updateScene(sceneText, choicesBox);  // Обновить сцену после выбора
                    });
                    choicesBox.getChildren().add(choiceButton);
                });
            }
        } else {
            sceneText.setText("Конец сценария.");
            logger.info("Достигнут конец сценария.");
        }
    }

    /**
     * Открывает диалог для добавления новой ветки в сценарий.
     *
     * @param sceneText Текстовое поле для отображения текста сцены
     * @param choicesBox VBox для отображения выборов для сцены
     */
    private void openAddBranchDialog(TextArea sceneText, VBox choicesBox) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Добавить новую ветку");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(10));

        TextField branchKeyField = new TextField();
        branchKeyField.setPromptText("Ключ новой ветки");

        TextArea branchTextArea = new TextArea();
        branchTextArea.setPromptText("Текст новой ветки");

        TextField choiceOptionField = new TextField();
        choiceOptionField.setPromptText("Текст выбора");

        TextField nextKeyField = new TextField();
        nextKeyField.setPromptText("Ключ следующей сцены");

        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(e -> {
            String branchKey = branchKeyField.getText();
            String branchText = branchTextArea.getText();
            String choiceOption = choiceOptionField.getText();
            String nextKey = nextKeyField.getText();

            if (!branchKey.isEmpty() && !branchText.isEmpty() && !choiceOption.isEmpty() && !nextKey.isEmpty()) {
                logger.info("Сохраняем новую ветку с ключом: " + branchKey);
                addNewBranch(branchKey, branchText, choiceOption, nextKey);
                updateScene(sceneText, choicesBox); // Обновить сцену после добавления ветки
                dialog.close();
            } else {
                logger.warn("Не удалось сохранить новую ветку: отсутствуют обязательные поля.");
                showAlert("Ошибка", "Все поля должны быть заполнены!");
            }
        });

        dialogVBox.getChildren().addAll(
                new Label("Введите данные новой ветки:"),
                branchKeyField,
                branchTextArea,
                choiceOptionField,
                nextKeyField,
                saveButton
        );

        Scene dialogScene = new Scene(dialogVBox, 300, 400);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    /**
     * Добавляет новую ветку в сценарий и обновляет выборы для текущей сцены.
     *
     * @param branchKey Ключ новой ветки
     * @param branchText Текст новой ветки
     * @param choiceOption Вариант выбора, ведущий к новой ветке
     * @param nextKey Ключ следующей сцены после новой ветки
     */
    private void addNewBranch(String branchKey, String branchText, String choiceOption, String nextKey) {
        // Создаем новую ветку
        JSONObject newBranch = new JSONObject();
        newBranch.put("text", branchText);
        newBranch.put("choices", new JSONArray());

        // Добавляем новую ветку в данные сценария
        scenarioData.put(branchKey, newBranch);

        // Добавляем новый выбор в текущую сцену
        if (scenarioData.has(currentSceneKey)) {
            JSONObject currentScene = scenarioData.getJSONObject(currentSceneKey);
            JSONArray choices = currentScene.optJSONArray("choices");
            if (choices == null) {
                choices = new JSONArray();
                currentScene.put("choices", choices);
            }

            JSONObject newChoice = new JSONObject();
            newChoice.put("option", choiceOption);
            newChoice.put("next", branchKey);

            choices.put(newChoice);
        } else {
            logger.error("Текущая сцена не найдена в JSON при добавлении ветки!");
            showAlert("Ошибка", "Текущая сцена не найдена в JSON!");
        }

        saveScenarioData(); // Сохраняем обновленные данные сценария
        logger.info("Новая ветка успешно добавлена.");
        showAlert("Успех", "Новая ветка добавлена и выбор обновлен!");
    }
}
