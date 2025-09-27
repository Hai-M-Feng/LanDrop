package haimfeng.landrop.view;

import com.google.common.eventbus.EventBus;
import haimfeng.landrop.event.SendConnectionRequestEvent;
import haimfeng.landrop.model.BroadcastListItem;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;

public class BroadcastListItemCell extends ListCell<BroadcastListItem> {
    @FXML private HBox root; // 根布局
    @FXML private Label receivedNameLabel; // 收到文件名
    @FXML private Label receivedTimeLabel; // 收到时间
    @FXML private Button requestButton; // 请求连接按钮

    private final FXMLLoader loader; // 加载布局依赖
    private final EventBus eventbus; // 事件总线依赖

    /**
     * 构造函数
     * @param eventbus 事件总线
     */
    public BroadcastListItemCell(EventBus eventbus) {
        this.eventbus = eventbus;
        loader = new FXMLLoader(getClass().getResource("/haimfeng/landrop/broadcast-list-cell.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        receivedTimeLabel.setStyle("-fx-text-fill: gray;");
    }

    /**
     * 更新列表项
     * @param item 列表项数据
     * @param empty 是否为空
     */
    @Override
    protected void updateItem(BroadcastListItem item, boolean empty) {
        super.updateItem(item, empty);

        // 清理旧绑定
        if (getItem() != null) {
            receivedNameLabel.textProperty().unbind();
            receivedTimeLabel.textProperty().unbind();
        }

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // 绑定新数据
            receivedNameLabel.textProperty().bind(item.receivedNameProperty());
            receivedTimeLabel.textProperty().bind(item.receivedTimeProperty());

            // 清理按钮点击事件
            requestButton.setOnAction(null);
            // 设置按钮点击事件
            requestButton.setOnAction(event ->
                    eventbus.post(new SendConnectionRequestEvent(item.getBroadcastPacket()))
            );

            setGraphic(root);
        }
    }
}