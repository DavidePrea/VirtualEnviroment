
function configure3D() {
    var filenameBody = 'generated/ds_body_' + instance.uuid + '.ptx';
    var filenameBeam = 'generated/ds_beam_' + instance.uuid + '.ptx';
    var appBody = parametric.appearance()
            .diffuseColor(0xAAAAAA);
    var app = parametric.appearance()
            .diffuseColor(0xFF0000);

//    
//    builder.parametric(filename, box(6000, maxBatchWidth, hPiano).appearance(app))
    var bodyLength = 30;

    var group = builder.createChild('ds_geom')
            .frame('reference', bodyLength, 0, 0, 0, 0, 0);
    group.createChild('ds_body')
            .parametric(filenameBody, cylinder(10, bodyLength).appearance(appBody))
            .rotate(0, 90, 0);
    group.createChild('ds_beam')
            .parametric(filenameBeam, cylinder(2, maxDistance).appearance(app))
            .rotate(0, 90, 0)
            .translate(bodyLength, 0, 0);




}
