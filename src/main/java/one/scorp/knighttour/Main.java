package one.scorp.knighttour;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    public static Image icon = new Image(Main.class.getResourceAsStream("knight_white.png"));

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("chessboard.fxml"));
        GridPane gp = (GridPane) root.lookup("#grid");
        Background bf_white = new Background(new BackgroundFill(Paint.valueOf("white"), new CornerRadii(10), null));

        int n = 0;
        TextInputDialog tid = new TextInputDialog("6");
        tid.setTitle("Knight's Tour");
        tid.setHeaderText("Board Size");
        tid.setContentText("Please specify your board's size n (for a n x n board):");
        Optional<String> res = tid.showAndWait();
        RowConstraints rc = new RowConstraints(10, 100, 150, Priority.ALWAYS, VPos.CENTER, true);
        ColumnConstraints cc = new ColumnConstraints(10, 100, 150, Priority.ALWAYS, HPos.CENTER, true);
        Font fnt = new Font("Arial", 20);
        if (res.isPresent()) {
            n = Integer.parseInt(res.get());
            Controller.n = n;
        }
        Border brd = new Border(new BorderStroke(Paint.valueOf("grey"), BorderStrokeStyle.SOLID,
                new CornerRadii(10), new BorderWidths(0.4)));
        for (int i = 0; i < n; i++) {
            gp.getRowConstraints().addAll(rc);
            for (int j = 0; j < n; j++) {
                if (j == 0)
                    gp.getColumnConstraints().addAll(cc);

                Pane l;
                GridPane glp = new GridPane();
                l = glp;
                glp.setAlignment(Pos.CENTER);
                Text t = new Text("");
                t.setFont(fnt);
                t.setTextAlignment(TextAlignment.CENTER);
                l.getChildren().addAll(t);
                glp.setBorder(brd);

                if ((i + j) % 2 == 0)
                    l.setBackground(bf_white);

                l.setId("b_" + i + "_" + j);
                gp.add(l, j, i);
            }
        }

        primaryStage.setTitle("Knight's Tour");
        Scene sc = new Scene(root);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(sc);
        primaryStage.show();
    }
}
