try{
  if(!console){}
}
catch(e){
  try{
    if(!window){}
    if(!window.console)
    {
      document.getElementById("logging").style.display="block";
      var addPara = function(message)
      {
        var para = document.createElement("p");
        if(message)
        {
	        addToPara(para,message);
        }
        var loggingDiv = document.getElementById("logging");
        if(loggingDiv!==null){
          loggingDiv.appendChild(para);
          return para;
        }
        else{
          alert(message);
        }
      };
      var addToPara = function(p,message) 
      {
    	var messageNode = document.createTextNode(message);
 	    p.appendChild(messageNode);
 	    p.appendChild(document.createElement("br"));
      };
      addPara("no console defined");
      window.console={};
      window.console.log=function(){
    	var i, arg, message;
    	for (i = 0; i < arguments.length; i++) 
    	{
          arg = arguments[i];
          p=addPara();
          if(typeof(arg)==='object'){
            var o;
            for(o in arg){
              addToPara(p,o+": "+arg[o]);
            }
          }
          addToPara(p,arg);	
    	  
    	}
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
    setTimeout = f;
  }
}
