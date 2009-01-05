
/**
 * @class
 * @constructor
 * Contains utility functions for the JsUnit framework
 */
JsUnit.Util = {};

/**
 * Standardizes an HTML string by temporarily creating a DIV, setting its innerHTML to the string, and the asking for
 * the innerHTML back
 * @param html
 */
JsUnit.Util.standardizeHTML = function(html) {
    var translator = document.createElement("DIV");
    translator.innerHTML = html;
    return JsUnit.Util.trim(translator.innerHTML);
}

/**
 * Returns whether the given string is blank after being trimmed of whitespace
 * @param string
 */
JsUnit.Util.isBlank = function(string) {
    return JsUnit.Util.trim(string) == '';
}

/**
 * Implemented here because the JavaScript Array.push(anObject) and Array.pop() functions are not available in IE 5.0
 * @param anArray the array onto which to push
 * @param anObject the object to push onto the array
 */
JsUnit.Util.push = function(anArray, anObject) {
    anArray[anArray.length] = anObject;
}

/**
 * Implemented here because the JavaScript Array.push(anObject) and Array.pop() functions are not available in IE 5.0
 * @param anArray the array from which to pop
 */
JsUnit.Util.pop = function pop(anArray) {
    if (anArray.length >= 1) {
        delete anArray[anArray.length - 1];
        anArray.length--;
    }
}

/**
 * Returns the name of the given function, or 'anonymous' if it has no name
 * @param aFunction
 */
JsUnit.Util.getFunctionName = function(aFunction) {
    var regexpResult = aFunction.toString().match(/function(\s*)(\w*)/);
    if (regexpResult && regexpResult.length >= 2 && regexpResult[2]) {
            return regexpResult[2];
    }
    return 'anonymous';
}

/**
 * Returns the current stack trace
 */
JsUnit.Util.getStackTrace = function() {
    var result = '';

    if (typeof(arguments.caller) != 'undefined') { // IE, not ECMA
        for (var a = arguments.caller; a != null; a = a.caller) {
            result += '> ' + JsUnit.Util.getFunctionName(a.callee) + '\n';
            if (a.caller == a) {
                result += '*';
                break;
            }
        }
    }
    else { // Mozilla, not ECMA
        // fake an exception so we can get Mozilla's error stack
        try
        {
            foo.bar;
        }
        catch(exception)
        {
            var stack = JsUnit.Util.parseErrorStack(exception);
            for (var i = 1; i < stack.length; i++)
            {
                result += '> ' + stack[i] + '\n';
            }
        }
    }

    return result;
}

/**
 * Returns an array of stack trace elements from the given exception
 * @param exception
 */
JsUnit.Util.parseErrorStack = function(exception) {
    var stack = [];
    var name;

    if (!exception || !exception.stack) {
        return stack;
    }

    var stacklist = exception.stack.split('\n');

    for (var i = 0; i < stacklist.length - 1; i++) {
        var framedata = stacklist[i];

        name = framedata.match(/^(\w*)/)[1];
        if (!name) {
            name = 'anonymous';
        }

        stack[stack.length] = name;
    }
    // remove top level anonymous functions to match IE

    while (stack.length && stack[stack.length - 1] == 'anonymous') {
        stack.length = stack.length - 1;
    }
    return stack;
}

/**
 * Strips whitespace from either end of the given string
 * @param string
 */
JsUnit.Util.trim = function(string) {
    if (string == null)
        return null;

    var startingIndex = 0;
    var endingIndex = string.length - 1;

    var singleWhitespaceRegex = /\s/;
    while (string.substring(startingIndex, startingIndex + 1).match(singleWhitespaceRegex))
        startingIndex++;

    while (string.substring(endingIndex, endingIndex + 1).match(singleWhitespaceRegex))
        endingIndex--;

    if (endingIndex < startingIndex)
        return '';

    return string.substring(startingIndex, endingIndex + 1);
}

JsUnit.Util.getKeys = function(obj) {
    var keys = [];
    for (var key in obj) {
        JsUnit.Util.push(keys, key);
    }
    return keys;
}

JsUnit.Util.inherit = function(superclass, subclass) {
    var x = function() {};
    x.prototype = superclass.prototype;
    subclass.prototype = new x();
}
