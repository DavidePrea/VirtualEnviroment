// PARAMETERS
var width;
var height;
var radius;
var angle;
var extraLength = 0;
var BASELINE_HEIGHT = 958;
var direction;
var legs;

//--- profile params ---
var prfD = 14; // profile depth
var prfTh = 4.5; // profile thickness
var prfHg = 50; // profile height
var openHg = 14; // opening of the profile
var internalH = 23.4; // altezza interna profilo

var beltThickness = 1.5; // spessore tappeto
var deltaTapp = 12; // differenza tappeto - width
var tappDst = 1.5; // distanza tappeto - base

var cylRadius = 50 / 2.0;

var legWidth = 25;
var legConeHgt = 30;
var legConeRadius = 15;
var legInternalDisp = 150;


var structureAppearance = parametric.appearance()
        .diffuseColor(0xE6E6E6)
        .emissiveColor(0x000000)
        .specularColor(0x1A1A1A)
        .shininess(0.504);

var beltAppearance = parametric.appearance()
        .diffuseColor(0xFFFFFF)
        .emissiveColor(0x000000)
        .specularColor(0x7F7F7F)
        .shininess(0.409)
        .ambientColor(0x7F7F7F);

var dirName = 'generated/' + instance.uuid + '/';

var minAngle;


function configure3D() {

    minAngle = angle < 180 ? true : false;
    var alfa = java.lang.Math.toRadians(angle);
    var sina = Math.sin(alfa);
    var cosa = Math.cos(alfa);
    var sina2 = Math.sin(alfa * 0.5);
    var cosa2 = Math.cos(alfa * 0.5);

    var convObject = builder.createChild(instance.name + "_main");



    if (!lwGeom) {
        var bordersFile = localFile('conveyor/model.xml');
        var borders = convObject.createChildModel(instance.name, bordersFile);
        var ba = borders.getObject(instance.name + '.relocatorRotA');
        var bb = borders.getObject(instance.name + '.relocatorRotB');
    }

    if (direction === 'right') {
        convObject.rotate(0, 0, -angle).translate(0, -radius, 0);
        if (!lwGeom) {
            bb.translate((extraLength - 100), radius, 0).rotate(0, 0, 180);
            ba.translate((-radius * sina - (extraLength - 100) * cosa), (radius * cosa - (extraLength - 100) * sina), 0).rotate(0, 0, angle + 180);
        }
        builder
                .frame('startFrame', -extraLength, 0, height, 0, 0, 0)
                .frame('endFrame', radius * sina + extraLength * cosa, -radius * (1 - cosa) - extraLength * sina, height, 0, 0, -angle)
                .frame('commandsInFrame', (radius + width * 0.5) * sina + extraLength * cosa, -radius * (1 - cosa) + width * 0.5 * cosa - extraLength * sina, height, 0, 0, -angle)
                .frame('sensorsOutFrame', radius * sina2 + extraLength * cosa2, -radius * (1 - cosa2) - extraLength * sina2, height, 0, 0, -angle);
    } else {
        builder
                .frame('startFrame', -extraLength, 0, height, 0, 0, 0)
                .frame('endFrame', radius * sina + extraLength * cosa, radius * (1 - cosa) + extraLength * sina, height, 0, 0, angle)
                .frame('commandsInFrame', (radius + width * 0.5) * sina + extraLength * cosa, radius * (1 - cosa) - width * 0.5 * cosa + extraLength * sina, height, 0, 0, angle)
                .frame('sensorsOutFrame', radius * sina2 + extraLength * cosa2, radius * (1 - cosa2) + extraLength * sina2, height, 0, 0, angle);
        convObject.rotate(0, 0, 180).translate(0, +radius, 0);
        if (!lwGeom) {
            ba.translate((extraLength - 100), radius, 0);
            bb.translate((-radius * sina - (extraLength - 100) * cosa), (radius * cosa - (extraLength - 100) * sina), 0).rotate(0, 0, angle);
        }
    }
//
    if (!lwGeom) {
        var ca1 = borders.getObject(instance.name + '.carterA1');
        var ca2 = borders.getObject(instance.name + '.carterA2');
        var cb1 = borders.getObject(instance.name + '.carterB1');
        var cb2 = borders.getObject(instance.name + '.carterB2');
//
        var carterDisp = (width - 500) / 2.0;
        ca1.translate(-carterDisp, 0, height - BASELINE_HEIGHT);
        ca2.translate(+carterDisp, 0, height - BASELINE_HEIGHT);
        cb1.translate(-carterDisp, 0, height - BASELINE_HEIGHT);
        cb2.translate(+carterDisp, 0, height - BASELINE_HEIGHT);
    }

    curveStructure(convObject);
    belt(convObject);

    var s1 = straightStep(1, convObject);
    s1.translate(0, radius, height);



    var s2 = straightStep(2, convObject);
    s2.rotate(0, 0, (180 + angle))
            .translate(-radius * sina, radius * cosa, height);


    legs = Math.max(legs, 2);
    print("LEGS:" + legs);
    var legsAngle = angle / (legs - 1);
    for (var i = 0; i < legs; i++) {
        print("create leg");
        var cLegs = legCouple(i, convObject);
        cLegs.rotate(0, 0, i * legsAngle);
    }

    if (extraLength > 200) {
        // gambe inizio
        var legStartParent = convObject.createChild('legCouple' + (legs + 1));
        var legStart = legCouple(legs + 1, legStartParent);
        legStart.translate(extraLength - legInternalDisp, 0, 0);

        // gambe fine
        var legEndParent = convObject.createChild('legCouple' + (legs + 2));
        var legEnd = legCouple(legs + 2, legEndParent);
        legEnd.translate(-(extraLength - legInternalDisp), 0, 0);
        legEndParent.rotate(0, 0, angle);
    }

    directionArrow(instance.name, 0);



}

