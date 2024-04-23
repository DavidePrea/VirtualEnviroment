

var imports = new JavaImporter(java.io, java.lang, java.util);

with (imports) {
    function configure3D() {
        var filename = 'generated/source_' + instance.uuid + '.ptx';
        var app = parametric.appearance()
                .diffuseColor(0x00FFEE)
                .transparency(0.5);
//    
//    builder.parametric(filename, box(6000, maxBatchWidth, hPiano).appearance(app))

        var xc = 0;
        var yc = 0;
        var zc = baseHeight;
        builder.parametric(filename, box(baseWidth, baseLength, baseHeight).appearance(app))
                .frame('creationFrame', xc, yc, zc, 0, 0, 0)
                .frame('commandsFrame', -baseWidth * 0.5, yc, zc, 0, 0, 0);

        var appG = parametric.appearance()
                .diffuseColor(Integer.decode(color));
        var entityFilename = 'generated/source_' + instance.uuid + '_entity.ptx';
        var entityGeometry = box(entityLength, entityWidth, entityHeight)
                .appearance(appG);
        builder.createChild('entity')
                .parametric(entityFilename, entityGeometry)
                .translate(xc, yc, zc)
                .frame('ROOT', 0, 0, 0, 0, 0, 0);

    };
}