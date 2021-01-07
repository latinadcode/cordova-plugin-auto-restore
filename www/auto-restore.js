var exec = require('cordova/exec');

var PLUGIN_NAME = 'AutoRestore';

exports.enable = function () {
  exec(null, null, PLUGIN_NAME, 'enable', []);
};

exports.echo = function (phrase) {
  exec(null, null, PLUGIN_NAME, 'echo', [phrase]);
};