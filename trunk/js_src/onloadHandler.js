var onloadHandler = function()
{
  var action = function()
  {
    if (document.body)
    {
      document.body.onload = jabsorb( function(jsonrpc)
      {
        Jsoak.setBridge(jsonrpc);
        Jsoak.runTests();
      }, "JSON-RPC");
      return true;
    }
    return false;
  }
  var whenBodyExists = function()
  {
    if (!action())
    {
      setTimeout(whenBodyExists, 200);
    }
  }
  whenBodyExists();
};
onloadHandler();