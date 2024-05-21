function customize() {

    var operationsRequestFile = 'prodPlan.csv';
    var path = 'inputSource/' + instance.uuid + '/' + instance.name + '_' ;
    var dst =path + operationsRequestFile;
//'inputSource/' + instance.uuid + '/' +
    copy(operationsRequestFile, dst);
    parameter('prodPlan', dst);


}
