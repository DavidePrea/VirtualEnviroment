// PARAMETERS
var length;
var width;
var height;
var BASELINE_HEIGHT = 958;

//--- profile params ---
var prfD = 14; // profile depth
var prfTh = 4.5; // profile thickness
var prfHg = 50; // profile height
var openHg = 14; // opening of the profile
var internalH = 23.4; // altezza interna profilo

var tappTh = 1.5; // spessore tappeto
var deltaTapp = 12; // differenza tappeto - width
var tappDst = 1.5; // distanza tappeto - base

var cylRadius = 50 / 2.0;

var legWidth = 25;
var legConeHgt = 30;
var legConeRadius = 15;
var legInternalDisp = 150;


var app = parametric.appearance()
        .diffuseColor(0xE6E6E6)
        .emissiveColor(0x000000)
        .specularColor(0x1A1A1A)
        .shininess(0.504);

var appTapp = parametric.appearance()
        .diffuseColor(0xFFFFFF)
        .emissiveColor(0x000000)
        .specularColor(0x7F7F7F)
        .shininess(0.409)
        .ambientColor(0x7F7F7F);

var dirName = 'generated/' + instance.uuid + '/';




function configure3D() {

    if (!lwGeom) {
        var bordersFile = localFile('conveyor/model.xml');
        var borders = builder.createChildModel(instance.name, bordersFile);
        var ba = borders.getObject(instance.name + '.relocatorA');
        var bb = borders.getObject(instance.name + '.relocatorB');

        ba.translate(+length / 2.0 - 300, 0, 0);
        bb.translate(-length / 2.0 + 300, 0, 0);

        var ca1 = borders.getObject(instance.name + '.carterA1');
        var ca2 = borders.getObject(instance.name + '.carterA2');
        var cb1 = borders.getObject(instance.name + '.carterB1');
        var cb2 = borders.getObject(instance.name + '.carterB2');

        var carterDisp = (width - 500) / 2.0;
        ca1.translate(-carterDisp, 0, height - BASELINE_HEIGHT);
        ca2.translate(+carterDisp, 0, height - BASELINE_HEIGHT);
        cb1.translate(-carterDisp, 0, height - BASELINE_HEIGHT);
        cb2.translate(+carterDisp, 0, height - BASELINE_HEIGHT);
    }



    if (lwGeom) {
        lwMidStructure(0);
        lwLeg(1, length / 2.0 - legInternalDisp, width / 2.0 - legWidth / 2.0);
        lwLeg(2, length / 2.0 - legInternalDisp, -width / 2.0 + legWidth / 2.0);
        lwLeg(3, -length / 2.0 + legInternalDisp, -width / 2.0 + legWidth / 2.0);
        lwLeg(4, -length / 2.0 + legInternalDisp, width / 2.0 - legWidth / 2.0);
    } else {
        midStructure(0);
        leg(1, length / 2.0 - legInternalDisp, width / 2.0 - legWidth / 2.0);
        leg(2, length / 2.0 - legInternalDisp, -width / 2.0 + legWidth / 2.0);
        leg(3, -length / 2.0 + legInternalDisp, -width / 2.0 + legWidth / 2.0);
        leg(4, -length / 2.0 + legInternalDisp, width / 2.0 - legWidth / 2.0);
    }
    crossbar(1, -length / 2.0 + legInternalDisp);
    crossbar(2, +length / 2.0 - legInternalDisp);


    builder
            .frame('startFrame', -length / 2.0, 0, height, 0, 0, 0)
            .frame('endFrame', +length / 2.0, 0, height, 0, 0, 0)
            .frame('commandsInFrame', +length / 2.0, width / 2.0, height, 0, 0, 0)
            .frame('sensorsOutFrame', 0, 0, height, 0, 0, 0);

    if (sensorPositions !== null) {
        var color = 0xFF0000;
        var appSensor = parametric.appearance().diffuseColor(color);
        var sensGeom = box(2, width, 5).appearance(appSensor);
        for (var sk = 0; sk < sensorPositions.length; sk++) {
            var posSen = sensorPositions[sk];
            var entityFilename = 'generated/conv_' + instance.uuid + '_snesor.ptx';
            builder.createChild('sensor_' + sk)
                    .parametric(entityFilename, sensGeom)
                    .translate(-length * 0.5 + posSen, 0, height)
                    .rotate(0, 0, 0)
                    .frame('ROOT', 0, 0, 0, 0, 0, 0);
        }
    }
    
    direction(instance.name, 0);

}

