var hPiano = 900;
var lenght = 500;

var imports = new JavaImporter(java.io, java.lang, java.util);

with (imports) {
    function configure3D() {
        var filename = 'generated/source_' + instance.uuid + '.ptx';
        var app = parametric.appearance()
                .diffuseColor(0xFF0000)
                .transparency(0.5);
//    
//    builder.parametric(filename, box(6000, maxBatchWidth, hPiano).appearance(app))

        var xc = 0;
        var yc = 0;
        var zc = hPiano;
        builder.parametric(filename, box(lenght, lenght, hPiano).appearance(app))
                .frame('creationFrame', xc, yc, zc, 0, 0, 0);


    };
}