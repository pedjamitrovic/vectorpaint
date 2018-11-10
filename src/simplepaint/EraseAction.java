package simplepaint;

public class EraseAction extends Action
{
    private Paintable paintable;

    public EraseAction(Paintable paintable)
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
        paintable.add();
    }
    @Override public void redo()
    {
        if (paintable.isSelected()) paintable.deselect();
        paintable.remove();
    }
}
