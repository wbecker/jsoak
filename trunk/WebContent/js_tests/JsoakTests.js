Jsoak.addTestSuite(function()
{
  var pub = Jsoak.JsUnitTest();
  pub.testAddingTests = function()
  {
    var JsoakToTest = JsoakClass();
    JsoakToTest.addTestSuite(NumericTests());
    assertEquals("Amount of Test Suites",JsoakToTest.getTestSuiteCount(),1);
    assertEquals("Amount of Tests",JsoakToTest.getTestCount(),2);
  };
  pub.testRunningTests = function()
  {
    var successes = 0, 
        failures  = 0,
        JsoakToTest = JsoakClass();
    JsoakToTest.setBridge({ counter: {
      addSuccess: function () {
        successes++;
      },
      addFailure: function () {
        failures++;
      }
    }});
    JsoakToTest.addTestSuite(NumericTests());
    JsoakToTest.runTests();
    assertEquals("Amount of Successes",successes,1);
    assertEquals("Amount of Failures",failures,1);
    
  };
  return pub;
}());