package simplepaint;

import java.awt.*;

public class StrokeAction extends Action
{
    private Paintable paintable;
    private int oldWidth, newWidth;

    public StrokeAction(Paintable paintable, int oldWidth, int newWidth)
    {
        this.paintable = paintable;
        this.oldWidth = oldWidth;
        this.newWidth = newWidth;
    }

    public Paintable getPaintable()
    {
        return paintable;
    }
    public void setPaintable(Paintable paintable)
    {
        this.paintable = paintable;
    }
    public int getOldWidth()
    {
        return oldWidth;
    }
    public void setOldWidth(int oldWidth)
    {
        this.oldWidth = oldWidth;
    }
    public int getNewWidth()
    {
        return newWidth;
    }
    public void setNewWidth(int newWidth)
    {
        this.newWidth = newWidth;
    }

    @Override public void undo()
    {
        paintable.setStroke(oldWidth);
        Paintable.refresh(paintable.getCanvas());
    }
    @Override public void redo()
    {
        paintable.setStroke(newWidth);
        Paintable.refresh(paintable.getCanvas());
    }
}
