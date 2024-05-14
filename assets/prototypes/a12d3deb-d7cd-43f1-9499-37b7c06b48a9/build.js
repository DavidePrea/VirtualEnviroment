function configure3D() {
    
    app = parametric.appearance().diffuseColor(0x0000FF);
    builder.parametric('parametrics/' + instance.uuid + '.ptx', box(width, depth, height).appearance(app));
}