function midStructure(displacement) {

    var strName = 'midStructure';

    var structure = extrusion()
            .lineTo(width, 0)
            .lineTo(width, (prfHg - openHg) / 2.0)
            .lineTo(width - prfTh, (prfHg - openHg) / 2.0)
            .lineTo(width - prfTh, (prfHg - internalH) / 2.0)
            .lineTo(width - prfD, (prfHg - internalH) / 2.0)
            .lineTo(width - prfD, (prfHg + internalH) / 2.0)
            .lineTo(width - prfTh, (prfHg + internalH) / 2.0)
            .lineTo(width - prfTh, (prfHg + openHg) / 2.0)
            .lineTo(width, (prfHg + openHg) / 2.0)
            .lineTo(width, prfHg)
            .lineTo(0, prfHg)
            .lineTo(0, (prfHg + openHg) / 2.0)
            .lineTo(prfTh, (prfHg + openHg) / 2.0)
            .lineTo(prfTh, (prfHg + internalH) / 2.0)
            .lineTo(prfD, (prfHg + internalH) / 2.0)
            .lineTo(prfD, (prfHg - internalH) / 2.0)
            .lineTo(prfTh, (prfHg - internalH) / 2.0)
            .lineTo(prfTh, (prfHg - openHg) / 2.0)
            .lineTo(0, (prfHg - openHg) / 2.0)
            .lineTo(0, 0)
            .height(length)
            .appearance(app);





    var strObject = builder
            .createChild(strName)
            .parametric(dirName + strName + '.ptx', structure)
            .rotate(90, 90, 0)
            .translate(-length / 2.0, -width / 2.0, height - tappDst - tappTh - prfHg);

    var tapp = box(width - deltaTapp, tappTh, length).appearance(appTapp);
    strObject
            .createChild("tappeto")
            .parametric(dirName + 'tappeto.ptx', tapp)
            .rotate(0, 0, 0)
            .translate(width / 2.0, prfHg + tappDst + tappTh / 2.0, 0);

    var tappCurve = extrusion()
            .moveTo(0, -cylRadius - tappDst - tappTh)
            .arcTo(0, +cylRadius + tappDst + tappTh, cylRadius + tappDst + tappTh, true)
            .lineTo(0, +cylRadius + tappDst)
            .arcTo(0, -cylRadius - tappDst, -(cylRadius + tappDst), true)
            .lineTo(0, -cylRadius - tappDst - tappTh)
            .height(width - deltaTapp)
            .appearance(appTapp);
    var tappCurveB = extrusion()
            .moveTo(0, -cylRadius - tappDst - tappTh)
            .arcTo(0, +cylRadius + tappDst + tappTh, cylRadius + tappDst + tappTh, true)
            .lineTo(0, +cylRadius + tappDst)
            .arcTo(0, -cylRadius - tappDst, -(cylRadius + tappDst), true)
            .lineTo(0, -cylRadius - tappDst - tappTh)
            .height(width - deltaTapp)
            .appearance(appTapp);

    var tappAObject = builder
            .createChild('tappA')
            .parametric(dirName + 'tappA.ptx', tappCurve)
            .rotate(90, 0, 0)
            .translate(length / 2.0, (width - deltaTapp) / 2.0, height - tappDst - tappTh - prfHg / 2.0);

    var tappBObject = builder
            .createChild('tappB')
            .parametric(dirName + 'tappB.ptx', tappCurve)
            .rotate(90, 180, 0)
            .translate(-length / 2.0, -(width - deltaTapp) / 2.0, height - tappDst - tappTh - prfHg / 2.0);

    var cylA = cylinder(cylRadius, width);
    cylA.appearance(app);
    var cylAObject = builder
            .createChild('cylA')
            .parametric(dirName + 'cylA.ptx', cylA)
            .rotate(90, 0, 0)
            .translate(-length / 2.0, width / 2.0, height - tappDst - tappTh - prfHg / 2.0);

    var cylB = cylinder(cylRadius, width);
    cylB.appearance(app);

    var cylBObject = builder
            .createChild('cylB')
            .parametric(dirName + 'cylB.ptx', cylB)
            .rotate(90, 0, 0)
            .translate(+length / 2.0, width / 2.0, height - tappDst - tappTh - prfHg / 2.0);




}
function lwMidStructure(displacement) {

    var strName = 'midStructure';

    var structure = extrusion()
            .lineTo(width+1, 0)
            .lineTo(width+1, prfHg)
            .lineTo(0, prfHg)
            .lineTo(0, 0)
            .height(length)
            .appearance(app);





    var strObject = builder
            .createChild(strName)
            .parametric(dirName + strName + '.ptx', structure)
            .rotate(90, 90, 0)
            .translate(-length / 2.0, -0.5-width / 2.0, height - tappDst - tappTh - prfHg);

    var tapp = box(width, tappTh, length).appearance(appTapp);
    strObject
            .createChild("tappeto")
            .parametric(dirName + 'tappeto.ptx', tapp)
            .rotate(0, 0, 0)
            .translate(width / 2.0, prfHg + tappDst + tappTh / 2.0, 0);

    var cylA = cylinder(cylRadius + tappDst + tappTh, width);
    cylA.appearance(appTapp);
    var cylAObject = builder
            .createChild('cylA')
            .parametric(dirName + 'cylA.ptx', cylA)
            .rotate(90, 0, 0)
            .translate(-length / 2.0, width / 2.0, height - tappDst - tappTh - prfHg / 2.0);

    var cylB = cylinder(cylRadius + tappDst + tappTh, width);
    cylB.appearance(appTapp);

    var cylBObject = builder
            .createChild('cylB')
            .parametric(dirName + 'cylB.ptx', cylB)
            .rotate(90, 0, 0)
            .translate(+length / 2.0, width / 2.0, height - tappDst - tappTh - prfHg / 2.0);




}


