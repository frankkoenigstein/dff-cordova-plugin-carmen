
'use strict';

var cordova = require('cordova');
var feature = 'CarmenPlugin';
var actions = [
    "bindService",
    "unbindService",
    'startService',
    'setBackgroundScanPeriod',
    'setForegroundScanPeriod',
    'setRegionExitExpiration',
    'startMonitoring',
    'stopMonitoring',
    'startRanging',
    'stopRanging'
];

var eventActions = [
    "onServiceConnectionChange",
    'onEvent'
];

function CarmenPlugin () {};

function createActionFunction (action) {
    return function (args) {
        args = args || {};

        return new Promise(function (resolve, reject) {
            cordova.exec(function () {
                resolve.apply(this, arguments);
            }, function () {
                reject.apply(this, arguments);
            }, feature, action, [args]);
        });
    }
}

function createEventActionFunction (action) {
    return function (success, error, args) {
        args = args || {};

        cordova.exec(success, error, feature, action, [args]);
    }
}

actions.forEach(function (action) {
    CarmenPlugin.prototype[action] = createActionFunction(action);
});

eventActions.forEach(function (action) {
    CarmenPlugin.prototype[action] = createEventActionFunction(action);
});

module.exports = new CarmenPlugin();
