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

    double VROB = 6000;

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
    

    ConveyorBox workingB;
    ConveyorBox workingC;
    ConveyorBox workingD;

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
        try{
            String type = workingC.entity.getProperty("rfid");
            if(type.equals("PA")){
                switchState(2000);
            }else{
                switchState(3000);
            }
        }catch(Exception e){
            System.out.println("error state_200()");
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
     
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetTop(workingD), VROB);
        rob.pick(workingD.entity);
        Cd.remove(workingD);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300+BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        rob.move(BoxUtils.targetTop(workingC), workingC.cF, 2300.0);
        rob.release();
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300+BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        
        rob.home();
        schedule.attach(workingD.entity, workingB.entity);
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
        AssmBDE();
        switchState(3010);
    }
    public void AssmBDE() {
        
        schedule.startSerial();
     
       /* rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetTop(workingD), VROB);
        rob.pick(workingD.entity);
        Cd.remove(workingD);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300+BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        rob.move(BoxUtils.targetTop(workingC), workingC.cF, 2300.0);
        rob.release();
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300+BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        
        rob.home();
        schedule.attach(workingD.entity, workingB.entity);
        setVar(finishedAD, true);*/
        schedule.end();

      
    }
    public void state_3010() {
        if (finishedBDE.readBoolean()) {
            finishedBDE.write(false);
            switchState(2030);
        }
    }


    public void releasebox() {

        schedule.startSerial();
        Cc.release(workingC);
        schedule.end();

    }

}
