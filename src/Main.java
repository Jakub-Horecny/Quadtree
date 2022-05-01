import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return this.x; }
    public double getY() { return this.y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}

class Rectangle {
    private double x;
    private double y;
    private double w;
    private double h;

    public Rectangle(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getW() { return this.w; }
    public double getH() { return this.h; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setW(double w) { this.w = w; }
    public void setH(double h) { this.h = h; }

    public boolean contains(Point point) {
        return (point.getX() >= (this.x - this.w) &&
                point.getX() <= (this.x + this.w) &&
                point.getY() >= (this.y - this.h) &&
                point.getY() <= (this.y + this.h));
    }

    public boolean intersect(double rangeX, double rangeY, double rangeW, double rangeH){
        return !(rangeX - rangeW > this.x + this.w ||
                rangeX + this.w < this.x - this.w ||
                rangeY - rangeH > this.y + this.h ||
                rangeY + this.h < this.y - this.h);
    }
}

class Quadtree {

    // ohranicenie, aká veľká je plocha na ktorej je strom
    private Rectangle boundary;
    private int capacity;
    private ArrayList<Point> points;
    private boolean divided;

    private Quadtree northWest;
    private Quadtree northEast;
    private Quadtree southWest;
    private Quadtree southEast;

    public Quadtree(Rectangle boundary, int capacity) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.points = new ArrayList<>(this.capacity);
        this.divided = false;
    }

    /*
    * Vloží nový bod od stromu
    * */
    public boolean insert(Point point) {
        if (!this.boundary.contains(point)){
            return false;
        }

        // kontrola ci je počet bodov v strome menej < capacita
        if (this.points.size() < this.capacity) {
            this.points.add(point);
            return true;
        } else {
            if (!this.divided){
                // rozdelenie stromu, ak ešte nebol rozdelený
                this.subdivide();
            }

            if (this.northEast.insert(point)) {
                return true;
            } else if (this.northWest.insert(point)) {
                return true;
            }  else if (this.southEast.insert(point)) {
                return true;
            } else if (this.southWest.insert(point)) {
                return true;
            }
        }
        return false;
    }

    public void query(ArrayList<Point> found, double rangeX, double rangeY, double rangeW, double rangeH){

        if (!this.boundary.intersect(rangeX, rangeY, rangeW, rangeH)) {
            return;
        } else {
            // prejdem vsetky body a zistim ci patria do rozsahu
            for (Point p: this.points) {
                if (this.boundary.contains(p)) {
                    found.add(p);
                }
            }
            if (this.divided) {
                this.northWest.query(found, rangeX, rangeY, rangeW, rangeH);
                this.northEast.query(found, rangeX, rangeY, rangeW, rangeH);
                this.southWest.query(found, rangeX, rangeY, rangeW, rangeH);
                this.southEast.query(found, rangeX, rangeY, rangeW, rangeH);
            }
        }
    }
    /*
    * keď je kapacita plná, rozdelí strom na 4 časti
    * */
    private void subdivide() {
        /*double x = this.boundary.getX();
        double y = this.boundary.getY();
        double w = this.boundary.getW();
        double h = this.boundary.getH();*/

        // treba prepočítať súradnice
        Rectangle ne = new Rectangle(this.boundary.getX() + this.boundary.getW()/2,
                                     this.boundary.getY() - this.boundary.getH()/2,
                                     this.boundary.getW()/2,
                                     this.boundary.getH()/2);

        Rectangle nw = new Rectangle(this.boundary.getX() - this.boundary.getW()/2,
                                     this.boundary.getY() - this.boundary.getH()/2,
                                     this.boundary.getW()/2,
                                     this.boundary.getH()/2);

        Rectangle se = new Rectangle(this.boundary.getX() + this.boundary.getW()/2,
                                     this.boundary.getY() + this.boundary.getH()/2,
                                     this.boundary.getW()/2,
                                     this.boundary.getH()/2);

        Rectangle sw = new Rectangle(this.boundary.getX() - this.boundary.getW()/2,
                                     this.boundary.getY() + this.boundary.getH()/2,
                                     this.boundary.getW()/2,
                                     this.boundary.getH()/2);

        this.northWest = new Quadtree(nw, this.capacity);
        this.northEast = new Quadtree(ne, this.capacity);
        this.southWest = new Quadtree(sw, this.capacity);
        this.southEast = new Quadtree(se, this.capacity);
        this.divided = true;
    }

    public Rectangle getBoundary() { return this.boundary; }

    public void setBoundary(Rectangle boundary) { this.boundary = boundary; }
}

//public class Main extends Canvas{
//
//    static Rectangle b = new Rectangle(200,200,200,200);
//    static Quadtree t = new Quadtree(b, 4);
//    public static void main(String[] args) {
//        JFrame f = new JFrame();//creating instance of JFrame
//        Canvas c = new Main();
//
//        Draw d = new Draw();
//        f.pack();
//        f.setSize(400,500);//400 width and 500 height
//        f.setLayout(null);//using no layout managers
//        f.setVisible(true);//making the frame visible
//        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        f.add(c);
//
//    }
//    public void paint(Graphics g) {
//        g.fillOval(100, 100, 200, 200);
//    }
//}

//public class Main extends Canvas {
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("My Drawing");
//
//        Canvas canvas = new Main();
//        canvas.setSize(400, 400);
//
//        frame.add(canvas);
//
//        frame.pack();
//        frame.setVisible(true);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//    }
//
//    public void paint(Graphics g) {
//        g.fillOval(10, 10, 10, 10);
//    }
//}

public class Main extends JPanel  {

