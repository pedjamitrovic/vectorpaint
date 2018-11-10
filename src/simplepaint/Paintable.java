package simplepaint;

import com.sun.javafx.geom.Vec2d;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

public abstract class Paintable extends MouseInputAdapter implements PaintableAction
{
    private static LinkedList<Paintable> paintables = new LinkedList<Paintable>();
    private static MoveAction moveAction;
    protected static Point mouseStartPoint, mouseEndPoint;

    public static void newPainting()
    {
        if (!Program.getMainWindow().isSaved())
        {
            int saveOption = JOptionPane.showOptionDialog(null, "Painting not saved. Would you like to save it?", "Save painting?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(Settings.getImageIconResource("Question_50px.png").getImage()), null, null);
            if (saveOption == JOptionPane.YES_OPTION) savePainting(false);
            else if(saveOption == JOptionPane.NO_OPTION)
            {
                Action.setSavedLocation(null);
                savePainting(true);
                Program.getMainWindow().setSaved(true);
                Action.clearActions();
                paintables.clear();
                refresh(Program.getMainWindow().getCanvas());
            }
            else if(saveOption == JOptionPane.CANCEL_OPTION || saveOption == JOptionPane.CLOSED_OPTION) return;
        }
        Action.setSavedLocation(null);
        Program.getMainWindow().setSaved(true);
        Action.clearActions();
        paintables.clear();
        refresh(Program.getMainWindow().getCanvas());
    }
    public static void savePainting(boolean saveAs)
    {
        if (!saveAs && Program.getMainWindow().isSaved()) return;

        File outputFile;

        if (saveAs || Action.getSavedLocation()==null)
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (fileChooser.showDialog(null, "Save") == JFileChooser.CANCEL_OPTION) return;
            outputFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
        }
        else outputFile = new File(Action.getSavedLocation());

        StringBuffer sb = new StringBuffer();
        for(Paintable p: paintables) sb.append(p.toString() + System.lineSeparator());
        try
        {
            FileOutputStream os = new FileOutputStream(outputFile);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            Writer w = new BufferedWriter(osw);
            w.write(sb.toString());
            w.close();
            Action.setSavedLocation(outputFile.getAbsolutePath());
            Program.getMainWindow().setSaved(true);
        }
        catch (IOException e) { System.err.println("Problem writing to the file."); }
    }
    public static void loadPainting()
    {
        if (!Program.getMainWindow().isSaved())
        {
            int saveOption = JOptionPane.showOptionDialog(null, "Painting not saved. Would you like to save it?", "Save painting?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(Settings.getImageIconResource("Question_50px.png").getImage()), null, null);
            if (saveOption == JOptionPane.YES_OPTION) savePainting(false);
            else if(saveOption == JOptionPane.CANCEL_OPTION || saveOption == JOptionPane.CLOSED_OPTION) return;
        }

        File inputFile;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showDialog(null, "Open") == JFileChooser.CANCEL_OPTION) return;
        inputFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            Paintable paintable = null;
            Action.clearActions();
            paintables.clear();
            try { for (String line; (line = br.readLine()) != null; ) fromString(line); }
            finally { br.close(); }
            refresh(Program.getMainWindow().getCanvas());
            Action.setSavedLocation(inputFile.getAbsolutePath());
            Program.getMainWindow().setSaved(true);
        }
        catch(FileNotFoundException e){ System.err.println("Problem reading from the file."); }
        catch(IOException e){ System.err.println("Problem reading from the file."); }
    }
    public static void closePainting()
    {
        if (!Program.getMainWindow().isSaved() && JOptionPane.showOptionDialog(null, "Painting not saved. Would you like to save it?", "Save painting?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(Settings.getImageIconResource("Question_50px.png").getImage()), null, null) == JOptionPane.OK_OPTION) savePainting(false);
        Action.setSavedLocation(null);
        Program.getMainWindow().setSaved(true);
        Action.clearActions();
        paintables.clear();
        refresh(Program.getMainWindow().getCanvas());
    }

    public static Paintable fromString(String str)
    {
        String[] parts = str.split(" ");
        switch(parts[0]){
            case "line":
                return new Line(Program.getMainWindow().getCanvas(), new Color(Integer.parseInt(parts[1])), Integer.parseInt(parts[2]), new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])), new Point(Integer.parseInt(parts[5]), Integer.parseInt(parts[6])), true);
            case "polyline":
                LinkedList<Line> lines = new LinkedList<Line>();
                for(int i = 1; i + 6 < parts.length; i+=7){
                    if(parts[i].equals("line")) lines.add(new Line(Program.getMainWindow().getCanvas(), new Color(Integer.parseInt(parts[i+1])), Integer.parseInt(parts[i+2]), new Point(Integer.parseInt(parts[i+3]), Integer.parseInt(parts[i+4])), new Point(Integer.parseInt(parts[i+5]), Integer.parseInt(parts[i+6])), false));
                }
                return new Polyline(Program.getMainWindow().getCanvas(), lines, true);
            case "polygon":
                lines = new LinkedList<Line>();
                for(int i = 1; i + 6 < parts.length; i+=7){
                    if(parts[i].equals("line")) lines.add(new Line(Program.getMainWindow().getCanvas(), new Color(Integer.parseInt(parts[i+1])), Integer.parseInt(parts[i+2]), new Point(Integer.parseInt(parts[i+3]), Integer.parseInt(parts[i+4])), new Point(Integer.parseInt(parts[i+5]), Integer.parseInt(parts[i+6])), false));
                }
                return new Polygon(Program.getMainWindow().getCanvas(), lines, true);
            case "rectangle":
                return new Rectangle(Program.getMainWindow().getCanvas(), new Color(Integer.parseInt(parts[1])), Integer.parseInt(parts[2]), new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])), new Dimension(Integer.parseInt(parts[5]), Integer.parseInt(parts[6])), true);
            default: return null;
        }
    }

    public static void refresh(JPanel canvas)
    {
        refresh(canvas, null);
    }

    public static void refresh(JPanel canvas, LinkedList<? extends Paintable> additional)
    {
        BufferedImage buffer = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics(); repaint(g2d);
        if (additional != null)
        {
            for (Paintable paintable : additional)
            {
                paintable.paint(g2d);
                if (paintable.isSelected()) paintable.activateSelection(g2d);
            }
        }
        canvas.getGraphics().drawImage(buffer, 0, 0, Color.WHITE, null);

        //canvas.getGraphics().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        //repaint(canvas.getGraphics());
    }
    public static void repaint(Graphics g)
    {
        for (Paintable paintable : paintables)
        {
            paintable.paint(g);
            if (paintable.isSelected()) paintable.activateSelection((Graphics2D)g);
        }
    }

    public static Paintable setSelection(Point mouseClickPosition, boolean dontDrop)
    {
        Paintable current = Program.getMainWindow().getCurrent();

        if (dontDrop && current != null && current.isSelected() && current.rectWrapper.containsWrapped(mouseClickPosition)) return current;

        boolean wasSelected = false;

        if (current != null)
        {
            wasSelected = current.isSelected();
            current.deselect();
        }

        if (current != null && wasSelected && current.rectWrapper.containsWrapped(mouseClickPosition))
        {
            boolean found = false;

            for (Paintable paintable : paintables)
            {
                if (!found) { found = paintable == current; continue; }
                if (paintable.rectWrapper.containsWrapped(mouseClickPosition)) return paintable.select();
            }

            //return current.select();
        }

        for (Paintable paintable : paintables)
            if (paintable.rectWrapper.containsWrapped(mouseClickPosition))
                return paintable.select();

        return null;

    }
    public static void deletePaintable(Point mouseClickPosition)
    {
        Paintable current = Program.getMainWindow().getCurrent();

        if (current == null || !current.isSelected() || !current.rectWrapper.containsWrapped(mouseClickPosition))
        {
            for (ListIterator<Paintable> it = paintables.listIterator(); it.hasNext(); )
            {
                if ((current = it.next()).rectWrapper.containsWrapped(mouseClickPosition))
                {
                    it.remove();
                    Action.addAction(new EraseAction(current));
                    Program.getMainWindow().setCurrent(null);
                    refresh(Program.getMainWindow().getCanvas());
                    return;
                }
            }
        }
        else
        {
            paintables.removeFirstOccurrence(current);
            Action.addAction(new EraseAction(current));
            Program.getMainWindow().setCurrent(null);
        }
    }

    private JPanel canvas;
    private RectWrapper rectWrapper; // = null;
    private boolean painting; // = false;
    private boolean selected; // = false;
    private Point mouse; // = null;

    protected Paintable(JPanel canvas)
    {
        this.canvas = canvas;
    }

    public final JPanel getCanvas() { return canvas; }
    protected final RectWrapper getRectWrapper() { return rectWrapper; }

    protected final void start() { painting = true; }
    protected final void stop() { painting = false; }
    protected final void enlist() { paintables.addFirst(this); }
    protected final void addCharacteristicPoint(Point cPoint)
    {
        if (!painting) return;

        if (rectWrapper != null)
        {
            if (cPoint.x < rectWrapper.x)
            {
                rectWrapper.setSize(rectWrapper.x + rectWrapper.width - cPoint.x, rectWrapper.height);
                rectWrapper.setLocation(cPoint.x, rectWrapper.y);
            }
            else if (cPoint.x > rectWrapper.x + rectWrapper.width) rectWrapper.setSize(cPoint.x - rectWrapper.x, rectWrapper.height);

            if (cPoint.y < rectWrapper.y)
            {
                rectWrapper.setSize(rectWrapper.width, rectWrapper.y + rectWrapper.height - cPoint.y);
                rectWrapper.setLocation(rectWrapper.x, cPoint.y);
            }
            else if (cPoint.y > rectWrapper.y + rectWrapper.height) rectWrapper.setSize(rectWrapper.width, cPoint.y - rectWrapper.y);
        }
        else rectWrapper = new RectWrapper(cPoint, new Dimension(0, 0));
    }

    public final boolean isPainting() { return painting; }
    public final boolean isSelected()
    {
        return selected;
    }

    @Override public final void add()
    {
        paintables.addLast(this);
        paint(canvas.getGraphics());
    }
    @Override public final void remove()
    {
        paintables.removeFirstOccurrence(this);
        refresh(canvas);
    }

    public Paintable select()
    {
        selected = true;
        activateSelection((Graphics2D)canvas.getGraphics());
        Program.getMainWindow().setSpecificLabelText(rectWrapper.getWrappedSize().width + ", " + rectWrapper.getWrappedSize().height + "px");
        return this;
    }
    public Paintable deselect()
    {
        selected = false;
        refresh(canvas);
        Program.getMainWindow().setSpecificLabelText("");
        return this;
    }

    protected final void activateSelection(Graphics2D g)
    {
        Point p = rectWrapper.getWrappedLocation();
        Dimension d = rectWrapper.getWrappedSize();
        //Graphics2D g = (Graphics2D)(canvas.getGraphics());
        g.setColor(Settings.getRectWrapperColor());
        g.setStroke(Settings.getRectWrapperStroke());
        for(int x = p.x; x < p.x + (int)d.getWidth(); x += 2*Settings.getRectWrapperStep()) {
            g.drawLine(x, p.y, Math.min(x + Settings.getRectWrapperStep(), p.x + (int)d.getWidth()) , p.y);
            g.drawLine(x, p.y + (int)d.getHeight(), Math.min(x + Settings.getRectWrapperStep(), p.x + (int)d.getWidth()), p.y + (int)d.getHeight());
        }
        for(int y = p.y; y < p.y + (int)d.getHeight(); y += 2*Settings.getRectWrapperStep()) {
            g.drawLine(p.x, y, p.x, Math.min(y + Settings.getRectWrapperStep(), p.y + (int)d.getHeight()));
            g.drawLine(p.x + (int)d.getWidth(), y , p.x + (int)d.getWidth(), Math.min(y + Settings.getRectWrapperStep(), p.y + (int)d.getHeight()));
        }

        g.fillRect(p.x - Settings.getRectWrapperSquareWidth()/2, p.y - Settings.getRectWrapperSquareHeight()/2, Settings.getRectWrapperSquareWidth(),Settings.getRectWrapperSquareHeight());
        g.fillRect(p.x - Settings.getRectWrapperSquareWidth()/2, p.y + (int)d.getHeight() + 1 - Settings.getRectWrapperSquareHeight()/2, Settings.getRectWrapperSquareWidth(),Settings.getRectWrapperSquareHeight());
        g.fillRect(p.x + (int)d.getWidth() - 1, p.y - Settings.getRectWrapperSquareHeight()/2, Settings.getRectWrapperSquareWidth(),Settings.getRectWrapperSquareHeight());
        g.fillRect(p.x + (int)d.getWidth() - 1, p.y + (int)d.getHeight() + 1 - Settings.getRectWrapperSquareHeight()/2, Settings.getRectWrapperSquareWidth(),Settings.getRectWrapperSquareHeight());
        g.fillRect(p.x + (int)d.getWidth()/2, p.y - Settings.getRectWrapperSquareHeight()/2, Settings.getRectWrapperSquareWidth(),Settings.getRectWrapperSquareHeight());
        g.fillRect(p.x + (int)d.getWidth()/2, p.y + (int)d.getHeight() + 1 - Settings.getRectWrapperSquareHeight()/2, Settings.getRectWrapperSquareWidth(),Settings.getRectWrapperSquareHeight());
        g.fillRect(p.x - Settings.getRectWrapperSquareWidth()/2, p.y + (int)d.getHeight()/2 - 1, Settings.getRectWrapperSquareWidth(),Settings.getRectWrapperSquareHeight());
        g.fillRect(p.x + (int)d.getWidth() - 1, p.y + (int)d.getHeight()/2, Settings.getRectWrapperSquareWidth(),Settings.getRectWrapperSquareHeight());
    }

    @Override public void mousePressed(MouseEvent e)
    {
        if (painting) return;
        mouse = null; moveAction = null;
        if (rectWrapper.containsWrapped(e.getPoint()))
        {
            mouse = e.getPoint();
            moveAction = new MoveAction(this, new Vec2d(mouse.x, mouse.y));
        }
    }
    @Override public void mouseReleased(MouseEvent e)
    {
        if (painting || mouse == null) return;
        Vec2d offset = moveAction.getOffset();
        offset.set(e.getPoint().x - offset.x, e.getPoint().y - offset.y);
        if (offset.x != 0 || offset.y != 0) Action.addAction(moveAction);
    }
    @Override public void mouseDragged(MouseEvent e)
    {
        if (painting || mouse == null) return;
        Point dragged = e.getPoint();
        move(new Vec2d(dragged.x - mouse.x, dragged.y - mouse.y));
        mouse.setLocation(dragged);
        Paintable.refresh(canvas);
    }

    @Override public void move(Vec2d offset)
    {
        rectWrapper.setLocation(rectWrapper.x + (int)offset.x, rectWrapper.y + (int)offset.y);
    }
    @Override public abstract String toString();
    public abstract void paint(Graphics g);
}
