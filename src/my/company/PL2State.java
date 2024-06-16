/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company;

import com.ttsnetwork.modules.standard.BoxUtils;
import com.ttsnetwork.modules.standard.IConveyorCommands;
import com.ttsnetwork.modules.standard.IGripper;
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

    double VROB = 6000; //robot velocity 
    double VROB_attach = 3000; //attaching
    //timing
    long tempo = 0;
    long tempof = 0;
    public PLGlobal cont;//counter for path
    //skill variables
    IRobotCommands rob;
    IGripper gripper;
    IConveyorCommands Cc;
    IConveyorCommands Cd;
    IConveyorCommands Ce;
    ISensorProvider CcSen1;
    ISensorProvider CcSen2; 
    ISensorProvider CcSen3; 
    ISensorProvider CdSen1;
    ISensorProvider CeSen1;
    
    ConveyorBox workingC;
    ConveyorBox workingD;
    ConveyorBox workingE;
     //state variables for FSM
    SimpleStateVar partC = new SimpleStateVar();
    SimpleStateVar partD = new SimpleStateVar();
    SimpleStateVar partE = new SimpleStateVar();
    SimpleStateVar finishedAD = new SimpleStateVar();
    SimpleStateVar finishedBDE = new SimpleStateVar();



    @Override
    public void onInit() {
        //Skills declaration 
        rob = useSkill(IRobotCommands.class, "R2");
        gripper = useSkill(IGripper.class, "RobotGripper1");
        Cc = useSkill(IConveyorCommands.class, "CC");
        Cd = useSkill(IConveyorCommands.class, "CD1");
        Ce = useSkill(IConveyorCommands.class, "CE");
        CcSen1 = useSkill(ISensorProvider.class, "CC");
        CcSen2 = useSkill(ISensorProvider.class, "CC");
        CcSen3 = useSkill(ISensorProvider.class, "CC");
        CdSen1 = useSkill(ISensorProvider.class, "CD1");
        CeSen1 = useSkill(ISensorProvider.class, "CE");
        //registering listening functions
        CcSen1.registerOnSensors(this::c_sensor1, "C_sen1");
        CcSen2.registerOnSensors(this::c_sensorInit, "C_senInit");
        CcSen3.registerOnSensors(this::c_sensorFinish, "C_senFinish");
        CdSen1.registerOnSensors(this::d_sensor1, "D1_sen1");
        CeSen1.registerOnSensors(this::e_sensor1, "E_sen1");

    }

    @Override
    public void onStart() {
        switchState(100); //initial state
    }
    public void state_100() {
        if (partC.read() != null) {
            workingC = partC.readAndForget(); //taking care of C component - from main line
            switchState(200);
        }
    }

    public void state_200() {
        String type = workingC.entity.getProperty("rfid");
        if (type.equals("PA")) { //due the fact C could be PA or PB, we should choose 2 different path
            switchState(2000); // A-D path
        } else {
            switchState(3000); // BDE path
        }

    }

    public void state_2000() {
        if (partD.read() != null) {
            workingD = partD.readAndForget(); //taking care of D component
            switchState(2010); 
        }
    }

    public void state_2010() {
        AssmAD();  //assembling
        switchState(2020);
    }
 public void state_2020() {
        if (finishedAD.readBoolean()) {
            finishedAD.write(false); //we set that if we already finish, we are ready for the next one
            switchState(2030);
        }
    }

    public void state_2030() {
        releasebox();
        switchState(100); //backhome
    }

    public void state_3000() {
        if (partD.read() != null) {
            workingD = partD.readAndForget(); 
            switchState(3005);
        }
    }
        public void state_3005() {
        if (partE.read() != null) {
            workingE = partE.readAndForget();
            switchState(3010);
        }
    }

    public void state_3010() {
        AssmBDE();
        switchState(3020);
    }

    public void state_3020() {
        if (finishedBDE.readBoolean()) {
            finishedBDE.write(false);
            switchState(2030);
        }
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

    public void c_sensorInit(SensorCatch sc) {
          
        schedule.startSerial();
        //incrementing N2 
        tempo = model.getClock();
         sc.box.entity.setProperty("tempo", tempo);
        cont.N2++;
        schedule.end();
    }

    public void c_sensorFinish(SensorCatch sc) {
        schedule.startSerial();
        //decrementing N2   
        tempof = model.getClock();
        tempo = sc.box.entity.getProperty("tempo");
        sc.box.entity.setProperty("tempo", String.valueOf(tempof-tempo));
        cont.N2--;
        schedule.end();
    }


    public void AssmAD() {

        schedule.startSerial();

        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300 + BoxUtils.zSize(workingD), 0, 0, 90), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0,   BoxUtils.zSize(workingD), 0, 0, 90), VROB_attach);
        gripper.moveGripTo(BoxUtils.ySize(workingD), 500);
        rob.pick(workingD.entity);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300 + BoxUtils.zSize(workingD), 0, 0, 90), VROB);
        Cd.remove(workingD);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300 + BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, BoxUtils.zSize(workingD)+10, 0, 0, 0), VROB_attach);
        rob.release();
        gripper.moveGripTo(551, 500);
        rob.move(driver.getFrameTransform("root.homeLine2"), VROB);
        schedule.attach(workingD.entity, workingC.entity);
        setVar(finishedAD, true);
        schedule.end();

    }
    
    public void AssmBDE() {

        schedule.startSerial();
        /*
        BD - first assembling part
        */
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300 + BoxUtils.zSize(workingD), 0, 0, 90), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0,  BoxUtils.zSize(workingD), 0, 0, 90), VROB_attach);
        gripper.moveGripTo(BoxUtils.ySize(workingD), 500);
        rob.pick(workingD.entity);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300 + BoxUtils.zSize(workingD), 0, 0, 90), VROB);
        Cd.remove(workingD);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 300 + BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0,  BoxUtils.zSize(workingD)+10, 0, 0, 0), VROB_attach);
        rob.release();
        gripper.moveGripTo(551, 500);
        schedule.attach(workingD.entity, workingC.entity);
        /*
        DE - last assembling part
        */
        rob.moveLinear(BoxUtils.targetOffset(workingE, 0, 0, 300 + BoxUtils.zSize(workingE), 0, 0, 90), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingE, 0, 0,  BoxUtils.zSize(workingE), 0, 0, 90), VROB_attach);
        gripper.moveGripTo(BoxUtils.ySize(workingE), 500);
        rob.pick(workingE.entity);
        rob.moveLinear(BoxUtils.targetOffset(workingE, 0, 0, 300 + BoxUtils.zSize(workingE), 0, 0, 90), VROB);
        Ce.remove(workingE);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, 200 + BoxUtils.zSize(workingD) + BoxUtils.zSize(workingC), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingC, 0, 0, BoxUtils.zSize(workingD) + BoxUtils.zSize(workingC)-70, 0, 0, -90), workingE.cF, VROB_attach); // variante del movimento del concetto di external TCP
        rob.release();
        gripper.moveGripTo(551, 500);
        schedule.attach(workingE.entity, workingD.entity);
        rob.move(driver.getFrameTransform("root.homeLine2"), VROB);
        setVar(finishedBDE, true);
        schedule.end();

    }

    public void releasebox() {

        schedule.startSerial();
        Cc.release(workingC);
        schedule.end();

    }

}
