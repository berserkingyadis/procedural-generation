package app;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

public class Cave extends JFrame {

    private CELL_TYPE cells[][];
    private int scale;
    private int width;
    private int height;
    private int step_thresh;


    private final Color rock = new Color(128, 128, 128);
    private final Color free = Color.WHITE;
    private final Color stump = new Color(77,26,0);
    private final Color mid = new Color(153, 51, 0);
    private final Color top = new Color(0, 102, 0);

    public void savePNG() {
        BufferedImage image = new BufferedImage(width*scale, height*scale, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();

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

        try {
            ImageIO.write(image, "PNG", new File("hi.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private enum CELL_TYPE {
        ROCK,
        FREE,
        TREE_STUMP,
        TREE_MID,
        TREE_TOP,
        NO_TREE
    }

    public void reset(){
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = (try_(50))?CELL_TYPE.FREE:CELL_TYPE.ROCK;
            }
        }
    }
    public Cave(int width, int height, int scale, int step_thresh) {
        cells = new CELL_TYPE[width][height];
        this.width=width;
        this.height=height;
        this.scale = scale;
        this.step_thresh = step_thresh;

        reset();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON1){
                    step();
                    repaint();
                } else if ( e.getButton()==MouseEvent.BUTTON3){
                    stepX();
                    repaint();
                }

            }
        });

        this.setSize(width*scale, height*scale);
        this.setTitle("Cave Generator - left click to step, right click to redo");
        this.setResizable(false);
        this.setVisible(true);
    }


    @Override
    public void paint(Graphics g) {
       Graphics2D g2d = (Graphics2D)g;

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



    public void stepX(int s){
        for (int n = 0; n < s; n++) {

            for (int i = 0; i < cells.length; i++) {
                for (int j = 1; j < cells[0].length-1; j++) {

                    CELL_TYPE here = get(i, j);
                    CELL_TYPE under = get(i, j+1);


                   if(here!=CELL_TYPE.FREE)continue;

                    switch (under) {
                        case ROCK:
                            if(try_(40))set(i,j, CELL_TYPE.TREE_STUMP);
                            else set(i,j, CELL_TYPE.NO_TREE);
                            break;
                        case TREE_STUMP:
                            if(try_(70))set(i,j, CELL_TYPE.TREE_MID);
                            else set(i,j, CELL_TYPE.TREE_TOP);
                            break;
                        case TREE_MID:
                            if(try_(50))set(i,j, CELL_TYPE.TREE_MID);
                            else set(i,j, CELL_TYPE.TREE_TOP);
                            break;
                    }

                }
            }
        }
        repaint();
    }


    private int randInt(int low, int high){
        return ThreadLocalRandom.current().nextInt(low,high+1);
    }
    private boolean try_(int p){
        return (randInt(0,99)>=p)?true:false;
    }
    public void stepX(){
        stepX(1);
    }
    public void step(int s){
        for (int n = 0; n < s; n++) {

            for (int i = 1; i < cells.length-1; i++) {
                for (int j = 1; j < cells[i].length-1; j++) {

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
                        if(c>=step_thresh)cells[i][j]=CELL_TYPE.ROCK;
                        else cells[i][j]=CELL_TYPE.FREE;

                    }
                }
            }
            repaint();
        }

    public void step(){
        step(1);
    }


    private CELL_TYPE get(int i, int j){
        return cells[i][j];
    }

    private void set(int i, int j, CELL_TYPE t){
        cells[i][j] = t;
    }

}
