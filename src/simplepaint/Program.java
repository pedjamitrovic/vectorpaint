package simplepaint;

public final class Program
{
    private static MainWindow mWindow = new MainWindow();

    public static MainWindow getMainWindow() { return mWindow; }

    private Program() { }

    public static void main(String[] args)
    {
        mWindow.setVisible(true);
    }
}