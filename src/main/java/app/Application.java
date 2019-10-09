package app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Application {
    public static void main(String[] args) {

        int width = 400;
        int height = 300;
        int scale = 3;
        int step_thresh=5;

        int seed = 123;

        Cave c = new Cave(scale, width, height, step_thresh);




    }
}
