package simplepaint;

import javax.swing.*;
import java.util.LinkedList;
import java.awt.Color;
import java.util.ListIterator;

public abstract class Action
{
    public enum Tool
    {
        SELECT, DELETE, LINE, POLYLINE, POLYGON, RECTANGLE
    }

    private static Tool selectedTool = Tool.SELECT;
    private static int selectedWidth = 1;
    private static Color selectedColor = Settings.getColor(0);
    private static String savedLocation = null;

    public static Paintable createPaintable(JPanel canvas)
    {
        if (selectedTool == Tool.LINE) return new Line(canvas);
        else if (selectedTool == Tool.POLYLINE) return new Polyline(canvas);
        else if (selectedTool == Tool.POLYGON) return new Polygon(canvas);
        else if (selectedTool == Tool.RECTANGLE) return new Rectangle(canvas);
        return null;
    }

    public static Tool getSelectedTool() { return selectedTool; }
    public static void setSelectedTool(Tool selectedTool)
    {
        Action.selectedTool = selectedTool;
        Program.getMainWindow().setSelectedToolLabel();
    }
    public static int getSelectedWidth() { return selectedWidth; }
    public static void setSelectedWidth(int selectedWidth)
    {
        Action.selectedWidth = selectedWidth;
        Program.getMainWindow().setSelectedWidthLabel();
    }
    public static Color getSelectedColor() { return selectedColor; }
    public static void setSelectedColor(Color selectedColor)
    {
        Action.selectedColor = selectedColor;
        Program.getMainWindow().setSelectedColorLabel();
    }
    public static String getSavedLocation() { return savedLocation; }
    public static void setSavedLocation(String savedLocation)
    {
        Action.savedLocation = savedLocation;
        if (savedLocation == null) savedLocation = "Untitled";
        MainWindow mw = Program.getMainWindow();
        mw.setTitle(Settings.getString("MainWindowTitle") + " - " + savedLocation.substring(savedLocation.lastIndexOf("\\") + 1));
        if (!mw.isSaved()) mw.setTitle(mw.getTitle() + " *");
    }

    private static LinkedList<Action> actions = new LinkedList<Action>();
    private static ListIterator<Action> top;

    public static void clearActions()
    {
        actions.clear(); top = null;
        Program.getMainWindow().setUndoEnabled(false);
        Program.getMainWindow().setRedoEnabled(false);
    }
    public static void addAction(Action action)
    {
        if (actions.size() > 0 && top != null) actions.subList(top.nextIndex(), actions.size()).clear();
        actions.addLast(action);
        top = actions.listIterator(actions.size());
        Program.getMainWindow().setSaved(false);
        Program.getMainWindow().setUndoEnabled(true);
        Program.getMainWindow().setRedoEnabled(false);
    }
    public static void undoAction()
    {
        if (top.hasPrevious())
        {
            top.previous().undo();
            if (!top.hasPrevious()) Program.getMainWindow().setUndoEnabled(false);
            Program.getMainWindow().setRedoEnabled(true);
        }

        Program.getMainWindow().setSaved(false);
    }
    public static void redoAction()
    {
        if (top.hasNext())
        {
            top.next().redo();
            if (!top.hasNext()) Program.getMainWindow().setRedoEnabled(false);
            Program.getMainWindow().setUndoEnabled(true);
        }
        Program.getMainWindow().setSaved(false);
    }

    public abstract void undo();
    public abstract void redo();
}
