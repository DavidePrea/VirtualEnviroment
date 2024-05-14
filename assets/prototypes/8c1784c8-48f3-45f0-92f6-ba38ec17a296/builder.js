
function configure3D() {
    var filename = 'generated/simplephotocell_' + instance.uuid + '.ptx';
    var app = parametric.appearance()
            .diffuseColor(0xFF0000)
            .transparency(0.5);
//    
//    builder.parametric(filename, box(6000, maxBatchWidth, hPiano).appearance(app))

    var xc = 0;
    var yc = 0;
    var zc = 0;
    builder.createChild('pc_geom').parametric(filename, box(length, width, height).appearance(app))
            .frame('reference', xc, yc, zc, 0, 0, 0);
    

}
