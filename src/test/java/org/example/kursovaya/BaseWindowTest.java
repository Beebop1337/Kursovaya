package org.example.kursovaya;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса BaseWindow.
 */
public class BaseWindowTest {

    private static final String TEST_SCENARIO_NAME = "testScenario";
    private File tempBaseDirectory; // Временная директория для сценариев
    private BaseWindow baseWindow;

    /**
     * Подготовка временной директории и тестового сценария перед каждым тестом.
     */
    @BeforeEach
    public void setUp() throws IOException {
        // Создаем временную директорию
        tempBaseDirectory = Files.createTempDirectory("scenarios").toFile();

        // Создаем директорию для сценария
        File scenarioDir = new File(tempBaseDirectory, TEST_SCENARIO_NAME);
        scenarioDir.mkdirs();

        // Создаем файл сценария
        File scenarioFile = new File(scenarioDir, "scenario.txt");
        JSONObject testScenarioData = new JSONObject();
        testScenarioData.put("start", new JSONObject().put("text", "Start scene"));
        Files.writeString(scenarioFile.toPath(), testScenarioData.toString(4), StandardOpenOption.CREATE);

        // Инициализируем объект BaseWindow
        baseWindow = new BaseWindow(TEST_SCENARIO_NAME, tempBaseDirectory.getAbsolutePath());
    }

    /**
     * Удаление временных файлов после каждого теста.
     */
    @AfterEach
    public void tearDown() {
        deleteDirectory(tempBaseDirectory);
    }

    /**
     * Удаляет директорию и все ее содержимое.
     */
    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

    /**
     * Тест на загрузку данных сценария.
     */
    @Test
    public void testLoadScenarioData() {
        assertNotNull(baseWindow.scenarioData, "Сценарий не был загружен.");
        assertTrue(baseWindow.scenarioData.has("start"), "В сценарии отсутствует сцена 'start'.");
        assertEquals("Start scene", baseWindow.scenarioData.getJSONObject("start").getString("text"),
                "Текст сцены не совпадает.");
    }

    /**
     * Тест на сохранение данных сценария.
     */
    @Test
    public void testSaveScenarioData() throws IOException {
        // Изменяем данные сценария
        baseWindow.scenarioData.getJSONObject("start").put("text", "New start scene");

        // Сохраняем изменения
        baseWindow.saveScenarioData();

        // Загружаем данные сценария из файла и проверяем изменения
        File scenarioFile = new File(tempBaseDirectory, TEST_SCENARIO_NAME + "/scenario.txt");
        String content = Files.readString(scenarioFile.toPath());
        JSONObject savedData = new JSONObject(content);

        assertEquals("New start scene", savedData.getJSONObject("start").getString("text"),
                "Текст сцены не был сохранен.");
    }

    /**
     * Тест на перезагрузку данных сценария.
     */
    @Test
    public void testReloadScenario() throws IOException {
        // Изменяем данные в файле напрямую
        File scenarioFile = new File(tempBaseDirectory, TEST_SCENARIO_NAME + "/scenario.txt");
        JSONObject modifiedScenario = new JSONObject();
        modifiedScenario.put("start", new JSONObject().put("text", "Reloaded start scene"));
        Files.writeString(scenarioFile.toPath(), modifiedScenario.toString(4), StandardOpenOption.TRUNCATE_EXISTING);

        // Перезагружаем данные
        baseWindow.reloadScenario();

        // Проверяем, что данные перезагружены
        assertEquals("Reloaded start scene", baseWindow.scenarioData.getJSONObject("start").getString("text"),
                "Текст сцены не был перезагружен.");
    }


}
