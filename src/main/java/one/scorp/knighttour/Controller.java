package one.scorp.knighttour;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.util.*;

public class Controller {
    // possible directions of a knight
    private static final int[][] dirs = new int[][]{{1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}};
    // board size
    public static int n = 6;
    private static Point starting = new Point(0, 0);
    private static PairComp pair_comparator = new PairComp();

    @FXML
    private GridPane grid;
    private Background bf_white = new Background(new BackgroundFill(Paint.valueOf("white"), new CornerRadii(10), null));
    private Background bf_grey = new Background(new BackgroundFill(Paint.valueOf("whitesmoke"), new CornerRadii(10), null));
    private Background bf_orange = new Background(new BackgroundFill(Paint.valueOf("orange"), new CornerRadii(10), null));
    private Background bf_red = new Background(new BackgroundFill(Paint.valueOf("red"), new CornerRadii(10), null));
    private Background bf_yellow = new Background(new BackgroundFill(Paint.valueOf("yellow"), new CornerRadii(10), null));
    private Task<Void> tsk;
    private Stack<ArrayList<Point>> all_scs;
    private ArrayList<Point> cur;
    private boolean inited = false;

    // a simple heuristic function
    private static int heuristics(Point p, int n) {
        int x = p.x, y = p.y;
        return Math.min(x - 1, n - x) + Math.min(y - 1, n - y);
    }

    // generate a point's successors
    private static ArrayList<Point> getSuccessors(Point p, int n, List<Point> steps) {
        int x = p.x, y = p.y;
        ArrayList<Pair<Point, Integer>> al = new ArrayList<>();
        boolean b = steps.size() == n * n;
        for (int[] ds : dirs) {
            int dx = ds[0], dy = ds[1];
            Point cp;
            boolean xok = (dx < 0) ? x > -dx - 1 : x < n - dx,
                    yok = (dy < 0) ? y > -dy - 1 : y < n - dy;
            if (xok && yok && (b & starting.equals(cp = new Point(x + dx, y + dy)) || !b && !steps.contains(cp)))
                al.add(new Pair<>(cp, heuristics(cp, n)));
        }
        al.sort(pair_comparator);
        ArrayList<Point> rt = new ArrayList<>();
        for (Pair<Point, Integer> npr : al)
            rt.add(npr.first);
        return rt;
    }

    // stack-unrolled implementation
    public static ArrayList<Point> findPath(int n) {
        Stack<ArrayList<Point>> all_scs = new Stack<>();
        ArrayList<Point> al = new ArrayList<>(), cur = new ArrayList<>();
        al.add(starting);
        all_scs.push(al);
        while (!all_scs.isEmpty()) {
            if (cur.size() == n * n + 1)
                return cur;
            al = all_scs.peek();
            if (al.isEmpty()) {
                cur.remove(cur.size() - 1);
                all_scs.pop();
                continue;
            }
            Point s = al.remove(0);
            cur.add(s);
            ArrayList<Point> scs = getSuccessors(s, n, cur);
            all_scs.push(scs);
        }
        return null;
    }

    // concise implementation
    public static ArrayList<Point> backtrack_dfs(int n, ArrayList<Point> cur) {
        if (cur.size() == n * n + 1)
            return cur;
        ArrayList<Point> scs = getSuccessors(cur.get(cur.size() - 1), n, cur);
        for (Point p : scs) {
            cur.add(p);
            ArrayList<Point> res = backtrack_dfs(n, cur);
            if (res != null)
                return res;
            cur.remove(cur.size() - 1);
        }
        return null;
    }

    // starting point of recursive backtrack-dfs
    public static ArrayList<Point> findPath_recur(int n) {
        ArrayList<Point> ap = new ArrayList<>();
        ap.add(starting);
        return backtrack_dfs(n, ap);
    }

    private Pane getPane(int x, int y) {
        return (Pane) grid.lookup("#b_" + x + "_" + y);
    }

    private Pane getPane(Point p) {
        return getPane(p.x, p.y);
    }

