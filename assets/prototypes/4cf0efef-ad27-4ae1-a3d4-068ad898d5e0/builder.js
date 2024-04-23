

function configure3D() {
    print("Script file: " + script);
    var modelFile = localFile('fanuc-m170-iC50H.xml');
    var robotModel = builder.createChildModel(modelFile);

//    robotModel.getObject("link1").collision(localFile("/models/Link1.csx"), instance.uuid, "sidel");


}
