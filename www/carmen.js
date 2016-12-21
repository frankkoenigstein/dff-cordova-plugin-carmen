
'use strict';

var cordova = require('cordova');
var feature = "CarmenPlugin";
var actions = ["startService"];

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

actions.forEach(function (action) {
    CarmenPlugin.prototype[action] = createActionFunction(action);
});

module.exports = new CarmenPlugin();
