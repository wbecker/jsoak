var NumericTests = function()
{
  var pub = Jsoak.JsUnitTest();
  pub.testMultiplication=function()
  {
    assertEquals("Testing Multiplication",3*3,6);
  }
  pub.testAddition=function()
  {
    assertEquals("Testing Addition",1+1,2);
  }
  return pub;
};
Jsoak.addTestSuite(NumericTests());