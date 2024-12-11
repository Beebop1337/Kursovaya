module org.example.kursovaya {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.junit.jupiter.api;
    requires org.opentest4j;
    requires org.junit.platform.commons;
    requires org.apiguardian.api;
    requires org.junit.platform.engine;
    requires org.junit.platform.launcher;
    requires org.junit.jupiter;
    requires org.junit.jupiter.params;
    requires org.json;



    opens org.example.kursovaya to javafx.fxml, org.junit.platform.commons;
    exports org.example.kursovaya;

}