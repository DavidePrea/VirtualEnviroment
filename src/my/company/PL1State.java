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
public class PL1State extends StateMachine {

    double VROB = 6000;
    public PLGlobal cont;
    IRobotCommands rob;

    IConveyorCommands Cb;
    IConveyorCommands Cd;

    ISensorProvider CbSen1;
    ISensorProvider CbSen2; //init for B
    ISensorProvider CbSen3; //finish for B
    ISensorProvider CdSen1;

    SimpleStateVar partB = new SimpleStateVar();
    SimpleStateVar partD = new SimpleStateVar();
    SimpleStateVar finished = new SimpleStateVar();

    ConveyorBox workingB;
    ConveyorBox workingD;

    @Override
    public void onInit() {
        rob = useSkill(IRobotCommands.class, "R1");

        Cb = useSkill(IConveyorCommands.class, "CB");
        Cd = useSkill(IConveyorCommands.class, "CD2");

        CbSen1 = useSkill(ISensorProvider.class, "CB");
        CbSen2 = useSkill(ISensorProvider.class, "CB");
        CbSen3 = useSkill(ISensorProvider.class, "CB");
        CdSen1 = useSkill(ISensorProvider.class, "CD2");

        CbSen1.registerOnSensors(this::b_sensor1, "B_sen1");
        CbSen2.registerOnSensors(this::b_sensorInit, "B_senInit");
        CbSen3.registerOnSensors(this::b_sensorFinish, "B_senFinish");
        
        CdSen1.registerOnSensors(this::d_sensor1, "D2_sen1");

    }

    @Override
    public void onStart() {
        switchState(100);
    }

    public void b_sensor1(SensorCatch sc) {
        schedule.startSerial();
        Cb.lock(sc.box);
        setVar(partB, sc.box);
        schedule.end();
    }

    public void d_sensor1(SensorCatch sc) {
        schedule.startSerial();
        Cd.lock(sc.box);
        setVar(partD, sc.box);
        schedule.end();
    }
    public void b_sensorInit(SensorCatch sc) {
        schedule.startSerial();
             cont.N1++;
        schedule.end();
    }
    public void b_sensorFinish(SensorCatch sc) {
        schedule.startSerial();
             cont.N1--;
        schedule.end();
    }
    public void state_100() {
        if (partB.read() != null) {
            workingB = partB.readAndForget();
            switchState(200);
        }
    }

    public void state_200() {
        if (partD.read() != null) {
            workingD = partD.readAndForget();
            switchState(300);
        }

    }

    public void AssmBD() {
        schedule.startSerial();
     
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
        rob.moveLinear(BoxUtils.targetTop(workingD), VROB);
        rob.pick(workingD.entity);
        Cd.remove(workingD);
        rob.moveLinear(BoxUtils.targetOffset(workingD, 0, 0, 300+BoxUtils.zSize(workingD), 0, 0, 0), VROB);
      //  rob.moveLinear(driver.getFrameTransform("CB.midPos"), VROB);
        rob.moveLinear(BoxUtils.targetOffset(workingB, 0, 0, 300+BoxUtils.zSize(workingB), 0, 0, 0), VROB);
        rob.move(BoxUtils.targetTop(workingB), workingD.cF, 2300.0);
        rob.release();
        rob.moveLinear(BoxUtils.targetOffset(workingB, 0, 0, 300+BoxUtils.zSize(workingB), 0, 0, 0), VROB);
        
        rob.home();
        schedule.attach(workingD.entity, workingB.entity);
        setVar(finished, true);
        schedule.end();
    }

    public void state_300() {
        AssmBD();
        switchState(400);
    }

    public void state_400() {
        if (finished.readBoolean()) {
            finished.write(false);
            switchState(500);
        }
    }

    public void state_500() {
        releasebox();
        switchState(100);
    }

    public void releasebox() {

        schedule.startSerial();
        Cb.release(workingB);
        schedule.end();

    }

}
