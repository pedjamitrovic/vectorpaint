package simplepaint;

import com.sun.javafx.geom.Vec2d;
import java.awt.*;

public interface PaintableAction
{
    void add();
    void remove();
    Color getColor();
    void setColor(Color color);
    int getStroke();
    void setStroke(int stroke);
    void move(Vec2d offset);
}
