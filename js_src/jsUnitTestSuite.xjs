

/**
 * @class
 * A JsUnitTestSuite represents a suite of JsUnit Test Pages.  Test Pages and Test Suites can be added to a
 * JsUnitTestSuite
 * @constructor
 */
function JsUnitTestSuite() {
    /**
     * Declares that this object is a JsUnitTestSuite
     */
    this.isJsUnitTestSuite = true;
    /**
     * @private
     */
    this._testPages = Array();
    /**
     * @private
     */
    this._pageIndex = 0;

    for (var i = 0; i < arguments.length; i++) {
        if (arguments[i]._testPages) {
            this.addTestSuite(arguments[i]);
        } else {
            this.addTestPage(arguments[i]);
        }
    }
}

/**
 * Adds a Test Page to the suite
 * @param pageName the path to the Test Page
 */
JsUnitTestSuite.prototype.addTestPage = function (page) {
    this._testPages[this._testPages.length] = page;
}

/**
 * Adds a Test Suite to the suite
 * @param suite another JsUnitTestSuite object
 */

JsUnitTestSuite.prototype.addTestSuite = function (suite) {
    for (var i = 0; i < suite._testPages.length; i++)
        this.addTestPage(suite._testPages[i]);
}

/**
 * Whether the suite contains any Test Pages
 */
JsUnitTestSuite.prototype.containsTestPages = function () {
    return this._testPages.length > 0;
}

/**
 * Moves the suite on to its next Test Page
 */
JsUnitTestSuite.prototype.nextPage = function () {
    return this._testPages[this._pageIndex++];
}

/**
 * Whether the suite has more Test Pages
 */
JsUnitTestSuite.prototype.hasMorePages = function () {
    return this._pageIndex < this._testPages.length;
}

/**
 * Produces a copy of the suite
 */
JsUnitTestSuite.prototype.clone = function () {
    var clone = new JsUnitTestSuite();
    clone._testPages = this._testPages;
    return clone;
}

//For legacy support - JsUnitTestSuite used to be called jsUnitTestSuite
jsUnitTestSuite = JsUnitTestSuite;

function setJsUnitTracer(aJsUnitTracer) {
    top.tracer = aJsUnitTracer;
}

function jsUnitGetParm(name) {
    return top.params.get(name);
}

JsUnit._newOnLoadEvent = function() {
    isTestPageLoaded = true;
}

JsUnit._setOnLoad = function(windowRef, onloadHandler) {
    var isKonqueror = navigator.userAgent.indexOf('Konqueror/') != -1;

    if (typeof(windowRef.attachEvent) != 'undefined') {
        // Internet Explorer, Opera
        windowRef.attachEvent("onload", onloadHandler);
    } else if (typeof(windowRef.addEventListener) != 'undefined' && !isKonqueror) {
        // Mozilla
        // exclude Konqueror due to load issues
        windowRef.addEventListener("load", onloadHandler, false);
    } else if (typeof(windowRef.document.addEventListener) != 'undefined' && !isKonqueror) {
        // DOM 2 Events
        // exclude Mozilla, Konqueror due to load issues
        windowRef.document.addEventListener("load", onloadHandler, false);
    } else if (typeof(windowRef.onload) != 'undefined' && windowRef.onload) {
        windowRef.jsunit_original_onload = windowRef.onload;
        windowRef.onload = function() {
            windowRef.jsunit_original_onload();
            onloadHandler();
        };
    } else {
        // browsers that do not support windowRef.attachEvent or
        // windowRef.addEventListener will override a page's own onload event
        windowRef.onload = onloadHandler;
    }
}

JsUnit._setOnLoad(window, JsUnit._newOnLoadEvent);