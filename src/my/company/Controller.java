package my.company;

import com.ttsnetwork.heron.v5.api.SimulationController;
import com.ttsnetwork.heron.v5.api.SimulationEngine;
import com.ttsnetwork.heron.v5.api.SimulationExperiment;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TTS Technology Transfer System S.r.l.
 */
public class Controller implements SimulationController {
    
    public final static String PROJECT_ROOT_KEY = "project.root";

    @Override
    public int execute(SimulationEngine engine) {
        Map<String, String> data = new HashMap<>();
        SimulationExperiment exp = engine.create(data);
        exp.getModel().setProperty(PROJECT_ROOT_KEY, engine.getProjectConfig().getFile().getParent());
        if (!exp.init(false)) {
            return -1;
        }

        Future<Integer> task = exp.execute();
        int exitCode = -1;
        try {
            exitCode = task.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        exp.destroy();
        return exitCode;
    }

}