function curveStructure(mainObject) {
    var r1 = (radius - width / 2.0);
    var r2 = (radius + width / 2.0);

    var alfa = java.lang.Math.toRadians(angle);
    var sina = Math.sin(alfa);
    var cosa = Math.cos(alfa);

    var h1 = (prfHg - openHg) / 2.0;
    var zLow = height - prfHg - tappDst - beltThickness;

    var tappCurve = extrusion()
            .moveTo(0, r1)
            .lineTo(0, r2)
            .arcTo(-r2 * sina, r2 * cosa, r2, minAngle)
            .lineTo(-r1 * sina, r1 * cosa)
            .arcTo(0, r1, -r1, minAngle)
            .height(h1)
            .appearance(structureAppearance);

    var tapCurveObject = mainObject
            .createChild('tappCurveA')
            .parametric(dirName + 'tappCurveA.ptx', tappCurve)
            .rotate(0, 0, 0)
            .translate(0, 0, zLow);

    var r1d = (r1 + prfD);
    var r2d = (r2 - prfD);
    var tappCurveD = extrusion()
            .moveTo(0, r1d)
            .lineTo(0, r2d)
            .arcTo(-r2d * sina, r2d * cosa, r2d, minAngle)
            .lineTo(-r1d * sina, r1d * cosa)
            .arcTo(0, r1d, -r1d, minAngle)
            .height(openHg)
            .appearance(structureAppearance);

    var tapCurveDObject = mainObject
            .createChild('tappCurveD')
            .parametric(dirName + 'tappCurveD.ptx', tappCurveD)
            .rotate(0, 0, 0)
            .translate(0, 0, zLow + h1);

    var tapCurveObjectB = mainObject
            .createChild('tappCurveB')
            .parametric(dirName + 'tappCurveB.ptx', tappCurve)
            .rotate(0, 0, 0)
            .translate(0, 0, zLow + openHg + h1);

}

function belt(mainObject) {

    var diff = (width - deltaTapp) / 2.0;
    var r1 = (radius - diff);
    var r2 = (radius + diff);

    var alfa = java.lang.Math.toRadians(angle);
    var sina = Math.sin(alfa);
    var cosa = Math.cos(alfa);

    var zLow = height - beltThickness;

    var beltMain = extrusion()
            .moveTo(0, r1)
            .lineTo(0, r2)
            .arcTo(-r2 * sina, r2 * cosa, r2, minAngle)
            .lineTo(-r1 * sina, r1 * cosa)
            .arcTo(0, r1, -r1, minAngle)
            .height(beltThickness)
            .appearance(beltAppearance);

    var beltMainObject = mainObject
            .createChild("beltMain")
            .parametric(dirName + 'beltMain.ptx', beltMain)
            .rotate(0, 0, 0)
            .translate(0, 0, zLow);

}

