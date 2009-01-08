Jsoak.KeystrokeEmulator=function()
{
  var pub=Jsoak.JsUnitTest(), 
      prv={};

  pub.typeString = function (intField,testString) {
    var i;
    var evt;
    for (i = 0; i < testString.length; i++) {
      pub.fireWithChar(intField,testString.charAt(i));
    }
  };
  
  pub.fireWithChar = function (intField,c) {
    if(document.createEvent) {
      try {
    	pub.fireWithChar_Firefox(intField,c);
      }catch(e) {
    	pub.fireWithChar_Chrome(intField,c);
      }
    }
    else {
      pub.fireWithCharIE(intField, c);
    }
  };
  
  pub.fireWithCharIE = function (intField, c) {
    var evt = document.createEventObject();
    evt.charCode = c.charCodeAt(0);
    intField.domElement.fireEvent("onkeypress",evt);
    var result = evt.result;
    if(result) {
      pub.setTextForRecalcitrantBrowsers(intField, c);
    }
  };
  
  pub.fireWithChar_Firefox = function (intField, c) {
    var evt=document.createEvent('KeyboardEvent');
    evt.initKeyEvent( "keypress", true, true, window, false, false, false,                                                       
        false, 0, c.charCodeAt(0));
    intField.domElement.dispatchEvent(evt);
  };
  
  pub.fireWithChar_Chrome = function (intField, c) {
    var evt=document.createEvent('KeyboardEvent');
    evt.initKeyboardEvent("keypress", true, true, window, false, false, false,                                                       
       false, 0, c.charCodeAt(0));
    if(evt.charCode===0) {
      evt.testCharCode = c.charCodeAt(0); 
    }
    var result = intField.domElement.dispatchEvent(evt);
    if(result) {
      pub.setTextForRecalcitrantBrowsers(intField, c);
    }
  };
  
  pub.setTextForRecalcitrantBrowsers = function (intField, c) {
    intField.setText(intField.getText()+c);
  };

  return pub;
};
