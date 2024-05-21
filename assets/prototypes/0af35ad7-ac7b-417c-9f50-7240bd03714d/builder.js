
var imports = new JavaImporter(java.io, java.lang, java.util);

with (imports) {
    function configure3D() {
        var filename = 'generated/source_' + instance.uuid + '.ptx';
        var app = parametric.appearance()
                .diffuseColor(0xFFC300)
                .transparency(0.5);
//    
//    builder.parametric(filename, box(6000, maxBatchWidth, hPiano).appearance(app))

        var xc = 0;
        var yc = 0;
        var zc = sourceSzZ;
        builder.parametric(filename, box(sourceSzX, sourceSzY, sourceSzZ).appearance(app))
                .frame('creationFrame', xc, yc, zc, 0, 0, 0)
                .frame('commandsFrame', -sourceSzX * 0.5, yc, zc, 0, 0, 0);


        builder.createChildModel('entity', localFile(modelFile))
                .translate(xc, yc, zc)
                .rotate(0, 0, 0)
                .frame('ROOT', 0, 0, 0, 0, 0, 0);

    };
}