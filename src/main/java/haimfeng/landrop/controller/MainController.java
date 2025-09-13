package haimfeng.landrop.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;

public class MainController {
    public static final EventBus eventBus = new EventBus();

    @FXML
    public void initialize()
    {
        System.out.println("MainController");
        eventBus.register(this);
    }

    @FXML private void ButtonClicked() {
        eventBus.post("ButtonClicked");
    }

    @Subscribe
    public void onButtonClicked(String event) {
        System.out.println("onButtonClicked: " + event);
    }
}