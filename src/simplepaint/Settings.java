package simplepaint;

import javax.swing.ImageIcon;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;

public final class Settings
{
    private static int width = 1024;
    private static int height = 700;
    private static int actionPanelRows = 1;
    private static int actionPanelColumns = 6;
    private static int colorsPanelRows = 2;
    private static int colorsPanelColumns = 8;
    private static int undoredoPanelRows = 1;
    private static int undoredoPanelColumns = 2;
    private static int statusPanelRows = 1;
    private static int statusPanelColumns = 5;
    private static int selectedColorLabelIconDimension = 20;
    private static int rectWrapperStep = 3;
    private static Color rectWrapperColor = Color.BLACK;
    private static Stroke rectWrapperStroke = new BasicStroke(1);
    private static int rectWrapperSquareWidth = 4;
    private static int rectWrapperSquareHeight = 4;

    private static String mainWindowTitle = "Simple Paint";
    private static String fileMenuLabel = "File";
    private static String helpMenuLabel = "Help";
    private static String newMenuItemLabel = "New";
    private static String openMenuItemLabel = "Open";
    private static String saveMenuItemLabel = "Save";
    private static String saveAsMenuItemLabel = "Save as";
    private static String closeMenuItemLabel = "Close";
    private static String exitMenuItemLabel = "Exit";
    private static String authorMenuItemLabel = "Author";
    private static String authorName = "Author of program: Predrag Mitrovic 2015/0608";

    private static Point rectWrapperOffset = new Point(-2, -2);
    private static Dimension rectWrapperExtend = new Dimension(4, 4);

    private static Color[] colors = {Color.BLACK, Color.GRAY, new Color(220,20,60), Color.RED,  Color.ORANGE, Color.YELLOW, Color.BLUE, Color.CYAN,
            Color.WHITE, new Color(211,211,211) ,new Color(165,42,42) ,new Color(128,0,128), Color.MAGENTA, Color.PINK ,
            new Color(0,100,0), Color.GREEN
    };

    public static Point getRectWrapperOffset() { return rectWrapperOffset; }
    public static Dimension getRectWrapperExtend() { return rectWrapperExtend; }

    public static int getWidth() { return width; }
    public static int getHeight() { return height; }

    public static int getActionPanelRows() { return actionPanelRows; }
    public static int getActionPanelColumns() { return actionPanelColumns; }
    public static Dimension getActionPanelMaxSize() { return new Dimension(200,40); }

    public static int getColorsPanelRows() { return colorsPanelRows; }
    public static int getColorsPanelColumns() { return colorsPanelColumns; }
    public static Dimension getColorsPanelMaxSize() { return new Dimension(200,40); }

    public static int getStatusPanelRows() { return statusPanelRows; }
    public static int getStatusPanelColumns() { return statusPanelColumns; }

    public static int getSelectedColorLabelIconDimension() { return selectedColorLabelIconDimension; }

    public static Dimension getLineWidthComboMaxSize() { return new Dimension(80,40); }

    public static Color getColor(int i) { return colors[i]; }
    public static int getUndoRedoPanelRows() { return undoredoPanelRows; }
    public static int getUndoRedoPanelColumns() { return undoredoPanelColumns; }

    public static Dimension getUndoRedoPanelMaxSize() { return new Dimension(65,40); }

    public static int getRectWrapperStep() { return rectWrapperStep; }
    public static Color getRectWrapperColor() { return rectWrapperColor; }
    public static Stroke getRectWrapperStroke() { return rectWrapperStroke; }
    public static int getRectWrapperSquareWidth() { return rectWrapperSquareWidth; }
    public static int getRectWrapperSquareHeight() { return rectWrapperSquareHeight; }

    public static ImageIcon getImageIconResource(String name) { return new ImageIcon("res\\" + name); }

    public static String getString(String name)
    {
        switch (name.toLowerCase())
        {
            case "mainwindowtitle": return mainWindowTitle;
            case "filemenulabel": return fileMenuLabel;
            case "helpmenulabel": return helpMenuLabel;
            case "newmenuitemlabel": return newMenuItemLabel;
            case "openmenuitemlabel": return openMenuItemLabel;
            case "savemenuitemlabel": return saveMenuItemLabel;
            case "saveasmenuitemlabel": return saveAsMenuItemLabel;
            case "closemenuitemlabel": return closeMenuItemLabel;
            case "exitmenuitemlabel": return exitMenuItemLabel;
            case "authormenuitemlabel": return authorMenuItemLabel;
            case "authorname": return authorName;
        }
        return null;
    }

    private Settings() { }
}