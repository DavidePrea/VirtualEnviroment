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
public class PL1State extends StateMachine {

    double VROB = 6000; // robot velocity 
    double VROB_attach = 3000; //attaching
    //timing
    long tempo = 0;
    long tempof = 0;

    public PLGlobal cont; //counter for path
    //skill variables
    IRobotCommands rob;
    IGripper gripper;
    IConveyorCommands Cb;
    IConveyorCommands Cd;
    ISensorProvider CbSen1;
    ISensorProvider CbSen2;
    ISensorProvider CbSen3;
    ISensorProvider CdSen1;

    ConveyorBox workingB;
    ConveyorBox workingD;

    //state variables for FSM
    SimpleStateVar partB = new SimpleStateVar();
    SimpleStateVar partD = new SimpleStateVar();
    SimpleStateVar finished = new SimpleStateVar();

    @Override
    public void onInit() {
        //Skills declaration 
        rob = useSkill(IRobotCommands.class, "R1");
        gripper = useSkill(IGripper.class, "RobotGripper2");
        Cb = useSkill(IConveyorCommands.class, "CB");
        Cd = useSkill(IConveyorCommands.class, "CD2");
        CbSen1 = useSkill(ISensorProvider.class, "CB");
        CbSen2 = useSkill(ISensorProvider.class, "CB");
        CbSen3 = useSkill(ISensorProvider.class, "CB");
        CdSen1 = useSkill(ISensorProvider.class, "CD2");
        //registering listening functions
        CbSen1.registerOnSensors(this::b_sensor1, "B_sen1");
        CbSen2.registerOnSensors(this::b_sensorInit, "B_senInit");
        CbSen3.registerOnSensors(this::b_sensorFinish, "B_senFinish");
        CdSen1.registerOnSensors(this::d_sensor1, "D2_sen1");

    }

    @Override
    public void onStart() {
        switchState(100); //initial state
    }

    public void state_100() {
        if (partB.read() != null) {
            workingB = partB.readAndForget(); //taking care of B component - from main line
            switchState(200);
        }
    }

    public void state_200() {
        if (partD.read() != null) {
            workingD = partD.readAndForget(); //taking care of D component
            switchState(300);
        }

    }

    public void state_300() {
        AssmBD(); //assembling function
        switchState(400);
    }

    public void state_400() {
        if (finished.readBoolean()) {
            finished.write(false); //if we finished, we set the SV to be ready for the next one
            switchState(500);
        }
    }

    public void state_500() {
        releasebox(); //releasing
        switchState(100); //back home
    }

    public void b_sensor1(SensorCatch sc) {
        schedule.startSerial();
        Cb.lock(sc.box); //lock at half
        setVar(partB, sc.box); //partB settled to sc.box
        schedule.end();
    }

    public void d_sensor1(SensorCatch sc) {
        schedule.startSerial();
        Cd.lock(sc.box); //locked, waiting to be picked from robot
        setVar(partD, sc.box);
        schedule.end();
    }

    public void b_sensorInit(SensorCatch sc) {
        schedule.startSerial();
        tempo = model.getClock();//get time of arrives
        sc.box.entity.setProperty("tempo", tempo);//we set the property time to looking up at the end
        cont.N1++; //increased items counter
        schedule.end();
    }

    public void b_sensorFinish(SensorCatch sc) {
        schedule.startSerial();
        tempof = model.getClock();//get time of finish
        tempo = sc.box.entity.getProperty("tempo");
        sc.box.entity.setProperty("tempo", String.valueOf(tempof - tempo));
        cont.N1--;
        schedule.end();
    }

    //assembling 
    public void AssmBD() {

        schedule.startSerial();

        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300 + BoxUtils.zSize(workingD), 0, 0, 90), VROB); //move to middle point
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, BoxUtils.zSize(workingD), 0, 0, 90), VROB_attach); //move to the top
        gripper.moveGripTo(BoxUtils.ySize(workingD), 500); //closing gripper
        rob.pick(workingD.entity); //picking 
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300 + BoxUtils.zSize(workingD), 0, 0, 90), VROB); //move to middle point
        Cd.remove(workingD); //remove box from converyor (after thus the queue wait to move forward)
        rob.moveLinear(BoxUtils.targetOffset(workingB, 0, 0, 300 + BoxUtils.zSize(workingB), 0, 0, 0), VROB); //move to middle point of conveyor (upper B)
        rob.moveLinear(BoxUtils.targetOffset(workingB, 0, 0, BoxUtils.zSize(workingD) + 10, 0, 0, 0), VROB_attach); //assemgling
        rob.release(); //release the item
        gripper.moveGripTo(551, 500); //open the gripper
        rob.move(driver.getFrameTransform("root.homeLine1"), VROB); //move to the "fake home"
        schedule.attach(workingD.entity, workingB.entity); //attaching the B,D components
        setVar(finished, true); // settling the finish variable
        schedule.end();
    }

    public void releasebox() {
        schedule.startSerial(); //allow the converyor to goes on
        Cb.release(workingB);
        schedule.end();

    }

}
