/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company;

import com.ttsnetwork.modules.standard.BoxUtils;
import com.ttsnetwork.modules.standard.IConveyorCommands;
import com.ttsnetwork.modules.standard.IRobotCommands;
import com.ttsnetwork.modules.standard.ISensorProvider;
import com.ttsnetwork.modules.standard.SimpleStateVar;
import com.ttsnetwork.modules.standard.StateMachine;
import com.ttsnetwork.modulespack.conveyors.ConveyorBox;
import com.ttsnetwork.modulespack.conveyors.SensorCatch;

/**
 *
 * @author Simone
 */
public class PL2State extends StateMachine {

    double VROB = 3000;
    double VROB_attach = 500;
    
    IRobotCommands rob;

    IConveyorCommands Cc;
    IConveyorCommands Cd;
    IConveyorCommands Ce;

    ISensorProvider CcSen1;
    ISensorProvider CdSen1;
    ISensorProvider CeSen1;
    
    SimpleStateVar partC = new SimpleStateVar();
    SimpleStateVar partD = new SimpleStateVar();
    SimpleStateVar partE = new SimpleStateVar();
    
    SimpleStateVar finishedAD = new SimpleStateVar();
    SimpleStateVar finishedBDE = new SimpleStateVar();
    

    
    ConveyorBox workingC;
    ConveyorBox workingD;
    ConveyorBox workingE;
   
    
    @Override
    public void onInit() {
        
        rob = useSkill(IRobotCommands.class, "R2");

        Cc = useSkill(IConveyorCommands.class, "CC");
        Cd = useSkill(IConveyorCommands.class, "CD1");
        Ce = useSkill(IConveyorCommands.class, "CE");
        
        CcSen1 = useSkill(ISensorProvider.class, "CC");
        CdSen1 = useSkill(ISensorProvider.class, "CD1");
        CeSen1 = useSkill(ISensorProvider.class, "CE");
        
        CcSen1.registerOnSensors(this::c_sensor1, "C_sen1");
        CdSen1.registerOnSensors(this::d_sensor1, "D1_sen1");
        CeSen1.registerOnSensors(this::e_sensor1, "E_sen1");

    }

    @Override
    public void onStart() {
        switchState(100);
    }

    public void c_sensor1(SensorCatch sc) {
        schedule.startSerial();
        Cc.lock(sc.box);
        setVar(partC, sc.box);
        schedule.end();
    }
    public void d_sensor1(SensorCatch sc) {
        schedule.startSerial();
        Cd.lock(sc.box);
        setVar(partD, sc.box);
        schedule.end();
    }
    public void e_sensor1(SensorCatch sc) {
        schedule.startSerial();
        Ce.lock(sc.box);
        setVar(partE, sc.box);
        schedule.end();
    }

    public void state_100() {
        if (partC.read() != null) {
            workingC = partC.readAndForget();
            switchState(200);
        }
    }

    public void state_200() {
            String type = workingC.entity.getProperty("rfid");
            if(type.equals("PA")){
                switchState(2000);
            }else{
                switchState(3000);
            }

    }
    public void state_2000(){
        if(partD.read() != null){
            workingD = partD.readAndForget();
            switchState(2010);
        }
    }
    public void state_2010(){
        AssmAD();
        switchState(2020);
    }

    public void AssmAD() {
        
        schedule.startSerial();
     
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetTop(workingD), VROB);
        rob.pick(workingD.entity);
        Cd.remove(workingD);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300+BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        rob.move(BoxUtils.targetTop(workingC), workingD.cF, 2300.0);
        rob.release();
        rob.home();
        schedule.attach(workingD.entity, workingC.entity);
        setVar(finishedAD, true);
        schedule.end();

      
    }
    public void state_2020(){
        if(finishedAD.readBoolean()){
            finishedAD.write(false);
            switchState(2030);
        }
    }
    public void state_2030(){
        releasebox();
        switchState(100);
    }

    public void state_3000() {
        if(partD.read() != null){
            workingD = partD.readAndForget();
            switchState(3005);
        }
    }
    public void state_3005() {
        if(partE.read() != null){
            workingE = partE.readAndForget();
            switchState(3010);
        }
    }
    public void state_3010() {
        AssmBDE();
        switchState(3020);
    }
    public void state_3020() {
        if(finishedBDE.readBoolean()){
            finishedBDE.write(false);
            switchState(2030);
        }
    }
    public void AssmBDE() {
        
        schedule.startSerial();
     
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetTop(workingD), VROB);
        rob.pick(workingD.entity);
        Cd.remove(workingD);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300+BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetTop(workingC), workingD.cF, VROB_attach);
        rob.release();
        //rob.home();
        schedule.attach(workingD.entity, workingC.entity);
        rob.moveLinear(BoxUtils.targetOffset(workingE, 0, 0, 300+BoxUtils.zSize(workingE), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetTop(workingE), VROB);
        rob.pick(workingE.entity);
        Ce.remove(workingE);
        
        rob.moveLinear(BoxUtils.targetOffset(workingE, 0, 0, 300+BoxUtils.zSize(workingE), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 200+BoxUtils.zSize(workingD)+BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, BoxUtils.zSize(workingD)+BoxUtils.zSize(workingC), 0, 0, 0),workingE.cF, VROB_attach); // variante del movimento del concetto di external TCP
        rob.release();
        schedule.attach(workingE.entity, workingD.entity);
        rob.home();
        
        setVar(finishedBDE, true);
        schedule.end();

      
    }



    public void releasebox() {

        schedule.startSerial();
        Cc.release(workingC);
        schedule.end();

    }

}
