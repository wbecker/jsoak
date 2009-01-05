/*
 * jabsorb - a Java to JavaScript Advanced Object Request Broker
 * http://www.jabsorb.org
 *
 * Copyright 2007 The jabsorb team
 * Copyright (c) 2005 Michael Clark, Metaparadigm Pte Ltd
 * Copyright (c) 2003-2004 Jan-Klaas Kollhof
 *
 * This code is based on original code from the json-rpc-java library
 * which was originally based on Jan-Klaas' JavaScript o lait library
 * (jsolait).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * Call this to create a jabsorb object.
 */
function jabsorb()
{
  // The way to properly extend jabsorb is as follows:
  //   var extendingJabsorbClass = function(){
  //     var pub = jabsorb.apply(this, arguments);
  //     var pro = this.pro;
  //     //All pub and pro variables of jabsorb can be used here.
  //     return pub;
  //   }   

  /** Assign public variables to this */
  var pub = {};

  /** Assign protected variables to this */
  var pro = {};

  /** Assign private variables to this */
  var prv = {};

  /* *************************** PUBLIC VARIABLES *************************** */

  /** The number of requests that can occur at once */
  pub.max_req_active = 1;

  /** Whether the time that each async call takes should be measured */
  pub.profile_async = false;

  /** The id of the next request */
  pub.requestId = 1;

  /**
   * if true, java.util.Date object are unmarshalled to javascript dates if
   * false, no customized unmarshalling for dates is done
   */
  pub.transformDates = false;

  /**
   * Set this to true to allow synchronous (blocking) calls. This exists to
   * prevent accidentally creating synchronous code. Otherwise all methods must
   * have a callback function as the first or last argument.
   */
  pub.allowSyncCalls = false;

  /* ************************* PROTECTED VARIABLES ************************** */

  /**
   * This is a static variable that maps className to a map of functions names
   * to calls, ie Map knownClasses<ClassName,Map<FunctionName,Function>>
   */
  pro.knownClasses = {};
  
  /* ************************** PRIVATE VARIABLES *************************** */

  /** Private static final variables */
  prv.CALLABLE_REFERENCE_METHOD_PREFIX = ".ref";

  /**
   * The method creators that are iterated through in order in the addMethods()
   * method.
   */
  prv.methodCreators = [];

  /* **************************** INNER CLASSES ***************************** */
  
  /**
   * Callable Proxy constructor
   * 
   * @param objectID
   *          A unique identifier which the identity hashcode of the object on
   *          the server, if this is a reference type
   * @param javaclass
   *          The full package and classname of the object
   */
  pro.JSONRPCCallableProxy = function(objectID, javaclass)
  {
    this.objectID = objectID;
    this.javaClass = javaclass;
    this.JSONRPCType = "CallableReference";
  }

  /**
   * Exception Constructor
   * 
   * @param errorObject
   *          An object which describes the error.
   */
  prv.Exception = function(errorObject)
  {
    var m;
    for ( var prop in errorObject)
    {
      if (errorObject.hasOwnProperty(prop))
      {
        this[prop] = errorObject[prop];
      }
    }
    if (this.trace)
    {
      m = this.trace.match(/^([^:]*)/);
      if (m)
      {
        this.name = m[0];
      }
    }
    if (!this.name)
    {
      this.name = "JSONRpcClientException";
    }
  };
  // Error codes that are the same as on the bridge
  prv.Exception.CODE_REMOTE_EXCEPTION = 490;
  prv.Exception.CODE_ERR_CLIENT = 550;
  prv.Exception.CODE_ERR_PARSE = 590;
  prv.Exception.CODE_ERR_NOMETHOD = 591;
  prv.Exception.CODE_ERR_UNMARSHALL = 592;
  prv.Exception.CODE_ERR_MARSHALL = 593;

  prv.Exception.prototype = new Error();

  prv.Exception.prototype.toString = function(code, msg)
  {
    var str = "";
    if (this.name)
    {
      str += this.name;
    }
    if (this.message)
    {
      str += ": " + this.message;
    }
    if (str.length == 0)
    {
      str = "no exception information given";
    }
    return str;
  };

  /* ************************** METHOD CREATORS ***************************** */

  /**
   * A method creator for callable references.
   */
  pro.callableMethodCreator = function()
  {
    var pub = {};
    pub.accept = function(methodName)
    {
      var startIndex = methodName.indexOf("[");
      var endIndex = methodName.indexOf("]");
      return ((methodName.substring(0,
          prv.CALLABLE_REFERENCE_METHOD_PREFIX.length) == prv.CALLABLE_REFERENCE_METHOD_PREFIX)
          && (startIndex != -1) && (endIndex != -1) && (startIndex < endIndex));

    }
    pub.createMethod = function(methodName, toAddTo, dontAdd)
    {
      var startIndex = methodName.indexOf("[");
      var endIndex = methodName.indexOf("]");
      var javaClass = methodName.substring(startIndex + 1, endIndex);
      var name = methodName.substring(endIndex + 2);
      var method = prv.createMethod(name);
      if (!pro.knownClasses[javaClass])
      {
        pro.knownClasses[javaClass] = {};
      }
      pro.knownClasses[javaClass][name] = method;
      return [method];
    }
    return pub;
  }
  
  /**
   * A method creator which creates normal methods. It will always accept
   * everything, so should be at the end of methodCreators.
   */
  pro.normalMethodCreator = function()
  {
    var pub = {};
    pub.accept = function()
    {
      return true;
    }
    pub.createMethod = function(methodName, toAddTo, dontAdd)
    {
      // Create intervening objects in the path to the method name.
      // For example with the method name "system.listMethods", we first
      // create a new object called "system" and then add the "listMethod"
      // function to that object.
      var tmp = toAddTo;
      var names = methodName.split(".");
      var name;

      for (n = 0; n < names.length - 1; n++)
      {
        name = names[n];
        if (tmp[name])
        {
          tmp = tmp[name];
        }
        else
        {
          tmp[name] = {};
          tmp = tmp[name];
        }
      }
      name = names[names.length - 1];
      var method = prv.createMethod(methodName);
      // If it doesn't yet exist and it is to be added to this
      if ((!toAddTo[name]) && (!dontAdd))
      {
        tmp[name] = method;
      }
      return [method];
    }
    return pub;
  }  
  
  /* **************************** CONSTRUCTORS ****************************** */
  
  /**
   * Jabsorb constructor
   * 
   * @param callback|methods
   *          The function to call once the rpc list methods has completed. If
   *          this argument is omitted completely, then jabsorb is constructed
   *          synchronously. If this argument is an array then it is the list of
   *          methods that can be invoked on the server (and the server will not
   *          be queried for that information)
   * @param serverURL
   *          Path to JSONRpcServlet on server.
   * @param user
   *          The user name to connect to the server (if necessary)
   * @param pass
   *          The password to connect to the server (if necessary)
   */
  prv.init = function()
  {
    var argShift = 0,
        req,
        doListMethods = true,
        readyCB,
        args = [];

    for ( var i = 0; i < arguments.length; i++)
    {
      args.push(arguments[i]);
    }
    // if the first element is an array then just do add methods directly
    if (arguments[0] && typeof arguments[0] === "object" && arguments[0].length)
    {
      // go ahead and add the methods directly
      prv.addMethods(pub, arguments[0]);
      argShift++;
      doListMethods = false;
    }
    else
    {
      readyCB = pro.extractCallback(args);
    }

    // The next 3 args are passed to the http request
    prv.serverURL = args[argShift];
    prv.user = args[argShift + 1];
    prv.pass = args[argShift + 2];
    this.objectID = 0;

    pro.addMethodCreator(pro.normalMethodCreator());
    pro.addMethodCreator(pro.callableMethodCreator());

    if (doListMethods)
    {
      // Add the listMethods system methods
      prv.addMethods(pub, [ "system.listMethods" ]);
      // Make the call to list the methods
      req = comms.makeRequest("system.listMethods", []);
      // If a callback was added to the constructor, call it
      if (readyCB)
      {
        req.cb = function(result, e)
        {
          if (!e)
          {
            prv.addMethods(pub, result);
          }
          readyCB(pub, e);
        };
        comms.addRequest(req);
      }
      else
      {
        prv.addMethods(pub, comms.sendRequest(req));
      }
    }
  }
  /* **************************** PUBLIC METHODS **************************** */

  /**
   * Cancels a request that is currentlyin progress
   * 
   * @param requestId
   *          The id of the request to cancel
   */
  pub.cancelRequest = function(requestId)
  {
    comms.cancelRequest(requestId);
  };

  /**
   * Creates a new object from the bridge. A callback may optionally be given as
   * the first argument to make this an async call.
   * 
   * @param callback
   *          (optional)
   * @param constructorName
   *          The name of the class to create, which should be registered with
   *          JSONRPCBridge.registerClass()
   * @param constructorArgs
   *          The arguments the constructor takes
   * @return the new object if sync, the request id if async.
   */
  pub.createObject = function()
  {
    var args = [], callback = null, constructorName, constructorArgs, req;
    for ( var i = 0; i < arguments.length; i++)
    {
      args.push(arguments[i]);
    }
    callback = pro.extractCallback(args);
    constructorName = args[0] + ".$constructor";
    constructorArgs = args[1];

    req = comms.makeRequest(constructorName, constructorArgs, 0, callback);
    if (callback === null)
    {
      return comms.sendRequest(req);
    }
    else
    {
      comms.addRequest(req);
      return req.requestId;
    }
  };

  /**
   * Default top level exception handler
   */
  pub.default_ex_handler = function(e)
  {
    if (console && console.log)
    {
      console.log(e);
    }
    else
    {
      var str = "";
      for (a in e)
      {
        str += a + "\t" + e[a] + "\n";
      }
      alert(str);
    }
  };

  /**
   * A method which can be used in the place of a callback, which will cause the
   * call to be synchronous instead of asynchronous.
   */
  pub.doSync = function()
  {
  }

  /**
   * Converts data from json to objects used by jabsorb.
   * 
   * @param data
   *          The object which contains the json
   * @param key
   *          The key in which the json is stored.
   * 
   * @return An object used by jabsorb.
   */
  pub.fromJSON = function(data, key)
  {
    var r = data[key];
    /* Handle CallableProxy */
    if (r)
    {
      if (r.objectID && r.JSONRPCType == "CallableReference")
      {
        return pro.createCallableProxy(r.objectID, r.javaClass);
      }
      else
      {
        r = prv.extractCallableReferences(pub.transformDates ? prv
            .transformDate(r) : r);
        r = pro.postJSON(r, data)
      }
    }
    return r;
  }

  /**
   * Marshall an object to JSON format.
   * 
   * An exception will be thrown if a circular reference is detected.
   * 
   * @param o
   *          The object being converted to json
   * 
   * @return an object, { "<resultKey>": jsonString }
   */
  pub.toJSON = function(o, resultKey)
  {
    // temp variable to hold json while processing
    var json = pro.simpleToJSON(o, null, "root");
    var result={};
    result[resultKey] = json
    return result;
    //return "{\""+resultKey+"\":"+json+"}
  }

  /**
   * Converts a json string into jabsorb data types.
   * 
   * @param data
   *          The json string to parse. It should contain a value in the result
   *          field.
   * @return If data contains any Callable Proxies these will be extracted,
   *         otherwise, it returns the string converted into javascript objects.
   */
  pub.unmarshallResponse = function(data)
  {
    var obj;
    try
    {
      //TODO: for a better eval see: http://funkatron.com/site/comments/safely-parsing-json-in-javascript/
      eval("obj = " + data);
    }
    catch (e)
    {
      throw new prv.Exception( {
        code :550,
        message :"error parsing result"
      });
    }
    if (obj.error)
    {
      throw new prv.Exception(obj.error);
    }

    return pub.fromJSON(obj, "result");
  };

  /* ************************** PROTECTED METHODS *************************** */

  /**
   * Adds a method creator to the list of known method creators. The new creator
   * will have the highest priority.
   * 
   * @param methodCreator
   *          The method Creator to add. It should be an object which has two
   *          keys. The first should be "accept", take a method name and return
   *          whether it is accepted. The second be "createMethod" and should
   *          take the method name, the object it should add to and whether or
   *          not it should be added and return the created method.
   */
  pro.addMethodCreator = function(methodCreator)
  {
    prv.methodCreators.unshift(methodCreator);
  }

  /**
   * Combines objects passed as arguments to a json string
   */
  pro.combineObjectsToJSON=function()
  {
    var v = [], argIndex, key;

    for (argIndex = 0; argIndex < arguments.length; argIndex++)
    {
      for (key in arguments[argIndex])
      {
        if(typeof arguments[argIndex][key] !== "function")
        {
          v.push("\"" + key + "\": " + arguments[argIndex][key]);
        }
      }
    }
    return "{" + v.join(", ") + "}";
  }

  /**
   * Creates a new callable proxy (reference).
   * 
   * @param objectID
   *          The id of the object as determined by the server
   * @param javaClass
   *          The package+classname of the object
   * @return a new callable proxy object
   */
  pro.createCallableProxy = function(objectID, javaClass)
  {
    var cp, req, name, i;

    cp = new pro.JSONRPCCallableProxy(objectID, javaClass);
    // Then add all the cached methods to it.
    for (name in pro.knownClasses[javaClass])
    {
      // TODO: DO these get added to this's cp.pub alone or everyones??
      // I Think it may be the latter
      cp[name] = pro.knownClasses[javaClass][name];
    }
    return cp;
  };
  
  /**
   * Encodes a string into JSON format
   * 
   * @param s
   *          the string to escape
   * @return The escaped json string
   */
  pro.escapeJSONString = function(s)
  {
    /*
     * The following should suffice but Safari's regex is b0rken (doesn't
     * support callback substitutions) return "\"" +
     * s.replace(/([^\u0020-\u007f]|[\\\"])/g, escapeJSONChar) + "\"";
     */

    /* Rather inefficient way to do it */
    var parts = s.split("");
    for ( var i = 0; i < parts.length; i++)
    {
      parts[i] = prv.escapeJSONChar(parts[i]);
    }
    return "\"" + parts.join("") + "\"";
  }
  
  /**
   * Extracts the callback method from a list of arguments to a method. The
   * callback must be in either the first or last position, but not both. If
   * pub.doSync() is passed, then this will return null.
   * 
   * @param args
   *          the list of arguments to a method which may contain a callback in
   *          the first or last position
   * 
   * @return The callback if it exists, or null if no callback is found or the
   *         callback is pub.doSync().
   * @throws a
   *           prv.Exception if both the first and last methods are callbacks.
   */
  pro.extractCallback = function(args)
  {
    var callback = null;
    var typeofFirst = (typeof args[0] == "function");
    var typeofLast;
    if (args.length > 1)
    {
      typeofLast = (typeof args[args.length - 1] == "function");
    }
    else
    {
      typeofLast = null;
    }
    if (typeofFirst || typeofLast)
    {
      if (typeofFirst && typeofLast)
      {
        throw new prv.Exception( {
          code :prv.Exception.CODE_ERR_CLIENT,
          message :"A method was put in both the first and last positions, "
              + "but should only by put in one"
        });
      }
      if (typeofFirst)
      {
        callback = args.shift();
      }
      else
      {
        callback = args.pop();
      }
      if (callback == pub.doSync)
      {
        callback = null;
      }
    }
    if ((callback == null) && (!pub.allowSyncCalls))
    {
      throw new prv.Exception( {
        code :prv.Exception.CODE_ERR_CLIENT,
        message :"A synchronous call was made. To enable sync calls set "
            + "allowSyncCalls to true"
      });
    }
    return callback;
  }
  
  /**
   * Creates a callable reference on an object if it fits the form:
   * 
   * {"objectID":x "javaClass":y "JSONRPCType":"CallableReference"}
   * 
   * @param Value
   *          The object to make the callable reference from
   * @return The callable reference or null, if it couldn't be created.
   */
  pro.makeCallableReference = function(value)
  {
    if (value && value.objectID && value.javaClass
        && value.JSONRPCType == "CallableReference")
    {
      return pro.createCallableProxy(value.objectID, value.javaClass);
    }
    return null;
  };

  /**
   * Method called by fromJSON() to do any final adjustments to a json object.
   * This can be overridden to do extend this to handle circular references.
   * 
   * @param value
   *          The json value
   * @param data
   *          An object which holds the json
   * @return The adjusted valued.
   */
  pro.postJSON = function(value, data)
  {
    return value;
  }

  /**
   * Converts a simple object to JSON. Throws an exception if a circular
   * reference is found.
   * 
   * @param o
   *          The object being converted into JSON.
   * 
   * @return A string containing the JSON representation of the object o
   */
  pro.simpleToJSON = function(o, helper)
  {
    if (!helper)
    {
      helper = {};
      var state;
      helper.pre = function(o)
      {
        if (!state)
        {
          state = [];
        }
      }
      helper.markerFound = function(o)
      {
        // Remove markers, and throw exception.
        var i;
        for (i = 0; i < state.length; i++)
        {
          delete state[i][pro.simpleToJSON.marker];
        }
        throw new prv.Exception( {
          code :550,
          message :"circular reference found"
        });
      }
      helper.markerNotFound = function(o)
      {
        o[pro.simpleToJSON.marker] = 1;
        state.push(o);
      }
      helper.post = function(o, v)
      {
        // We are done dealing with object, pop it off the state and remove the
        // marker
        state.pop();
        
        //TODO: test to make sure no markers are ever left
        delete o[pro.simpleToJSON.marker];
        return "{" + v.join(", ") + "}";
      }
    }
    /**
     * Does the work of converting an object to json
     * 
     * @param o
     *          The object to convet
     * @param helper
     *          This tells the system what to do when a circ ref is found. It
     *          should be an object with 4 functions mapped to
     *          "pre","markerFound","markerNotFound","post". The first three
     *          takes o as a parameter, post also takes a vector of the
     *          processed objects and should return the result of the function
     *          (the json).
     * @param p
     *          The parent of the current object
     * @param ref
     *          The reference to o in p
     * @return The object converted to json
     */
    var _simpleToJSON = function(o, helper, p, ref)
    {
      var v = [], i, json;
      helper.pre(o);
      if (o === null || o === undefined)
      {
        return "null"; // it's null or undefined, so serialize it as null
      }
      else if (typeof o === 'string')
      {
        return pro.escapeJSONString(o);
      }
      else if (typeof o === 'number')
      {
        return o.toString();
      }
      else if (typeof o === 'boolean')
      {
        return o.toString();
      }
      else if (o.constructor === Date)
      {
        return '{javaClass: "java.util.Date", time: ' + o.valueOf() + '}';
      }
      else
      {
        // Here we put a marker on each object we come across, to check for
        // circular references. We do this since we can't do identity hash
        // codes.
        // We could compare to each element in state but this is quicker.

        // If it already has a marker, its a circ ref.
        {
          if (o[pro.simpleToJSON.marker])
          {
            json = helper.markerFound(o, p, ref);
          }
          else
          {
            json = helper.markerNotFound(o, p, ref);
          }
          if (json)
          {
            return json;
          }
        }
        if (o.constructor === Array)
        {
          for (i = 0; i < o.length; i++)
          {
            json = _simpleToJSON(o[i], helper, o, i);
            v.push(json);
          }
          return "[" + v.join(", ") + "]";
        }
        else
        {
          for ( var attr in o)
          {
            if ((attr === pro.simpleToJSON.marker)
                || (typeof o[attr] === "function"))
            {
              /* skip */
              continue;
            }
            if (o[attr] === null || o[attr] === undefined)
            {
              v.push("\"" + attr + "\": null");
            }
            else
            {
              json = _simpleToJSON(o[attr], helper, o, attr);
              v.push(pro.escapeJSONString(attr) + ": " + json);
            }
          }
          return helper.post(o, v);
        }
      }
    }
    return _simpleToJSON(o, helper, null, "root");
  }

  /**
   * A marker that can be put on objects to show they have been already
   * serialised that may be used in the helper functions
   */
  pro.simpleToJSON.marker = "$_$jabsorbed$813492";

  /* *************************** PRIVATE METHODS **************************** */

  /**
   * This is used to add a list of methods to this.
   * 
   * @param the
   *          object that methods should be added to
   * @param methodNames
   *          a list containing the names of the methods to add
   * @param dontAdd
   *          If this is set, methods wont actually added
   * @return the methods that were created
   */
  prv.addMethods = function(toAddTo, methodNames, dontAdd)
  {
    var methods = [], method, i, j;

    for (i = 0; i < methodNames.length; i++)
    {
      for (j = 0; j < prv.methodCreators.length; j++)
      {
        if (prv.methodCreators[j].accept(methodNames[i]))
        {
          //create method returns an array, so we use apply so push 
          //(which can take many arguments) adds all of them.
          methods=methods.concat(prv.methodCreators[j].createMethod(methodNames[i],
              toAddTo, dontAdd));
        }
      }
    }

    return methods;
  };

  /**
   * This creates a method that points to the serverMethodCaller and binds it
   * with the correct methodName.
   * 
   * @param methodName
   *          The name of the method to create.
   */
  prv.createMethod = function(methodName)
  {
    // This function is what the user calls.
    // This function uses a closure on methodName to ensure that the function
    // always has the same name, but can take different arguments each call.
    var serverMethodCaller = function()
    {
      var args = [];
      for ( var i = 0; i < arguments.length; i++)
      {
        args.push(arguments[i]);
      }
      var callback = pro.extractCallback(args);
      var req = comms.makeRequest(methodName, args, this.objectID, callback);
      if (!callback)
      {
        return comms.sendRequest(req);
      }
      else
      {
        // when there is a callback, add the req to the list
        comms.addRequest(req);
        return req.requestId;
      }
    };

    return serverMethodCaller;
  };

  /**
   * Escapes a character, ensuring any character with a code < 32 is escaped
   * 
   * @param c
   *          The character to escape
   * @return The escaped character
   */
  prv.escapeJSONChar = function()
  {
    var escapeChars = [ "\b", "\t", "\n", "\f", "\r" ];

    return function(c)
    {
      // Need to do these first as their ascii values are > 32 (34 & 92)
      if (c == "\"" || c == "\\")
      {
        return "\\" + c;
      }
      // Otherwise it doesn't need escaping
      if (c.charCodeAt(0) >= 32)
      {
        return c;
      }
      // Otherwise it is has a code < 32 and may need escaping.
      for ( var i = 0; i < escapeChars.length; i++)
      {
        if (c == escapeChars[i])
        {
          return "\\" + c;
        }
      }
      // it was a character from 0-31 that wasn't one of the escape chars
      return c;
    };
  }();

  /**
   * Recursivly extracts objects in the form:
   * 
   * {"objectID":x "javaClass":y "JSONRPCType":"CallableReference"}
   * 
   * and replaces it with a real callabe proxy.
   * 
   * @param root
   *          the top level element to start processing from. The values in this
   *          will change.
   * @return The same root parameter, with the values fixed up.
   */
  prv.extractCallableReferences = function(root)
  {
    var i, tmp, value;
    for (i in root)
    {
      if (typeof (root[i]) == "object")
      {
        tmp = pro.makeCallableReference(root[i]);
        if (tmp)
        {
          root[i] = tmp;
        }
        else
        {
          tmp = prv.extractCallableReferences(root[i]);
          root[i] = tmp;
        }
      }
      if (typeof (i) == "object")
      {
        tmp = pro.makeCallableReference(i);
        if (tmp)
        {
          value = root[i];
          delete root[i];
          root[tmp] = value;
        }
        else
        {
          tmp = prv.extractCallableReferences(i);
          value = root[i];
          delete root[i];
          root[tmp] = value;
        }
      }
    }
    return root;
  };

  /**
   * Traverse the resulting object graph and replace serialized date objects
   * with javascript dates. An object is replaced with a JS date when any of the
   * following conditions is true: The object has a class hint, and the value of
   * the hint is 'java.util.Date' The object does not have a class hint, and the
   * ONE AND ONLY property is 'time' Note that the traversal creates an infinite
   * loop if the object graph is not a DAG, so do not call this function after
   * fixing up circular refs.
   * 
   * @param obj
   *          root of the object graph where dates should be replaces.
   * @return object graph where serialized date objects are replaced by
   *         javascript dates.
   */
  prv.transformDate = function(obj)
  {
    var hint, foo, num, i, jsDate
    if (obj && typeof obj === 'object')
    {
      hint = obj.hasOwnProperty('javaClass');
      foo = hint ? obj.javaClass === 'java.util.Date' : obj
          .hasOwnProperty('time');
      num = 0;
      // if there is no class hint but the object has 'time' property, count its
      // properties
      if (!hint && foo)
      {
        for (i in obj)
        {
          if (obj.hasOwnProperty(i))
          {
            num++;
          }
        }
      }
      // if class hint is java.util.Date or no class hint set, but the only
      // property is named 'time', we create jsdate
      if (hint && foo || foo && num === 1)
      {
        jsDate = new Date(obj.time);
        return jsDate;
      }
      else
      {
        for (i in obj)
        {
          if (obj.hasOwnProperty(i))
          {
            obj[i] = transformDate(obj[i]);
          }
        }
        return obj;
      }
    }
    else
    {
      return obj;
    }
  }

  /* **************************** HIDDEN MODULES **************************** */

  /**
   * This object holds functions that deal with data communications with the
   * server
   */
  var comms = function()
  {

    /** Private variables */
    var comms_pub = {};
    /** Public variables */
    var comms_prv = {};

    /* Async queue globals */

    /* ************************* PRIVATE VARIABLES ************************** */

    /**
     * Maps the id of the request to the request of requests that have been sent
     * but have not got a response.
     */
    comms_prv.asyncInflight = {};

    /** Requests that have not been sent yet. */
    comms_prv.asyncRequests = [];

    /** Responses that have been returned but not yet been dealt with */
    comms_prv.asyncResponses = [];

    /** A time out for the asyncHandler function so it runs regularly */
    comms_prv.asyncTimeout = null;

    /**
     * The names with which an MS ActiveXObject should be created.
     * 
     * The search order here may seem strange, but it's actually what Microsoft
     * recommends
     */
    comms_prv.msxmlNames = [ "MSXML2.XMLHTTP.6.0", "MSXML2.XMLHTTP.3.0",
        "MSXML2.XMLHTTP", "MSXML2.XMLHTTP.5.0", "MSXML2.XMLHTTP.4.0",
        "Microsoft.XMLHTTP" ];

    /** The number of requests currently active. */
    comms_prv.numReqActive = 0;

    /* ************************** PUBLIC METHODS **************************** */

    /**
     * Add a request to be sent to the server.
     * 
     * @param req
     *          The request (which should have been created by makeRequest()) to
     *          send.
     */
    comms_pub.addRequest = function(req)
    {
      comms_prv.asyncRequests.push(req);
      comms_prv.kickAsync();
    }

    /**
     * Ensures that the response for the given request will be no longer
     * handled. This must be called before the response is acted upon, but can
     * be called after it is received.
     * 
     * @param requestId
     *          The id of the request to cancel.
     */
    comms_pub.cancelRequest = function(requestId)
    {
      /*
       * If it is in flight then mark it as canceled in the inflight map and the
       * XMLHttpRequest callback will discard the reply.
       */
      if (comms_prv.asyncInflight[requestId])
      {
        comms_prv.asyncInflight[requestId].canceled = true;
        return true;
      }
      var i;

      /*
       * If its not in flight yet then we can just mark it as canceled in the
       * the request queue and it will get discarded before being sent.
       */
      for (i in comms_prv.asyncRequests)
      {
        if (comms_prv.asyncRequests[i].requestId == requestId)
        {
          comms_prv.asyncRequests[i].canceled = true;
          return true;
        }
      }

      /*
       * It may have returned from the network and be waiting for its callback
       * to be dispatched, so mark it as canceled in the response queue and the
       * response will get discarded before calling the callback.
       */
      for (i in comms_prv.asyncResponses)
      {
        if (comms_prv.asyncResponses[i].requestId == requestId)
        {
          comms_prv.asyncResponses[i].canceled = true;
          return true;
        }
      }

      return false;
    };

    /**
     * Makes a request to send to the server.
     * 
     * @param methodName
     *          The method to call on the server
     * @param args
     *          The arguments to send with the method
     * @param objectID
     *          (optional) The id of the proxy to call this upon.
     * @param cb
     *          (optional) A callback which will be called when the response is
     *          received.
     */
    comms_pub.makeRequest = function(methodName, args, objectID, cb)
    {
      var req = {}, obj = {};
      req.requestId = pub.requestId++;
      obj.id = req.requestId;
      if ((objectID) && (objectID > 0))
      {
        obj.method = pro.escapeJSONString(".obj[" + objectID + "]."
            + methodName);
      }
      else
      {
        obj.method = pro.escapeJSONString(methodName);
      }

      req.data = pro.combineObjectsToJSON(pub.toJSON(args, "params"), obj);
      if (cb)
      {
        req.cb = cb;
      }
      if (pub.profile_async)
      {
        req.profile = {
          submit :new Date()
        };
      }
      return req;
    };

    /**
     * Sends a request to the server.
     * 
     * @param req
     *          A request created with makeRequest().
     */
    comms_pub.sendRequest = function(req)
    {
      var http;
      if (req.profile)
      {
        req.profile.start = new Date();
      }

      /* Get free http object from the pool */
      http = xhr.poolGetHTTPRequest();
      comms_prv.numReqActive++;

      /* Send the request */
      http.open("POST", prv.serverURL, !!req.cb, prv.user, prv.pass);

      /* setRequestHeader is missing in Opera 8 Beta */
      try
      {
        http.setRequestHeader("Content-type", "text/plain");
      }
      catch (e)
      {
      }

      /* Construct call back if we have one */
      if (req.cb)
      {
        http.onreadystatechange = function()
        {
          var res;
          if (http.readyState == 4)
          {
            http.onreadystatechange = function()
            {
            };
            res = {
              cb :req.cb,
              result :null,
              ex :null
            };
            if (req.profile)
            {
              res.profile = req.profile;
              res.profile.end = new Date();
            }
            else
            {
              res.profile = false;
            }
            try
            {
              res.result = comms_prv.handleResponse(http);
            }
            catch (e)
            {
              res.ex = e;
            }
            if (!comms_prv.asyncInflight[req.requestId].canceled)
            {
              comms_prv.asyncResponses.push(res);
            }
            delete comms_prv.asyncInflight[req.requestId];
            comms_prv.kickAsync();
          }
        };
      }
      else
      {
        http.onreadystatechange = function()
        {
        };
      }

      comms_prv.asyncInflight[req.requestId] = req;

      try
      {
        http.send(req.data);
      }
      catch (e)
      {
        xhr.poolReturnHTTPRequest(http);
        comms_prv.numReqActive--;
        throw new prv.Exception( {
          code :prv.Exception.CODE_ERR_CLIENT,
          message :"Connection failed"
        });
      }

      if (!req.cb)
      {
        delete comms_prv.asyncInflight[req.requestId];
        return comms_prv.handleResponse(http);
      }
      return null;
    };

    /* ************************** PRIVATE METHODS *************************** */

    /**
     * Calls the asyncHandler right away.
     */
    comms_prv.kickAsync = function()
    {
      if (!comms_prv.asyncTimeout)
      {
        comms_prv.asyncTimeout = setTimeout(comms_prv.asyncHandler, 0);
      }
    };

    /**
     * Responds to requests being received.
     */
    comms_prv.asyncHandler = function()
    {
      var res, req;
      comms_prv.asyncTimeout = null;

      while (comms_prv.asyncResponses.length > 0)
      {
        res = comms_prv.asyncResponses.shift();
        if (res.canceled)
        {
          continue;
        }
        if (res.profile)
        {
          res.profile.dispatch = new Date();
        }
        try
        {
          res.cb(res.result, res.ex, res.profile);
        }
        catch (e)
        {
          pub.toplevel_ex_handler(e);
        }
      }

      while (comms_prv.asyncRequests.length > 0
          && comms_prv.numReqActive < pub.max_req_active)
      {
        req = comms_prv.asyncRequests.shift();
        if (req.canceled)
        {
          continue;
        }
        comms.sendRequest(req);
      }
    };

    /**
     * Tries to determine the correct character set for a server connection.
     * 
     * @param http
     *          The connection to the server.
     */
    comms_prv.getCharsetFromHeaders = function(http)
    {
      var contentType, parts, i;
      try
      {
        contentType = http.getResponseHeader("Content-type");
        parts = contentType.split(/\s*;\s*/);
        for (i = 0; i < parts.length; i++)
        {
          if (parts[i].substring(0, 8) == "charset=")
          {
            return parts[i].substring(8, parts[i].length);
          }
        }
      }
      catch (e)
      {
      }
      return "UTF-8"; // default
    };

    /**
     * Deals with a response from the server.
     * 
     * @param http
     *          The connection to the server
     */
    comms_prv.handleResponse = function(http)
    {
      /* Get the charset */
      if (!this.charset)
      {
        this.charset = comms_prv.getCharsetFromHeaders(http);
      }

      /* Get request results */
      var status, statusText, data;
      try
      {
        status = http.status;
        statusText = http.statusText;
        data = http.responseText;
      }
      catch (e)
      {
        /*
         * todo: don't throw away the original error information here!! todo:
         * and everywhere else, as well! if (e instanceof Error) { alert (e.name + ": " +
         * e.message); }
         */
        xhr.poolReturnHTTPRequest(http);
        comms_prv.numReqActive--;
        comms_prv.kickAsync();
        throw new prv.Exception( {
          code :prv.Exception.CODE_ERR_CLIENT,
          message :"Connection failed"
        });
      }

      /* Return http object to the pool; */
      xhr.poolReturnHTTPRequest(http);
      comms_prv.numReqActive--;

      /* Unmarshall the response */
      if (status != 200)
      {
        throw new prv.Exception( {
          code :status,
          message :statusText
        });
      }
      ;
      return pub.unmarshallResponse(data);
    };

    /**
     * XMLHttpRequest wrapper
     */
    var xhr = function()
    {
      /** Public data */
      var xhr_pub = {};

      /** Private data */
      var xhr_prv = {};

      /* ************************ PRIVATE VARIABLES ************************* */

      /**
       * The most http connections that will be kept
       */
      xhr_prv.httpMaxSpare = 8;

      /**
       * Unused server connections
       */
      xhr_prv.httpSpare = [];

      /* ************************** PUBLIC METHODS ************************** */

      /**
       * Gets a http request from the cache.
       */
      xhr_pub.poolGetHTTPRequest = function()
      {
        // atomic test and fetch spare
        // (pop returns undefined if httpSpare is empty)
        var http = xhr_prv.httpSpare.pop();
        if (http)
        {
          return http;
        }
        return xhr_prv.getHTTPRequest();
      };

      /**
       * Returns a server connection to the cache.
       * 
       * @param http
       *          The connection to return
       */
      xhr_pub.poolReturnHTTPRequest = function(http)
      {
        if (xhr_prv.httpSpare.length >= xhr_prv.httpMaxSpare)
        {
          delete http;
        }
        else
        {
          xhr_prv.httpSpare.push(http);
        }
      };

      /* ************************* PRIVATE METHODS ************************** */

      /**
       * Gets a fresh http connection
       * 
       * @return A new http connection
       */
      xhr_prv.getHTTPRequest = function()
      {
        /*
         * Look for a browser native XMLHttpRequest implementation
         * (Mozilla/IE7/Opera/Safari, etc.)
         */
        try
        {
          xhr_prv.httpObjectName = "XMLHttpRequest";
          return new XMLHttpRequest();
        }
        catch (e)
        {
        }

        /* Microsoft MSXML ActiveX for IE versions < 7 */
        for ( var i = 0; i < comms_prv.msxmlNames.length; i++)
        {
          try
          {
            xhr_prv.httpObjectName = comms_prv.msxmlNames[i];
            return new ActiveXObject(comms_prv.msxmlNames[i]);
          }
          catch (e)
          {
          }
        }

        /* None found */
        xhr.httpObjectName = null;
        throw new prv.Exception( {
          code :0,
          message :"Can't create XMLHttpRequest object"
        });
      }
      return xhr_pub;
    }();

    return comms_pub;
  }();

  /* *********************** OBJECT INITIALISATION ************************** */

  // Set the exception handler to something.
  // The user can reset this to something else.
  pub.toplevel_ex_handler = pub.default_ex_handler;

  // Call the constructor
  prv.init.apply(this, arguments);
  
  // Give inheriting classes a way to access protected variables (see top)
  this.pro = pro;
  
  // Give access to public variables
  return pub;
};

function xxx(){}