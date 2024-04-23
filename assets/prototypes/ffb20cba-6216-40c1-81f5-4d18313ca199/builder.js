
function configure3D() {
    var filename = 'generated/pl_' + instance.uuid + '.ptx';
//    var filename = 'plc_cube.wrl';
    var app = parametric.appearance()
            .diffuseColor(0xFFFF00)
            .transparency(0.5);
//    
//    builder.parametric(filename, box(6000, maxBatchWidth, hPiano).appearance(app))

    var size = 300;
    builder.createChild('pl_geom').parametric(filename, box(size, size, size).appearance(app))
            .frame('control', 0, 0, size, 0, -90, 0);
//    builder.createChild('pl_geom').vrml(localFile(filename), lengthUnit.cm)
//            .frame('control', xc, yc, zc, 0, -90, 0);
    

}
