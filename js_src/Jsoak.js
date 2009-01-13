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
      try
      {
        prv.doTestSuite(prv.tests[i])
      }
      catch (ex) 
      {
        prv.bridge.counter.addFailure("error in test suite: "+ex.testSuiteName, ex.message, function(){});
        console.log("Test suite failed. Reason: "+ ex.message);
        console.log(ex);  
      }
      
    }
  };
  prv.doTestSuite = function (test) {
    var methodName;
    try
    {
      test[prv.setupString]();
    }
    catch(ex) 
    {
      ex.message = "Exception in test setup: "+ex.message;
      ex.testSuiteName = test.testSuiteName;
      throw ex;
    }
    for(methodName in test)
    {
      if(prv.isMethodATest(test, methodName))
      {
  	    try 
  	    {
          prv.performTest(test.testSuiteName, test[methodName], methodName);
  	    } 
    	  catch (ex) 
    	  {
          console.log(ex);
    	  }
      }
    }
    try
    {
      test[prv.teardownString]();
    }
    catch(ex) 
    {
      ex.message = "Exception in test teardown: "+ex.message;
      ex.testSuiteName = test.testSuiteName;
      throw ex;
    }
  };
  
  prv.isMethodATest = function (test, methodName) {
    return (methodName.substring(0,prv.testPrefix.length)===prv.testPrefix)
           && (typeof test[methodName] === "function");
  };
  
  prv.performTest = function (testSuiteName, test, methodName) {
    try
    {
      test();
      prv.bridge.counter.addSuccess(testSuiteName+"."+methodName, function () {});
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