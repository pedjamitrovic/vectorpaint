package simplepaint;

public class DrawAction extends Action
{
    private Paintable paintable;

    public DrawAction(Paintable paintable)
    {
        this.paintable = paintable;
    }

    public Paintable getPaintable()
    {
        return paintable;
    }
    public void setPaintable(Paintable paintable)
    {
        this.paintable = paintable;
    }

    @Override public void undo()
    {
        if (paintable.isSelected()) paintable.deselect();
        paintable.remove();
    }
    @Override public void redo()
    {
        if (paintable.isSelected()) paintable.deselect();
        paintable.add();
    }
}