function leg(count, x, y) {

    var legBox = box(legWidth, legWidth, height - tappDst - tappTh - prfHg - legConeHgt).appearance(app);
    var legObject = builder
            .createChild('leg' + count)
            .parametric(dirName + 'leg' + count + '.ptx', legBox)
            .translate(x, y, legConeHgt);

    var legCone = cone(legConeRadius, legConeHgt).appearance(app);
    var legConeObject = builder
            .createChild('legCone' + count)
            .parametric(dirName + 'legCone' + count + '.ptx', legCone)
            .translate(x, y, 0);

    var legCyl = cylinder(5, legConeHgt).appearance(app);
    var legCylObject = builder
            .createChild('legCyl' + count)
            .parametric(dirName + 'legCyl' + count + '.ptx', legCyl)
            .translate(x, y, 0);
}

function lwLeg(count, x, y) {

    var legBox = box(legWidth, legWidth, height - tappDst - tappTh - prfHg).appearance(app);
    var legObject = builder
            .createChild('leg' + count)
            .parametric(dirName + 'leg' + count + '.ptx', legBox)
            .translate(x, y, 0);

}

function crossbar(count, x) {
    var cBox = box(legWidth, width - legWidth, legWidth).appearance(app);
    var cObject = builder
            .createChild('cross' + count)
            .parametric(dirName + 'cross' + count + '.ptx', cBox)
            .translate(x, 0, Math.max(height / 5, legConeHgt + legWidth));
}

function direction(convId, dpos){
    var dapp;
    var dn = convId + '_direction';
    
    if(idleOnStart){
        dapp = parametric.appearance()
        .diffuseColor(0xAAAAAA)
        .shininess(0.504);
    }else{
        dapp = parametric.appearance()
        .diffuseColor(0x00FF00)
        .shininess(0.504);
    }
    
    var dirMarker = extrusion()
            .lineTo(0, -width*0.15)
            .lineTo(width*0.075, -width*0.075)
            .lineTo(0,0)
            .height(5)
            .appearance(dapp);
    
    var dirObject = builder
            .createChild(dn)
            .parametric(dirName + dn, dirMarker)
            .rotate(0, 0, 0)
            .translate(dpos,width*0.075,height);
}