    static Rectangle b;
    static Quadtree t;
    static int size;
    static double[] xCor;
    static double[] yCor;

    public Main(int size) {
        Main.b = new Rectangle(0,0,400,400);
        Main.t = new Quadtree(b, 4);
        Main.size = size;
        Main.xCor = new double[Main.size];
        Main.yCor = new double[Main.size];

        for (int i = 0; i < Main.size; i++) {
            Main.xCor[i] = (int) (Math.random() * 400);
            Main.yCor[i] = (int) (Math.random() * 400);
        }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < Main.size; i++) {
            drawPoint(g, (int) Main.xCor[i], (int) Main.yCor[i]);
        }
        drawRect(g,100,100,100,100);
        drawRect(g,0,0,400,400);
    }
    public void drawPoint(@NotNull Graphics g, int x, int y) {
        Color c = new Color(0, 0, 0);
        g.setColor(c);
        //g.drawLine(10, 20,x + 20, y  +20);
        //g.fillRect((int) (Math.random() * x), (int) (Math.random() * y), 5, 5);
        g.fillRect(x, y, 5, 5);
        //g.drawRect(100,100,50,50);
    }

    public void drawRect(@NotNull Graphics g, int x, int y, int w, int h) {
        Color c = new Color(0, 0, 0);
        g.setColor(c);
        g.drawRect(x,y,w,h);
    }
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().add(new Main(200), BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 400);
        f.setVisible(true);

        for (int i = 0; i < Main.size; i++) {
            Point p = new Point(Main.xCor[i], Main.yCor[i]);
            t.insert(p);
        }
        ArrayList<Point> res = new ArrayList<>();
        t.query(res, 100, 100, 100,100);
        System.out.println(res.size());
        for (Point p: res) {
            System.out.println("x " + p.getX() + " y " + p.getY());
        }

/*        for (int i = 0; i < 50; i++) {
            Point p = new Point((int) (Math.random() * 199),
                    (int) (Math.random() * 199));
            t.insert(p);
        }
        ArrayList<Point> res = new ArrayList<>();
        t.query(res, 25, 25, 25,25);
        System.out.println(res.size());
        for (Point p: res) {
            System.out.println("x: " + p.getX() + " y: " + p.getY());
        }*/
    }
}