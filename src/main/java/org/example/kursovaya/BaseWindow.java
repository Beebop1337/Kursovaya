package org.example.kursovaya;

import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс, представляющий базовое окно для работы с сценарием.
 * Отвечает за загрузку, сохранение и обновление сценария.
 */
public class BaseWindow {
    private static final Logger logger = LogManager.getLogger(BaseWindow.class); // Логгер для логирования действий
    protected String scenarioName;  // Название сценария
    protected JSONObject scenarioData;  // Данные сценария в формате JSON
    protected String baseDirectory; // Абсолютный путь к папке с сценариями

    /**
     * Конструктор для инициализации окна с указанным сценарием.
     * Добавлен параметр для абсолютного пути к папке с файлами сценариев.
     *
     * @param scenarioName Название сценария
     * @param baseDirectory Абсолютный путь к папке с сценариями
     */
    public BaseWindow(String scenarioName, String baseDirectory) {
        this.scenarioName = scenarioName;
        this.baseDirectory = baseDirectory;
        loadScenarioData();
    }

    /**
     * Загружает данные сценария из файла.
     * Если файл отсутствует, создаётся пустой JSON сценарий по умолчанию.
     */
    protected void loadScenarioData() {
        File scenarioFile = new File(baseDirectory + File.separator + scenarioName + File.separator + "scenario.txt");
        try {
            if (scenarioFile.exists()) {
                String content = new String(Files.readAllBytes(scenarioFile.toPath()));
                scenarioData = new JSONObject(content);

                // Проверяем структуру JSON
                validateScenarioData();
                logger.info("Сценарий {} загружен успешно.", scenarioName);
            } else {
                logger.warn("Файл сценария не найден: {}. Создание нового сценария по умолчанию.", scenarioFile.getAbsolutePath());

            }
        } catch (Exception e) {
            logger.error("Ошибка загрузки сценария: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить сценарий: " + e.getMessage());
            // Создаём сценарий по умолчанию в случае ошибки
        }
    }

    /**
     * Сохраняет данные сценария в файл.
     */
    protected void saveScenarioData() {
        File scenarioFile = new File(baseDirectory + File.separator + scenarioName + File.separator + "scenario.txt");
        try {
            Files.writeString(scenarioFile.toPath(), scenarioData.toString(4), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Сценарий {} успешно сохранён.", scenarioName);
        } catch (IOException e) {
            logger.error("Не удалось сохранить сценарий: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось сохранить сценарий: " + e.getMessage());
        }
    }

    /**
     * Проверяет структуру сценария и исправляет ошибки, если это возможно.
     */
    private void validateScenarioData() {
        try {
            for (String key : scenarioData.keySet()) {
                JSONObject node = scenarioData.getJSONObject(key);

                // Проверяем наличие обязательных полей
                if (!node.has("text") || !(node.get("text") instanceof String)) {
                    throw new IllegalArgumentException("У узла " + key + " отсутствует текст или он имеет неверный тип.");
                }
                if (!node.has("choices") || !(node.get("choices") instanceof JSONArray)) {
                    // Если поле choices отсутствует или имеет неверный тип, заменяем его на пустой массив
                    node.put("choices", new JSONArray());
                    logger.warn("У узла {} поле 'choices' было исправлено на пустой массив.", key);
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в структуре сценария: {}", e.getMessage(), e);
            throw new IllegalStateException("Сценарий имеет некорректную структуру и не может быть загружен.");
        }
    }



    /**
     * Перезагружает данные сценария из файла.
     */
    protected void reloadScenario() {
        try {
            File scenarioFile = new File(baseDirectory + File.separator + scenarioName + File.separator + "scenario.txt");
            if (scenarioFile.exists()) {
                String content = new String(Files.readAllBytes(scenarioFile.toPath()));
                scenarioData = new JSONObject(content);

                // Проверяем структуру JSON
                validateScenarioData();
                logger.info("Сценарий {} успешно обновлён.", scenarioName);
            } else {
                throw new IOException("Файл сценария не существует: " + scenarioFile.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.error("Не удалось обновить сценарий: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось обновить сценарий: " + e.getMessage());
        }
    }

    /**
     * Отображает всплывающее окно с сообщением об ошибке или успехе.
     *
     * @param title Заголовок окна
     * @param message Текст сообщения
     */
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        logger.info("Отображено сообщение: {}", message);  // Логирование вывода сообщения
    }
}
