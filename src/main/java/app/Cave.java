package app;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

public class Cave extends JFrame {

    private CELL_TYPE cells[][];

    private enum CELL_TYPE {
        ROCK,
        FREE,
        TREE_STUMP,
        TREE_MID,
        TREE_TOP,
        NO_TREE
    }


    private int scale;          //width-height of every cell in pixels
    private int width;          //width of cave
    private int height;         //height of cave
    private int step_thresh;    //how many rocks must be around a cell in order for it becoming a rock
    private int seed;           //seed of the RNG
    private int cave_n;         //current amount of steps in cave generation
    private int trees_n;        //current amount of steps in tree generation



    private final Color rock = new Color(128, 128, 128);
    private final Color free = Color.WHITE;
    private final Color stump = new Color(77,26,0);
    private final Color mid = new Color(153, 51, 0);
    private final Color top = new Color(0, 102, 0);

    private Random r;

    private void initValues(int scale, int width, int height, int step_thresh){
        cells = new CELL_TYPE[width][height];
        this.width=width;
        this.height=height;
        this.scale = scale;
        this.step_thresh = step_thresh;
    }

    private void setupUI(){

        this.setSize(width*scale, height*scale);
        this.setResizable(false);
        this.setTitle("q=make cave w=grow trees e=reset r=quit a=benchmark");
        this.setVisible(true);
    }
    private void registerInteractions(){
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()){
                    case KeyEvent.VK_Q:
                        step(); repaint(); break;
                    case KeyEvent.VK_W:
                        stepX(); repaint(); break;
                    case KeyEvent.VK_E:
                        reset(); repaint(); break;
                    case KeyEvent.VK_A:
                        benchmark(); repaint(); break;
                    case KeyEvent.VK_S:
                        System.out.println("Seed: " + seed); break;
                    case KeyEvent.VK_D:
                        savePNG(); break;
                    case KeyEvent.VK_R:
                        System.exit(0);
                }
            }
        });
    }
    public Cave(int scale, int width, int height, int step_thresh, int seed) {

        this.seed = seed;

        r = new Random(seed);

        initValues(scale, width, height, step_thresh);
        reset();
        registerInteractions();
        setupUI();
    }

    public Cave(int scale, int width, int height, int step_thresh){
        r = new Random();
        seed = randInt(1, 88888888);
        r = new Random(seed);
        initValues(scale, width, height, step_thresh);
        reset();
        registerInteractions();
        setupUI();
    }

    public void benchmark() {
        reset();
        long cave_t, trees_t;
        long start = System.currentTimeMillis();
        //step until nothing changes
        while(step())++cave_n;
        cave_t = System.currentTimeMillis()-start;
        //stepX until nothing changes
        start = System.currentTimeMillis();
        while(stepX())++trees_n;
        trees_t = System.currentTimeMillis()-start;
        System.out.println("Cave generation performance report:");
        System.out.println("Cave size = " + cells.length + " x " + cells[0].length);
        System.out.println("Seed = "+seed);
        System.out.println("Generating Cave in "+cave_n+" steps took "+cave_t+ " milliseconds.");
        System.out.println("Generating Trees in "+trees_n+" steps took "+trees_t+" milliseconds.");
        System.out.println("Total time is "+(cave_t+trees_t)+" milliseconds.");
    }

    private void drawCave(Graphics2D g2d){
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                switch(get(i,j)){
                    case TREE_MID: g2d.setColor(mid);break;
                    case ROCK: g2d.setColor(rock);break;
                    case TREE_STUMP: g2d.setColor(stump);break;
                    case TREE_TOP: g2d.setColor(top);break;
                    case FREE: g2d.setColor(free);break;



                }
                g2d.fillRect(i*scale,j*scale, scale, scale);
            }
        }
    }
    public void savePNG() {
        BufferedImage image = new BufferedImage(width*scale, height*scale, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();

        drawCave(g2d);

        String filename = filename();
        try {
            ImageIO.write(image, "PNG", new File(filename));
            System.out.println("Cave saved as "+filename);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private String filename(){
        return seed + "s-"+cave_n+"cn-"+trees_n+"tn-"+width+"w-"+height+"h.png";
    }
    public void reset(){
        this.cave_n=0;
        this.trees_n=0;

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = (try_(50))?CELL_TYPE.FREE:CELL_TYPE.ROCK;
            }
        }
    }


    @Override
    public void paint(Graphics g) {
       Graphics2D g2d = (Graphics2D)g;

       drawCave(g2d);
    }



    public boolean stepX(){
            boolean changed = false;

            for (int i = 0; i < cells.length; i++) {
                for (int j = 1; j < cells[0].length-1; j++) {

                    CELL_TYPE after;

                    CELL_TYPE here = get(i, j);
                    CELL_TYPE under = get(i, j+1);


                   if(here!=CELL_TYPE.FREE)continue;

                    switch (under) {
                        case ROCK:
                            if(try_(40))set(i,j, CELL_TYPE.TREE_STUMP);
                            else set(i,j, CELL_TYPE.NO_TREE);
                            if(!changed)changed=true;
                            break;
                        case TREE_STUMP:
                            if(try_(70))set(i,j, CELL_TYPE.TREE_MID);
                            else set(i,j, CELL_TYPE.TREE_TOP);
                            if(!changed)changed=true;
                            break;
                        case TREE_MID:
                            if(try_(50))set(i,j, CELL_TYPE.TREE_MID);
                            else set(i,j, CELL_TYPE.TREE_TOP);
                            if(!changed)changed=true;
                            break;
                    }

                }
            }

            return changed;

    }



    private int randInt(int low, int high){

        return r.nextInt((high - low) + 1) + low;
    }

    private int randInt(){
        return randInt(0,100);
    }


    private boolean try_(int p){
        return (randInt(0,99)>=p)?true:false;
    }
    public boolean step(){
            boolean changed = false;
            for (int i = 1; i < cells.length-1; i++) {
                for (int j = 1; j < cells[i].length-1; j++) {

                        CELL_TYPE before = get(i,j);
                        CELL_TYPE after;
                        int c = 0;
                        if(cells[i-1][j-1]==CELL_TYPE.ROCK)c++;
                        if(cells[i][j-1]==CELL_TYPE.ROCK)c++;
                        if(cells[i+1][j-1]==CELL_TYPE.ROCK)c++;
                        if(cells[i-1][j]==CELL_TYPE.ROCK)c++;
                        if(cells[i][j]==CELL_TYPE.ROCK)c++;
                        if(cells[i+1][j]==CELL_TYPE.ROCK)c++;
                        if(cells[i-1][j+1]==CELL_TYPE.ROCK)c++;
                        if(cells[i][j+1]==CELL_TYPE.ROCK)c++;
                        if(cells[i+1][j+1]==CELL_TYPE.ROCK)c++;
                        if(c>=step_thresh) after=CELL_TYPE.ROCK;
                        else after = CELL_TYPE.FREE;

                        set(i,j,after);
                        if(changed==false){
                            if(before!=after)changed=true;
                        }

                    }
                }

            return changed;
        }



    private CELL_TYPE get(int i, int j){
        return cells[i][j];
    }

    private void set(int i, int j, CELL_TYPE t){
        cells[i][j] = t;
    }

}
