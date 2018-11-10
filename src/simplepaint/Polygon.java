package simplepaint;

import com.sun.javafx.geom.Vec2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public class Polygon extends Paintable
{
    private LinkedList<Line> lines = new LinkedList<Line>();

    public Polygon(JPanel canvas) { this(canvas, true); }
    public Polygon(JPanel canvas, boolean start)
    {
        super(canvas);
        if (start) super.start();
    }
    public Polygon(JPanel canvas, LinkedList<Line> lines) { this(canvas, lines, false); }
    public Polygon(JPanel canvas, LinkedList<Line> lines, boolean enlist)
    {
        super(canvas);
        this.lines = lines;
        super.start();
        for(Line line : lines) super.addCharacteristicPoint(line.getFrom());
        super.addCharacteristicPoint(lines.getLast().getTo());
        super.stop();
        if (enlist) super.enlist();
    }

    public LinkedList<Line> getLines() { return lines; }

    @Override public Color getColor() { return lines.getFirst().getColor(); }
    @Override public void setColor(Color color)
    {
        for (Line line : lines) line.setColor(color);
    }
    @Override public int getStroke() { return lines.getFirst().getStroke(); }
    @Override public void setStroke(int stroke)
    {
        for (Line line : lines) line.setStroke(stroke);
    }
    @Override public void move(Vec2d offset)
    {
        super.move(offset);
        for (Line line : lines) line.move(offset);
    }
    @Override public void paint(Graphics g)
    {
        for (Line line : lines) line.paint(g);
    }
    @Override public void mouseClicked(MouseEvent e)
    {
        if (!super.isPainting()) return;

        mouseStartPoint = e.getPoint();

        Point curr = e.getPoint();
        Point last = (lines.size() > 0 ? lines.getLast().getTo() : null);
        super.addCharacteristicPoint(curr);

        if (last != null)
        {
            Line line = new Line(super.getCanvas(), new Point(last), new Point(curr));
            lines.addLast(line); line.paint(super.getCanvas().getGraphics());
        }
        else lines.addLast(new Line(super.getCanvas(), new Point(-1, -1), new Point(curr)));

        if (e.getClickCount() >= 2)
        {
            lines.removeFirst();
            if (lines.size() > 0)
            {
                Line line = new Line(super.getCanvas(), new Point(lines.getLast().getTo()), new Point(lines.getFirst().getFrom()));
                lines.add(line); line.paint(super.getCanvas().getGraphics());
            }
            mouseStartPoint = mouseEndPoint = null;
            super.stop();
            super.enlist();
            Paintable.refresh(super.getCanvas());
            Program.getMainWindow().setSpecificLabelText("");
            Action.addAction(new DrawAction(this));
        }
    }
    @Override public void mouseMoved(MouseEvent e)
    {
        if (!super.isPainting()) return;

        if (mouseStartPoint != null)
        {
            mouseEndPoint = e.getPoint();
            Program.getMainWindow().setSpecificLabelText(Integer.toString(Math.abs(mouseStartPoint.x - mouseEndPoint.x) + Action.getSelectedWidth()) + ", " + Integer.toString(Math.abs(mouseStartPoint.y - mouseEndPoint.y) + Action.getSelectedWidth()) + "px");
        }

        if (lines.size() == 0) return;

        Line current = new Line(super.getCanvas(), Action.getSelectedColor(), Action.getSelectedWidth(), lines.getLast().getTo(), e.getPoint(), false);
        Line first = lines.removeFirst(); lines.addFirst(current);
        lines.addLast(new Line(super.getCanvas(), Action.getSelectedColor(), Action.getSelectedWidth(), e.getPoint(), first.getTo(), false)
        {
            @Override public void paint(Graphics g)
            {
                Graphics2D gf = (Graphics2D)g;
                gf.setColor(getColor());
                gf.setStroke(new BasicStroke(getStroke(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                gf.drawLine(super.getFrom().x, super.getFrom().y, super.getTo().x, super.getTo().y);
            }
        });
        Paintable.refresh(super.getCanvas(), lines);
        lines.removeLast();
        lines.removeFirst(); lines.addFirst(first);

        //Paintable.refresh(super.getCanvas());
        //Graphics2D g = (Graphics2D)super.getCanvas().getGraphics();
        //for (Line line : lines) if (line.getFrom().x != -1 && line.getFrom().y != -1) line.paint(g);
        //g.setColor(Action.getSelectedColor());
        //g.setStroke(new BasicStroke(Action.getSelectedWidth()));
        //g.drawLine(lines.getLast().getTo().x, lines.getLast().getTo().y, e.getPoint().x, e.getPoint().y);

        //g.setStroke(new BasicStroke(Action.getSelectedWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        //g.drawLine(e.getPoint().x, e.getPoint().y, lines.getFirst().getTo().x, lines.getFirst().getTo().y);
    }
    @Override public String toString()
    {
        StringBuffer sb = new StringBuffer("polygon");
        for (Line line : lines) sb.append(" " + line.toString());
        return sb.toString();
    }
}
