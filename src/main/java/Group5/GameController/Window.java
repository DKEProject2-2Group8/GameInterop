package Group5.GameController;

import Interop.Percept.Vision.ObjectPerceptType;

public class Window extends Area {

    private boolean closed;

    private static double slowDownModifier;

    public Window(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
        super(x1, y1, x2, y2,x3, y3, x4, y4, ObjectPerceptType.Window);
        closed = true;
    }

    public boolean windowClosed(){
        return  closed;
    }

    public void openWindow(Hearing hearing){
        closed = false;
        hearing.windowSound(this);
    }

    public void closeWindow(Hearing hearing){
        closed = true;
        hearing.windowSound(this);
    }


    public static double getSlowDownModifier() {
        return slowDownModifier;
    }

    protected static void setSlowDownModifier(double slowDownModifier) {
        Window.slowDownModifier = slowDownModifier;
    }
}