    private void clear_all() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                Pane p = getPane(i, j);
                p.setBackground((i + j) % 2 == 0 ? bf_white : bf_grey);
                setCellText(p, "");
            }
    }

    @FXML
    protected void nextMove(ActionEvent e) {
        init();
        clear_all();
        showNextMove();
    }

    // display next move on the board
    private boolean showNextMove() {
        ArrayList<Point> state = getNextMove();
        if (state != null) {
            for (int i = 0; i < state.size(); i++) {
                Pane r = getPane(state.get(i));
                r.setBackground(i == state.size() - 1 ? bf_red : bf_yellow);
                Integer ss = i + 1;
                setCellText(r, ss.toString());
            }
        }
        return state != null;
    }

    // helper function for setting a cell's text
    private void setCellText(Pane r, String txt) {
        ((Text) (r.getChildren().get(0))).setText(txt);
    }

    @FXML
    // stop timer
    protected void stopTimer(ActionEvent e) {
        if (tsk != null) {
            tsk.cancel(false);
        }
    }

    @FXML
    // start displaying search steps using a task and thread
    protected void startTimer(ActionEvent e) throws InterruptedException {
        init();
        tsk = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (showNextMove() && !isCancelled())
                    Thread.sleep(2);
                return null;
            }
        };
        new Thread(tsk).start();
    }

    // draw solution on the bare-empty board
    private void drawSolution(ArrayList<Point> sol) {
        if (sol != null) {
            for (int i = 0; i < sol.size(); i++) {
                Point p = sol.get(i);
                Pane r = getPane(p);
                int x = p.x, y = p.y;
                r.setBackground((x + y) % 2 == 0 ? bf_white : bf_grey);
                Integer ss = i + 1;
                setCellText(r, ss.toString());
            }
        }
    }

    @FXML
    // action listener for fxml
    // opens up dialogs and calls a function to show the final solution of program
    protected void showSolution() {
        if (n % 2 != 0) {
            Alert alt = new Alert(Alert.AlertType.ERROR, "The specified board size doesn't have a closed Knight's Tour solution.");
            alt.showAndWait();
            return;
        } else if (n > 7) {
            Alert alt = new Alert(Alert.AlertType.WARNING, "You have to wait until next year!");
            alt.showAndWait();
            return;
        }

        ChoiceDialog<AlgorithmType> cd = new ChoiceDialog<>(AlgorithmType.Recursive, AlgorithmType.Recursive, AlgorithmType.Iterative);
        cd.setTitle("Knight's Tour");
        cd.setHeaderText("Search Strategy Implementation");
        cd.setContentText("Please specify whether you want to\nuse recursive or iterative backtracking dfs search:");
        Optional<AlgorithmType> res = cd.showAndWait();
        if (res.isPresent()) {
            inited = false;
            ArrayList<Point> sol;
            if (res.get() == AlgorithmType.Iterative) {
                sol = findPath(n);
            } else if (res.get() == AlgorithmType.Recursive) {
                sol = findPath_recur(n);
            } else return;
            drawSolution(sol);
        }
    }

    // sentinel-guarded initializer
    protected void init() {
        if (!inited) {
            all_scs = new Stack<>();
            cur = new ArrayList<>();
            ArrayList<Point> al = new ArrayList<>();
            al.add(starting);
            all_scs.push(al);
            inited = true;
        }
    }

    // iterate through the algorithm to get next search node
    private ArrayList<Point> getNextMove() {
        boolean found = false;
        ArrayList<Point> al;
        while (!all_scs.isEmpty() && !found) {
            if (cur.size() == n * n + 1)
                return cur;
            al = all_scs.peek();
            if (al.isEmpty()) {
                cur.remove(cur.size() - 1);
                all_scs.pop();
                continue;
            }
            Point s = al.remove(0);
            cur.add(s);
            found = true;
            ArrayList<Point> scs = getSuccessors(s, n, cur);
            showSuccessors(scs);
            all_scs.push(scs);
        }
        if (found)
            return cur;
        return null;
    }

    // highlight a point's successors
    private void showSuccessors(ArrayList<Point> scs) {
        for (int i = 0; i < scs.size(); i++) {
            getPane(scs.get(i)).setBackground(i == scs.size() - 1 ? bf_orange : bf_orange);
        }
    }

    private enum AlgorithmType {
        Iterative, Recursive;

        public String toString() {
            return this.name();
        }
    }

    private static class PairComp implements Comparator<Pair<Point, Integer>> {
        public int compare(Pair<Point, Integer> p1, Pair<Point, Integer> p2) {
            return p1.second.compareTo(p2.second);
        }
    }
}
