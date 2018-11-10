package simplepaint;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public final class MainWindow extends JFrame implements MouseInputListener
{
    private JMenuBar mainMenu;
    private JToolBar mainToolbar;
    private JLabel specificDimensionLabel;
    private JLabel positionLabel;
    private JLabel selectedToolLabel;
    private JLabel selectedColorLabel;
    private JLabel selectedWidthLabel;
    private JPanel statusPanel;
    private JButton undoButton;
    private JButton redoButton;

    private JPanel canvas;
    private Paintable current;

    private boolean saved = true;

    public JPanel getCanvas() { return canvas; }
    public Paintable getCurrent() { return current; }

    public void setCurrent(Paintable current) { setCurrent(current, false); }
    public void setCurrent(Paintable current, boolean select)
    {
        if (this.current != null) this.current.deselect();
        this.current = current;
        if (select && current != null)
        {
            current.select();
            Paintable.refresh(canvas);
        }
    }

    void onClosing()
    {
        if (!isSaved() && JOptionPane.showOptionDialog(null, "Painting not saved. Would you like to save it?", "Save painting?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(Settings.getImageIconResource("Question_50px.png").getImage()), null, null) == JOptionPane.YES_OPTION) Paintable.savePainting(false);
    }

    private void initializeComponents()
    {
        setTitle(Settings.getString("MainWindowTitle") + " - Untitled");
        setSize(Settings.getWidth(), Settings.getHeight());
        setIconImage((Settings.getImageIconResource("PaintPalette_40px.png")).getImage());
        setMinimumSize(new Dimension(Settings.getWidth(), Settings.getHeight()));
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { onClosing(); } });

        /*Menubar*/
        mainMenu = new JMenuBar();
        JMenu fileMenu = new JMenu(Settings.getString("FileMenuLabel"));
        JMenu helpMenu = new JMenu(Settings.getString("HelpMenuLabel"));
        JMenuItem newMenuItem = new JMenuItem(Settings.getString("NewMenuItemLabel"));
        newMenuItem.addActionListener(e -> Paintable.newPainting());
        JMenuItem openMenuItem = new JMenuItem(Settings.getString("OpenMenuItemLabel"));
        openMenuItem.addActionListener(e -> Paintable.loadPainting());
        JMenuItem saveMenuItem = new JMenuItem(Settings.getString("SaveMenuItemLabel"));
        saveMenuItem.addActionListener(e -> Paintable.savePainting(false));
        JMenuItem saveAsMenuItem = new JMenuItem(Settings.getString("SaveAsMenuItemLabel"));
        saveAsMenuItem.addActionListener(e -> Paintable.savePainting(true));
        JMenuItem closeMenuItem = new JMenuItem(Settings.getString("CloseMenuItemLabel"));
        closeMenuItem.addActionListener(e -> Paintable.closePainting());
        JMenuItem exitMenuItem = new JMenuItem(Settings.getString("ExitMenuItemLabel"));
        exitMenuItem.addActionListener(e -> dispatchEvent(new WindowEvent(MainWindow.this, WindowEvent.WINDOW_CLOSING)));
        JMenuItem authorMenuItem = new JMenuItem(Settings.getString("AuthorMenuItemLabel"));
        authorMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(null, Settings.getString("AuthorName"), Settings.getString("AuthorMenuItemLabel"), JOptionPane.INFORMATION_MESSAGE));

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(closeMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        helpMenu.add(authorMenuItem);
        mainMenu.add(fileMenu);
        mainMenu.add(helpMenu);
        setJMenuBar(mainMenu);
        /*Toolbar*/
        mainToolbar = new JToolBar();
        JPanel actionsPanel = new JPanel(new GridLayout(Settings.getActionPanelRows(),Settings.getActionPanelColumns()));
        JButton selectButton = new JButton(Settings.getImageIconResource("Cursor_24px.png"));
        selectButton.addActionListener(e -> Action.setSelectedTool(Action.Tool.SELECT));
        JButton deleteButton = new JButton(Settings.getImageIconResource("Delete_24px.png"));
        deleteButton.addActionListener(e -> Action.setSelectedTool(Action.Tool.DELETE));
        JButton lineButton = new JButton(Settings.getImageIconResource("Line_24px.png"));
        lineButton.addActionListener(e -> { if (current != null) { current.deselect(); current = null; } Action.setSelectedTool(Action.Tool.LINE); });
        JButton polylineButton = new JButton(Settings.getImageIconResource("Polyline_24px.png"));
        polylineButton.addActionListener(e -> { if (current != null) { current.deselect(); current = null; } Action.setSelectedTool(Action.Tool.POLYLINE); });
        JButton polygonButton = new JButton(Settings.getImageIconResource("Polygon_24px.png"));
        polygonButton.addActionListener(e -> { if (current != null) { current.deselect(); current = null; } Action.setSelectedTool(Action.Tool.POLYGON); });
        JButton rectButton = new JButton(Settings.getImageIconResource("Rectangle_24px.png"));
        rectButton.addActionListener(e -> { if (current != null) { current.deselect(); current = null; } Action.setSelectedTool(Action.Tool.RECTANGLE); });
        actionsPanel.setMaximumSize(Settings.getActionPanelMaxSize());
        actionsPanel.add(selectButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(lineButton);
        actionsPanel.add(polylineButton);
        actionsPanel.add(polygonButton);
        actionsPanel.add(rectButton);
        mainToolbar.add(actionsPanel);
        mainToolbar.addSeparator();
        JComboBox lineWidthCombo = new JComboBox();
        lineWidthCombo.addItem(Settings.getImageIconResource("Line1.png"));
        lineWidthCombo.addItem(Settings.getImageIconResource("Line2.png"));
        lineWidthCombo.addItem(Settings.getImageIconResource("Line3.png"));
        lineWidthCombo.addItem(Settings.getImageIconResource("Line4.png"));
        lineWidthCombo.addActionListener(e ->
        {
            Action.setSelectedWidth((int) Math.pow(2, lineWidthCombo.getSelectedIndex()));
            if (current!=null && current.isSelected())
            {
                Action.addAction(new StrokeAction(current, current.getStroke(), Action.getSelectedWidth()));
                current.setStroke(Action.getSelectedWidth());
                Paintable.refresh(canvas);
            }
        });
        lineWidthCombo.setMaximumSize(Settings.getLineWidthComboMaxSize());
        mainToolbar.add(lineWidthCombo);
        mainToolbar.addSeparator();
        /*Colors*/
        JPanel colorsPanel = new JPanel(new GridLayout(Settings.getColorsPanelRows(),Settings.getColorsPanelColumns()));
        colorsPanel.setMaximumSize(Settings.getColorsPanelMaxSize());
        for(int i = 0 ; i < Settings.getColorsPanelRows()*Settings.getColorsPanelColumns(); i++)
        {
            JButton colorButton = new JButton();
            colorButton.setBackground(Settings.getColor(i));
            colorButton.addActionListener(e ->
            {
                Action.setSelectedColor(colorButton.getBackground());
                if (current!=null && current.isSelected())
                {
                    Action.addAction(new ColorAction(current, current.getColor(), Action.getSelectedColor()));
                    current.setColor(Action.getSelectedColor());
                    Paintable.refresh(canvas);
                }
            });
            colorsPanel.add(colorButton);
        }
        mainToolbar.add(colorsPanel);
        mainToolbar.addSeparator();
        JPanel undoredoPanel = new JPanel(new GridLayout(Settings.getUndoRedoPanelRows(),Settings.getUndoRedoPanelColumns()));
        undoButton = new JButton(Settings.getImageIconResource("Undo_24px.png"));
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> Action.undoAction());
        undoredoPanel.add(undoButton);
        redoButton = new JButton(Settings.getImageIconResource("Redo_24px.png"));
        redoButton.setEnabled(false);
        redoButton.addActionListener(e -> Action.redoAction());
        undoredoPanel.add(redoButton);
        undoredoPanel.setMaximumSize(Settings.getUndoRedoPanelMaxSize());
        mainToolbar.add(undoredoPanel);
        mainToolbar.setFloatable(false);
        add(mainToolbar, BorderLayout.NORTH);

        /* Canvas */
        canvas = new JPanel() { @Override public void paintComponent(Graphics g) { Paintable.repaint(g); } };
        canvas.setOpaque(true);
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        add(canvas);

        /* Status bar */
        statusPanel = new JPanel(new GridLayout(Settings.getStatusPanelRows(),Settings.getStatusPanelColumns()));
        selectedToolLabel = new JLabel("", JLabel.CENTER);
        selectedColorLabel = new JLabel("", JLabel.CENTER);
        selectedWidthLabel = new JLabel("", JLabel.CENTER);
        specificDimensionLabel = new JLabel("Dimension: ", JLabel.CENTER);
        positionLabel = new JLabel("Mouse position: ", JLabel.CENTER);
        selectedToolLabel.setHorizontalTextPosition(JLabel.LEFT);
        selectedColorLabel.setHorizontalTextPosition(JLabel.LEFT);
        selectedWidthLabel.setHorizontalTextPosition(JLabel.RIGHT);
        specificDimensionLabel.setHorizontalTextPosition(JLabel.RIGHT);
        positionLabel.setHorizontalTextPosition(JLabel.RIGHT);
        selectedWidthLabel.setIcon(Settings.getImageIconResource("LineWidth_24px.png"));
        specificDimensionLabel.setIcon(Settings.getImageIconResource("Dimension_24px.png"));
        positionLabel.setIcon(Settings.getImageIconResource("Position_24px.png"));
        setSelectedToolLabel();
        setSelectedColorLabel();
        setSelectedWidthLabel();
        JPanel temporaryPanel = new JPanel();
        JLabel temporaryIcon = new JLabel("", JLabel.CENTER);
        temporaryIcon.setIcon(Settings.getImageIconResource("Tool_24px.png"));
        temporaryPanel.add(temporaryIcon);
        temporaryPanel.add(selectedToolLabel);
        statusPanel.add(temporaryPanel);
        temporaryPanel = new JPanel();
        temporaryIcon = new JLabel("", JLabel.CENTER);
        temporaryIcon.setIcon(Settings.getImageIconResource("Color_24px.png"));
        temporaryPanel.add(temporaryIcon);
        temporaryPanel.add(selectedColorLabel);
        statusPanel.add(temporaryPanel);
        statusPanel.add(selectedWidthLabel);
        statusPanel.add(specificDimensionLabel);
        statusPanel.add(positionLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    MainWindow()
    {
        initializeComponents();
    }

    public void setSaved(boolean saved)
    {
        this.saved = saved;
        if (saved)
        {
            if (getTitle().endsWith(" *")) setTitle(getTitle().substring(0, getTitle().length() - 2));
        }
        else if (!getTitle().endsWith(" *")) setTitle(getTitle() + " *");
    }
    public boolean isSaved() { return saved; }
    public void setUndoEnabled(boolean enabled)
    {
        undoButton.setEnabled(enabled);
    }
    public void setRedoEnabled(boolean enabled)
    {
        redoButton.setEnabled(enabled);
    }

    public void setSelectedToolLabel()
    {
        if (Action.getSelectedTool() == Action.Tool.SELECT)
        {
            selectedToolLabel.setText("Tool: Select");
            selectedToolLabel.setIcon(Settings.getImageIconResource("Cursor_24px.png"));
        }
        else if (Action.getSelectedTool() == Action.Tool.DELETE)
        {
            selectedToolLabel.setText("Tool: Delete");
            selectedToolLabel.setIcon(Settings.getImageIconResource("Delete_24px.png"));
        }
        else if (Action.getSelectedTool() == Action.Tool.LINE)
        {
            selectedToolLabel.setText("Tool: Line");
            selectedToolLabel.setIcon(Settings.getImageIconResource("Line_24px.png"));
        }
        else if (Action.getSelectedTool() == Action.Tool.POLYLINE)
        {
            selectedToolLabel.setText("Tool: Polyline");
            selectedToolLabel.setIcon(Settings.getImageIconResource("Polyline_24px.png"));
        }
        else if (Action.getSelectedTool() == Action.Tool.POLYGON)
        {
            selectedToolLabel.setText("Tool: Polygon");
            selectedToolLabel.setIcon(Settings.getImageIconResource("Polygon_24px.png"));
        }
        else if (Action.getSelectedTool() == Action.Tool.RECTANGLE)
        {
            selectedToolLabel.setText("Tool: Rectangle");
            selectedToolLabel.setIcon(Settings.getImageIconResource("Rectangle_24px.png"));
        }
    }
    public void setSelectedColorLabel()
    {
        selectedColorLabel.setText("Color: ");
        int width = Settings.getSelectedColorLabelIconDimension();
        int height = Settings.getSelectedColorLabelIconDimension();
        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage image = new BufferedImage(width, height, type);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Action.getSelectedColor());
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        selectedColorLabel.setIcon(new ImageIcon(image));
    }
    public void setSelectedWidthLabel()
    {
        selectedWidthLabel.setText("Line width: " + Action.getSelectedWidth() + "px");
    }
    public void setSpecificLabelText(String text)
    {
        specificDimensionLabel.setText("Dimension: " + text);
    }
    public void setPositionLabelText(String text){
        positionLabel.setText("Mouse position: " + text);
    }

    @Override public void mousePressed(MouseEvent e)
    {
        if (Action.getSelectedTool() == Action.Tool.SELECT) current = Paintable.setSelection(e.getPoint(), e.isControlDown());
        else if (Action.getSelectedTool() == Action.Tool.DELETE) Paintable.deletePaintable(e.getPoint());
        if (((current != null) && (current.isPainting() || Action.getSelectedTool() == Action.Tool.SELECT || Action.getSelectedTool() == Action.Tool.DELETE)) || ((current = Action.createPaintable(canvas)) != null)) current.mousePressed(e);
    }
    @Override public void mouseClicked(MouseEvent e)
    {
        if (current != null) current.mouseClicked(e);
    }
    @Override public void mouseReleased(MouseEvent e)
    {
        if (current != null) current.mouseReleased(e);
    }
    @Override public void mouseEntered(MouseEvent e)
    {
        if (current != null) current.mouseEntered(e);
    }
    @Override public void mouseExited(MouseEvent e)
    {
        setPositionLabelText("");
        if (current != null) current.mouseExited(e);
    }
    @Override public void mouseDragged(MouseEvent e)
    {
        setPositionLabelText(Integer.toString(e.getX()) + ", " + Integer.toString(e.getY())+ "px");
        if (current != null) current.mouseDragged(e);
    }
    @Override public void mouseMoved(MouseEvent e)
    {
        setPositionLabelText(Integer.toString(e.getX()) + ", " + Integer.toString(e.getY())+ "px");
        if (current != null) current.mouseMoved(e);
    }
}
