import control.EstimatorInterface;
import control.LocalizationDriver;
import model.DummyLocalizer;
import view.RobotLocalizationViewer;

/**
 * Created by Erik on 2017-02-22.
 */
public class main {

    public static void main(String args[]){
        EstimatorInterface l = new DummyLocalizer( 4, 4, 4);
        RobotLocalizationViewer viewer = new RobotLocalizationViewer( l);
        new LocalizationDriver( 500, viewer).start();
    }
}
