module com.ed522.secant {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires java.desktop;

    opens com.ed522.secant to javafx.fxml;
    exports com.ed522.secant;
    exports com.ed522.secant.theory;
    opens com.ed522.secant.theory to javafx.fxml;
}
