/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company;

import com.ttsnetwork.modules.standard.SplitRule;
import com.ttsnetwork.modulespack.conveyors.ConveyorBox;
import java.text.RuleBasedCollator;

/**
 *
 * @author preatons
 */
public class RuleClass implements SplitRule{
    
    int cont = 0; //cont boxes
    
    @Override
    public int select(ConveyorBox cb) {
        
        String type = cb.entity.getProperty("rfid");
        
        if(type.equals("PB")){
            return 1;
        }
        else{
            cont++;
            if(cont <= 3){
                return 0;
            }
            else{
                cont = 0;
                return 1;
            }
        }
    }
    
}
