module ni.edu.uam.practicahilos {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens ni.edu.uam.practicahilos to javafx.fxml;
    exports ni.edu.uam.practicahilos;
}