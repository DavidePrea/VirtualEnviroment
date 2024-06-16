/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company;

import com.ttsnetwork.modules.standard.IConveyorCommands;
import com.ttsnetwork.modules.standard.ISensorProvider;
import com.ttsnetwork.modules.standard.ProgrammableLogics;
import com.ttsnetwork.modulespack.conveyors.SensorCatch;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 *
 * @author Simone
 */
public class Control extends ProgrammableLogics{
    
    ISensorProvider ChSen;
    IConveyorCommands Ch;
    RealDistribution randomDist;
    
     @Override
    public void onInit() {
        Ch = useSkill(IConveyorCommands.class, "CH");
        ChSen = useSkill(ISensorProvider.class, "CH");
        ChSen.registerOnSensors(this::control, "CH_sensor");
        //triangle distribution for ispection time
        randomDist = model.getRandomGenerator().getTriangularDistribution(1000,1500,2000);
    }
    public void control(SensorCatch sc){
        double time = randomDist.sample(); //get time for wait
        schedule.startSerial();
        Ch.lock(sc.box);
        schedule.waitTime((long) time); //we wait for the sample taken
        Ch.release(sc.box);
        schedule.end();

        
    }
    
    
    
}
