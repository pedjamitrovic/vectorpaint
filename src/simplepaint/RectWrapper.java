package simplepaint;

import java.awt.Point;
import java.awt.Dimension;

public class RectWrapper extends java.awt.Rectangle
{
    private static Point offset = Settings.getRectWrapperOffset();
    private static Dimension extend = Settings.getRectWrapperExtend();

    public RectWrapper() { }
    public RectWrapper(java.awt.Rectangle r)
    {
        super(r);
    }
    public RectWrapper(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }
    public RectWrapper(int width, int height)
    {
        super(width, height);
    }
    public RectWrapper(Point p, Dimension d)
    {
        super(p, d);
    }
    public RectWrapper(Point p)
    {
        super(p);
    }
    public RectWrapper(Dimension d)
    {
        super(d);
    }

    public Point getWrappedLocation()
    {
        Point loc = super.getLocation();
        return new Point(loc.x + offset.x, loc.y + offset.y);
    }
    public Dimension getWrappedSize()
    {
        Dimension size = super.getSize();
        return new Dimension(size.width + extend.width, size.height + extend.width);
    }
    public boolean containsWrapped(Point p)
    {
        return (new java.awt.Rectangle(getWrappedLocation(), getWrappedSize())).contains(p);
    }
}
