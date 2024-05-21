
function configure3D() {
	var modelFile = localFile('model/omino.xml');
    var opModel = builder.createChildModel(modelFile);
	opModel.rotate(90, 0,0);

}
