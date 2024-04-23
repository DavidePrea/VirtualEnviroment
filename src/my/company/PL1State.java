/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company;

import com.ttsnetwork.modules.standard.IConveyorCommands;
import com.ttsnetwork.modules.standard.IRobotCommands;
import com.ttsnetwork.modules.standard.ISensorProvider;
import com.ttsnetwork.modules.standard.SimpleStateVar;
import com.ttsnetwork.modules.standard.StateMachine;
import com.ttsnetwork.modulespack.conveyors.ConveyorBox;

/**
 *
 * @author Simone
 */
public class PL1State extends StateMachine {
    
  double Vrob = 3000;
    IRobotCommands rob1;
  
    IConveyorCommands Cb;
    IConveyorCommands Cc;
    IConveyorCommands Cd;
    
    ISensorProvider CbSen1;
    ISensorProvider CdSen1;
    
    SimpleStateVar partB;
    
    ConveyorBox workingB;
    
    
    @Override
    public void onInit() {
       rob1 = useSkill(IRobotCommands.class, "R1");
     //  rob2 = useSkill(IRobotCommands.class, "R2");
       Cb = useSkill(IConveyorCommands.class, "CB");
       Cd = useSkill(IConveyorCommands.class, "CD2");
       
       CbSen1 = useSkill(ISensorProvider.class, "CB");
       CdSen1 = useSkill(ISensorProvider.class, "CD2");
    }
    @Override
    public void onStart() {
        switchState(100);
    }
    
    public void state_100(){
        if(partB != null){
            workingB = partB.readAndForget();
            switchState(200);
        }
    }
    public void state_200(){
    
    }
    public void state_300(){
    
    }
    public void state_400(){
    
    }
    public void state_500(){
    
    }
    public void state_600(){
    
    }
    
}
