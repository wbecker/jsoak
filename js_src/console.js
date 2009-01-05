try{
  if(!console){}
}
catch(e){
  try{
    if(!window){}
    if(!window.console)
    {
      var addPara = function(message)
      {
        var para = document.createElement("p");
        var messageNode = document.createTextNode(message);
        para.appendChild(messageNode);
        var loggingDiv = document.getElementById("logging");
        if(loggingDiv!==null){
          loggingDiv.appendChild(para);
        }
        else{
          alert(message);
        }
      }
      addPara("no console defined");
      window.console={};
      console.log=function(message){
        addPara(message);
      }
    }
  }
  catch(e2){
    window={};
    document={};
    var f=function(){return f};
    f.appendChild=f;
    document.createElement=f;
    document.createTextNode=f;
    document.getElementById=f;
    console={};
    console.log=f;
  }
}
