
var imports = new JavaImporter(java.io, java.lang, java.util);

with (imports) {
    function configure3D() {
        var filename = 'generated/source_' + instance.uuid + '.ptx';
        var app = parametric.appearance()
                .diffuseColor(0x00FFBB)
                .transparency(0.5);
//    
//    builder.parametric(filename, box(6000, maxBatchWidth, hPiano).appearance(app))

        var xc = 0;
        var yc = 0;
        var zc = baseHeight;
        builder.parametric(filename, box(baseLength, baseWidth, baseHeight).appearance(app))
                .frame('creationFrame', xc, yc, zc, 0, 0, 0)
                .frame('commandsFrame', xc, baseWidth * 0.5, zc, 0, 0, 90)
                .frame('outFrame', -baseLength * 0.5, yc, zc, 0, 0, 0)
                ;

        var appG = parametric.appearance()
                .diffuseColor(Integer.decode(color));
        var entityFilename = 'generated/source_' + instance.uuid + '_entity.ptx';
        var entityGeometry = box(200, 200, 200).appearance(appG);
        builder.createChild('entity')
                .parametric(entityFilename, entityGeometry)
                .translate(xc, yc, zc)
                .frame('ROOT', 0, 0, 0, 0, 0, 0);

    };
}
