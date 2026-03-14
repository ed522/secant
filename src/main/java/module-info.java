module com.ed522.secant {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ed522.secant to javafx.fxml;
    exports com.ed522.secant;
}
