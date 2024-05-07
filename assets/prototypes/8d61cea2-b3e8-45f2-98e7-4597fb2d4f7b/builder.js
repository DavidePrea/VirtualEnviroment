
function configure3D() {
    var dirName = 'generated/' + instance.uuid + '/';

    var app = parametric.appearance()
            .diffuseColor(0xE6E6E6)
            .emissiveColor(0x000000)
            .specularColor(0x1A1A1A)
            .shininess(0.504);

    var ep = extrusion()
            .lineTo(80, 00)
            .lineTo(80, 80)
            .lineTo(0, 80)
            .lineTo(0, 0)
            .height(10)
            .appearance(app);

    var nomePar = "piede1";
    builder
            .createChild(nomePar)
            .parametric(dirName + nomePar + '.ptx', ep)
            .translate(-40, -40, 0);

    nomePar = "piede2";
    builder
            .createChild(nomePar)
            .parametric(dirName + nomePar + '.ptx', ep)
            .translate(length - 40, -40, 0);

    ep = extrusion()
            .lineTo(40, 00)
            .lineTo(40, 40)
            .lineTo(0, 40)
            .lineTo(0, 0)
            .height(height - 10)
            .appearance(app);

    nomePar = "asta1";
    builder
            .createChild(nomePar)
            .parametric(dirName + nomePar + '.ptx', ep)
            .translate(-20, -20, 10);
    nomePar = "asta2";
    builder
            .createChild(nomePar)
            .parametric(dirName + nomePar + '.ptx', ep)
            .translate(length - 20, -20, 10);


        app = parametric.appearance()
                .diffuseColor(0xE6E6E6)
                .emissiveColor(0x000000)
                .specularColor(0x1A1A1A)
                .shininess(0.504)
                .transparency(0.5);

        ep = extrusion()
                .lineTo(length, 0)
                .lineTo(length, 10)
                .lineTo(0, 10)
                .lineTo(0, 0)
                .height(height - 210 - 40)
                .appearance(app);
        nomePar = "vetro";
        builder
                .createChild(nomePar)
                .parametric(dirName + nomePar + '.ptx', ep)
                .translate(0, -5, 210);

}
