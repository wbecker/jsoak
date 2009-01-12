/**
 * Javascript unit test framework
 * @return public variables
 */
var JsoakClass=function()
{
  /** Public variables*/
  var pub={};
  /** Private variables*/
  var prv={};
  
  prv.tests=[];
  
  /**
   * Adds a unit test
   * @param test the test to add
   */
  pub.addTestSuite=function(test)
  {
    prv.tests.push(test);
  }
  prv.testPrefix="test";
  prv.setupString="setup";
  prv.teardownString="teardown";
  prv.bridge=null;
  
  pub.setBridge=function(bridge){
    prv.bridge=bridge;
  };
  
  pub.getTestSuiteCount = function () {
    return prv.tests.length;
  };
  
  pub.getTestCount = function () {
    return prv.determineAmountOfTests();
  };
  
  prv.determineAmountOfTests = function () {
    var i, test, methodName, amountOfTests=0;
    for(i=0;i<prv.tests.length;i++)
    {
      test = prv.tests[i];
      for(methodName in test)
      {
        if(prv.isMethodATest(test, methodName))
        {
          amountOfTests++;
        }
      }
    }
    return amountOfTests;
  };
  /**
   * Run all registered tests
   */
  pub.runTests=function()
  {
    var i;
    for(i=0;i<prv.tests.length;i++)
    {
      prv.doTestSuite(prv.tests[i])
    }
  };
  prv.doTestSuite = function (test) {
    var methodName;
    test[prv.setupString]();
    for(methodName in test)
    {
      if(prv.isMethodATest(test, methodName))
      {
    	try {
          prv.performTest(test[methodName], methodName);
    	} 
    	catch (ex) {
          console.log(ex);
    	}
      }
    }
    test[prv.teardownString]();
  };
  
  prv.isMethodATest = function (test, methodName) {
    return (methodName.substring(0,prv.testPrefix.length)===prv.testPrefix)
           && (typeof test[methodName] === "function");
  };
  
  prv.performTest = function (test, methodName) {
    try
    {
      test();
      prv.bridge.counter.addSuccess(methodName, function () {});
      console.log(methodName + " succeeded.")
    }
    catch(e)
    {
      var errorMessage;
      if(e.jsUnitMessage) {
    	errorMessage = e.comment+" - "+e.jsUnitMessage;
      } 
      else {
    	if(e.fileName && e.lineNumber) {
    	  errorMessage = "[" + e.fileName + "@" + e.lineNumber + "] ";
    	}
    	else if(e.fileName) {
      	  errorMessage = "[" + e.fileName + "] ";
      	}
    	else if(e.lineNumber) {
      	  errorMessage = "[ line: " + e.lineNumber + "] ";
      	}
    	errorMessage = errorMessage + e.name+": "+e.message;
      }
      prv.bridge.counter.addFailure(methodName, errorMessage, function(){});
      console.log(methodName+" failed. Reason1: "+ e.jsUnitMessage);
      console.log(e)
    }   
  };
  return pub;
};

var Jsoak = JsoakClass();