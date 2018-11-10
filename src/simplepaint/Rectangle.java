package simplepaint;

import com.sun.javafx.geom.Vec2d;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JPanel;

public class Rectangle extends Paintable
{
    private Color color;
    private int stroke;
    private Point corner;
    private Dimension length;

    public Rectangle(JPanel canvas) { this(canvas, true); }
    public Rectangle(JPanel canvas, boolean start)
    {
        super(canvas);
        if (start) super.start();
    }
    public Rectangle(JPanel canvas, Color color, int stroke) { this(canvas, color, stroke, true); }
    public Rectangle(JPanel canvas, Color color, int stroke, boolean start)
    {
        super(canvas);
        this.color = color; this.stroke = stroke;
        if (start) super.start();
    }
    public Rectangle(JPanel canvas, Point corner, Dimension length) { this(canvas, corner, length, false); }
    public Rectangle(JPanel canvas, Point corner, Dimension length, boolean enlist)
    {
        super(canvas);
        super.start();
        super.addCharacteristicPoint(this.corner = corner); super.addCharacteristicPoint(new Point(corner.x + (this.length = length).width, corner.y + length.height));
        super.stop();
        if (enlist) super.enlist();
    }
    public Rectangle(JPanel canvas, Color color, int stroke, Point corner, Dimension length) { this(canvas, color, stroke, corner, length, false); }
    public Rectangle(JPanel canvas, Color color, int stroke, Point corner, Dimension length, boolean enlist)
    {
        super(canvas);
        super.start();
        this.color = color; this.stroke = stroke; super.addCharacteristicPoint(this.corner = corner); super.addCharacteristicPoint(new Point(corner.x + (this.length = length).width, corner.y + length.height));
        super.stop();
        if (enlist) super.enlist();
    }

    public Color getColor() { if (color == null) color = Action.getSelectedColor(); return color; }
    public void setColor(Color color) { this.color = color; }
    public int getStroke() { if (stroke == 0) stroke = Action.getSelectedWidth(); return stroke; }
    public void setStroke(int stroke) { this.stroke = stroke; }
    public Point getCorner() { return corner; }
    public Dimension getLength() { return length; }

    @Override public void move(Vec2d offset)
    {
        super.move(offset);
        corner.setLocation(corner.x + offset.x, corner.y + offset.y);
    }
    @Override public void paint(Graphics g)
    {
        Graphics2D gf = (Graphics2D)g;
        gf.setColor(getColor());
        gf.setStroke(new BasicStroke(getStroke()));
        gf.drawRect(corner.x, corner.y, length.width, length.height);
    }

    @Override public void mousePressed(MouseEvent e)
    {
        super.mousePressed(e);
        if (!super.isPainting()) return;
        mouseStartPoint = e.getPoint();
        super.addCharacteristicPoint(corner = e.getPoint());
    }
    @Override public void mouseReleased(MouseEvent e)
    {
        super.mouseReleased(e);
        if (!super.isPainting()) return;
        Point p = e.getPoint();
        length = new Dimension(Math.abs(p.x - corner.x), Math.abs(p.y - corner.y));
        this.corner.setLocation(Math.min(corner.x, p.x), Math.min(corner.y, p.y));
        super.addCharacteristicPoint(e.getPoint());
        super.stop(); super.enlist(); paint(super.getCanvas().getGraphics());
        Program.getMainWindow().setSpecificLabelText("");
        Action.addAction(new DrawAction(this));
    }
    @Override public void mouseDragged(MouseEvent e)
    {
        super.mouseDragged(e);
        if (!super.isPainting()) return;
        mouseEndPoint = e.getPoint();
        Program.getMainWindow().setSpecificLabelText(Integer.toString(Math.abs(mouseStartPoint.x-mouseEndPoint.x)+Action.getSelectedWidth()) + ", " + Integer.toString(Math.abs(mouseStartPoint.y-mouseEndPoint.y)+Action.getSelectedWidth()) + "px");

        LinkedList<Rectangle> additional = new LinkedList<Rectangle>();
        additional.add(new Rectangle(super.getCanvas(), getColor(), getStroke(), new Point(Math.min(corner.x, e.getPoint().x), Math.min(corner.y, e.getPoint().y)), new Dimension(Math.abs(corner.x - e.getPoint().x), Math.abs(corner.y - e.getPoint().y)), false));
        Paintable.refresh(super.getCanvas(), additional);

        //Paintable.refresh(super.getCanvas());
        //Graphics2D g = (Graphics2D)super.getCanvas().getGraphics();
        //g.setColor(getColor());
        //g.setStroke(new BasicStroke(getStroke()));
        //g.drawRect(Math.min(corner.x, e.getPoint().x), Math.min(corner.y, e.getPoint().y), Math.abs(corner.x - e.getPoint().x), Math.abs(corner.y - e.getPoint().y));
    }
    @Override public String toString()
    {
        StringBuffer sb = new StringBuffer("rectangle ");
        sb.append(color.getRGB() + " " + stroke + " " + corner.x+ " " + corner.y + " " + length.width + " " + length.height);
        return sb.toString();
    }
}
