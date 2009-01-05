Jsoak.addTest(function()
{
  var pub=Jsoak.JsUnitTest();
  pub.testMultiplication=function()
  {
    console.log("test running!")

    assertEquals("Testing Multiplication",3*3,6);
  }
  return pub;
}());