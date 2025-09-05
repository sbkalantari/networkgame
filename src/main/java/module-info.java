module com.example.network_game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.example.network_game to javafx.fxml;
    exports com.example.network_game;
    exports com.example.network_game.model;
    opens com.example.network_game.model to javafx.fxml;
    exports com.example.network_game.view;
    opens com.example.network_game.view to javafx.fxml;
    exports com.example.network_game.controller;
    opens com.example.network_game.controller to javafx.fxml;
    exports com.example.network_game.save;
    opens com.example.network_game.save to javafx.fxml;
}