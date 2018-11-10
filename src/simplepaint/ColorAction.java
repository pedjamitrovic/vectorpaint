package simplepaint;

import java.awt.*;

public class ColorAction extends Action
{
    private Paintable paintable;
    private Color oldColor, newColor;

    public ColorAction(Paintable paintable, Color oldColor, Color newColor)
    {
        this.paintable = paintable;
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    public Paintable getPaintable()
    {
        return paintable;
    }
    public void setPaintable(Paintable paintable)
    {
        this.paintable = paintable;
    }
    public Color getOldColor()
    {
        return oldColor;
    }
    public void setOldColor(Color oldColor)
    {
        this.oldColor = oldColor;
    }
    public Color getNewColor()
    {
        return newColor;
    }
    public void setNewColor(Color newColor)
    {
        this.newColor = newColor;
    }

    @Override public void undo()
    {
        paintable.setColor(oldColor);
        Paintable.refresh(paintable.getCanvas());
    }
    @Override public void redo()
    {
        paintable.setColor(newColor);
        Paintable.refresh(paintable.getCanvas());
    }
}
