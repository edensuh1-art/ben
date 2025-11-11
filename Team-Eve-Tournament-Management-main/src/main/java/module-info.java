module edu.augustana.csc305.lab2 {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires com.google.gson;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    // Allow Jackson (and Gson) to use reflection to access private fields
    // for serialization/deserialization at runtime.
    opens edu.augustana.csc305.project to com.fasterxml.jackson.databind, com.google.gson;

    exports edu.augustana.csc305.project;
}
