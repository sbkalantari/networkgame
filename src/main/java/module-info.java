module com.example.network_game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.example.network_game to javafx.fxml;
    exports com.example.network_game;
}