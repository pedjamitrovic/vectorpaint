package simplepaint;

import com.sun.javafx.geom.Vec2d;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JPanel;

public class Line extends Paintable
{
    private Color color;
    private int stroke;
    private Point from;
    private Point to;

    public Line(JPanel canvas) { this(canvas, true); }
    public Line(JPanel canvas, boolean start)
    {
        super(canvas);
        if (start) super.start();
    }
    public Line(JPanel canvas, Color color, int stroke) { this(canvas, color, stroke, true); }
    public Line(JPanel canvas, Color color, int stroke, boolean start)
    {
        super(canvas);
        this.color = color; this.stroke = stroke;
        if (start) super.start();
    }
    public Line(JPanel canvas, Point from, Point to) { this(canvas, from, to, false); }
    public Line(JPanel canvas, Point from, Point to, boolean enlist)
    {
        super(canvas);
        super.start();
        super.addCharacteristicPoint(this.from = from); super.addCharacteristicPoint(this.to = to);
        super.stop();
        if (enlist) super.enlist();
    }
    public Line(JPanel canvas, Color color, int stroke, Point from, Point to) { this(canvas, color, stroke, from, to, false); }
    public Line(JPanel canvas, Color color, int stroke, Point from, Point to, boolean enlist)
    {
        super(canvas);
        super.start();
        this.color = color; this.stroke = stroke; super.addCharacteristicPoint(this.from = from); super.addCharacteristicPoint(this.to = to);
        super.stop();
        if (enlist) super.enlist();
    }

    public Color getColor() { if (color == null) color = Action.getSelectedColor(); return color; }
    public void setColor(Color color) { this.color = color; }
    public int getStroke() { if (stroke == 0) stroke = Action.getSelectedWidth(); return stroke; }
    public void setStroke(int stroke) { this.stroke = stroke; }
    public Point getFrom() { return from; }
    public Point getTo() { return to; }

    @Override public void move(Vec2d offset)
    {
        super.move(offset);
        from.setLocation(from.x + offset.x, from.y + offset.y);
        to.setLocation(to.x + offset.x, to.y + offset.y);
    }
    @Override public void paint(Graphics g)
    {
        Graphics2D gf = (Graphics2D)g;
        gf.setColor(getColor());
        gf.setStroke(new BasicStroke(getStroke()));
        gf.drawLine(from.x, from.y, to.x, to.y);
    }

    @Override public void mousePressed(MouseEvent e)
    {
        super.mousePressed(e);
        if (!super.isPainting()) return;
        mouseStartPoint = e.getPoint();
        super.addCharacteristicPoint(from = e.getPoint());
    }
    @Override public void mouseReleased(MouseEvent e)
    {
        super.mouseReleased(e);
        if (!super.isPainting()) return;
        super.addCharacteristicPoint(to = e.getPoint());
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

        LinkedList<Line> additional = new LinkedList<Line>();
        additional.add(new Line(super.getCanvas(), getColor(), getStroke(), from, e.getPoint(), false));
        Paintable.refresh(super.getCanvas(), additional);

        //Paintable.refresh(super.getCanvas());
        //Graphics2D g = (Graphics2D)super.getCanvas().getGraphics();
        //g.setColor(getColor());
        //g.setStroke(new BasicStroke(getStroke()));
        //g.drawLine(from.x, from.y, e.getPoint().x, e.getPoint().y);
    }
    @Override public String toString()
    {
        StringBuffer sb = new StringBuffer("line ");
        sb.append(color.getRGB() + " " + stroke + " " + from.x + " " + from.y + " " + to.x + " " + to.y);
        return sb.toString();
    }
}
