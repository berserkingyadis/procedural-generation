package app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Application {
    public static void main(String[] args) {

        int width = 100;
        int height = 100;
        int scale = 5;
        int step_thresh=5;

        int seed = 123;

        Cave c = new Cave(scale, width, height, step_thresh);




    }
}
