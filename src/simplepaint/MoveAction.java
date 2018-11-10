package simplepaint;

import com.sun.javafx.geom.Vec2d;

public class MoveAction extends Action
{
    private Paintable paintable;
    private Vec2d offset;

    public MoveAction(Paintable paintable, Vec2d offset)
    {
        this.paintable = paintable;
        this.offset = offset;
    }

    public Paintable getPaintable()
    {
        return paintable;
    }
    public void setPaintable(Paintable paintable)
    {
        this.paintable = paintable;
    }
    public Vec2d getOffset()
    {
        return offset;
    }
    public void setOffset(Vec2d offset)
    {
        this.offset = offset;
    }

    @Override public void undo()
    {
        paintable.move(new Vec2d(-offset.x, -offset.y));
        Paintable.refresh(paintable.getCanvas());
    }
    @Override public void redo()
    {
        paintable.move(offset);
        Paintable.refresh(paintable.getCanvas());
    }
}
