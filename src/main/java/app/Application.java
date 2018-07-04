package app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Application {
    public static void main(String[] args) {

        int width = 1000;
        int height = 1000;
        int scale = 4;
        int step_thresh=5;

        Cave c = new Cave(scale, width, height, step_thresh);






    }
}
