/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company;

import com.ttsnetwork.modules.standard.IConveyorCommands;
import com.ttsnetwork.modules.standard.ISensorProvider;
import com.ttsnetwork.modules.standard.ProgrammableLogics;
import com.ttsnetwork.modulespack.conveyors.SensorCatch;

/**
 *
 * @author Simone
 */
public class PLFLow extends ProgrammableLogics{
    
    ISensorProvider CaSen;
    IConveyorCommands Ca;
    public PLGlobal cont;
      
    @Override
    public void onInit() {
  
     Ca = useSkill(IConveyorCommands.class, "CA");
     CaSen = useSkill(ISensorProvider.class, "CA");
     CaSen.registerOnSensors(this::sensor, "SA");
    }
     @Override
    public void onStart() {
    
    }
    
    public void sensor(SensorCatch sc){
        schedule.startSerial();
        String type = sc.box.entity.getProperty("rfid");
        
        if(type.equals("PA")){
            if(cont.N1<cont.N2){
               //Set Property 
               sc.box.entity.setProperty("SU", 0);
            }else{
                sc.box.entity.setProperty("SU", 1);
            }
        }
        else{
             sc.box.entity.setProperty("SU", 1);
        }
        schedule.end();
    }
}