function straightStep(index, mainObject) {

    var parent = mainObject.createChild('straight' + index);
    var z0 = -(cylRadius + tappDst + beltThickness); // metto la parte sopra a filo zero z


    var beltCurve = extrusion()
            .moveTo(0, -cylRadius - tappDst - beltThickness)
            .arcTo(0, +cylRadius + tappDst + beltThickness, cylRadius + tappDst + beltThickness, minAngle)
            .lineTo(0, +cylRadius + tappDst)
            .arcTo(0, -cylRadius - tappDst, -(cylRadius + tappDst), minAngle)
            .lineTo(0, -cylRadius - tappDst - beltThickness)
            .height(width - deltaTapp)
            .appearance(beltAppearance);

    var beltCurveAObject = parent
            .createChild('beltCurveA' + index)
            .parametric(dirName + 'beltCurveA' + index + '.ptx', beltCurve)
            .rotate(90, 0, 0)
            .translate(extraLength, (width - deltaTapp) / 2.0, z0);


    var r2c = width / 2.0;
    var cylA = cylinder(cylRadius, width);
    cylA.appearance(structureAppearance);
    var cylAObject = parent
            .createChild('cylA' + index)
            .parametric(dirName + 'cylA' + index + '.ptx', cylA)
            .rotate(90, 0, 0)
            .translate(extraLength, r2c, z0);

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
            .height(extraLength)
            .appearance(structureAppearance);





    var strObject = parent
            .createChild('str' + index)
            .parametric(dirName + 'str' + index + '.ptx', structure)
            .rotate(90, 90, 0)
            .translate(0, -width / 2.0, -tappDst - beltThickness - prfHg);

    var beltBox = box(extraLength, width - deltaTapp, beltThickness).appearance(beltAppearance);
    var strObject = parent
            .createChild('beltStr' + index)
            .parametric(dirName + 'beltStr' + index + '.ptx', beltBox)
            .rotate(0, 0, 0)
            .translate(extraLength / 2.0, 0, -beltThickness);
    return parent;

}


function leg(count, mainObject) {

    var legParent = mainObject.createChild('legParent' + count);

    var legBox = box(legWidth, legWidth, height - tappDst - beltThickness - prfHg - legConeHgt).appearance(structureAppearance);
    var legObject = legParent
            .createChild('leg' + count)
            .parametric(dirName + 'leg' + count + '.ptx', legBox)
            .translate(0, 0, legConeHgt);

    var legCone = cone(legConeRadius, legConeHgt).appearance(structureAppearance);
    var legConeObject = legParent
            .createChild('legCone' + count)
            .parametric(dirName + 'legCone' + count + '.ptx', legCone)
            .translate(0, 0, 0);

    var legCyl = cylinder(5, legConeHgt).appearance(structureAppearance);
    var legCylObject = legParent
            .createChild('legCyl' + count)
            .parametric(dirName + 'legCyl' + count + '.ptx', legCyl)
            .translate(0, 0, 0);
    return legParent;
}

function lwLeg(count, mainObject) {

    var legParent = mainObject.createChild('legParent' + count);

    var legBox = box(legWidth, legWidth, height - tappDst - beltThickness - prfHg).appearance(structureAppearance);
    var legObject = legParent
            .createChild('leg' + count)
            .parametric(dirName + 'leg' + count + '.ptx', legBox)
            .translate(0, 0, 0);

    return legParent;

}

function legCouple(index, mainObject) {

    var couple = mainObject.createChild('couple_' + index);
    if (lwGeom) {
        var leg1 = lwLeg((index + 1) * 10 + 1, couple);
        leg1.translate(0, radius + width / 2.0 - legWidth, 0);
        var leg2 = lwLeg((index + 1) * 10 + 2, couple);
        leg2.translate(0, radius - width / 2.0 + legWidth, 0);
        var cross = crossbar((index + 1) * 10 + 3, couple);
        cross.translate(0, radius, Math.max(height / 5, legConeHgt + legWidth));
    } else {
        var leg1 = leg((index + 1) * 10 + 1, couple);
        leg1.translate(0, radius + width / 2.0 - legWidth, 0);
        var leg2 = leg((index + 1) * 10 + 2, couple);
        leg2.translate(0, radius - width / 2.0 + legWidth, 0);
        var cross = crossbar((index + 1) * 10 + 3, couple);
        cross.translate(0, radius, Math.max(height / 5, legConeHgt + legWidth));
    }

    return couple;
}

function crossbar(count, parent) {
    var cBox = box(legWidth, width - legWidth, legWidth).appearance(structureAppearance);
    var cObject = parent
            .createChild('cross' + count)
            .parametric(dirName + 'cross' + count + '.ptx', cBox);

    return cObject;
}

function directionArrow(convId, dpos){
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
