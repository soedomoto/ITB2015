/**
 * Created by tnlam on 5/7/15.
 */

var util = require('util');
var assert = require('assert');
var defineClass = require('simple-cls').defineClass;
var Class = require('simple-cls').Class;
var sprintf = require("../../util/sprintf.js").sprintf;

var Matrix = require('sylvester').Matrix;
var Vector = require('sylvester').Vector;
var OSolution = require('../../core/OSolution.js');

var VRPSolution = defineClass({
    name: 'VRPSolution',
    extend: OSolution,
    construct: function() {
        OSolution.apply(this, arguments);
    },

    methods: {
        identical: function (sol) {
            assert.ok(Class.isInstanceOfClass(sol, 'VRPSolution'));

            if (sol.dimension() != this.dimension()) return false;
            for (var i = 0; i < this.dimension(); i++)
                if (sol.data.elements[i] != this.data.elements[i] ) return false;
            return true;
        },

        dimension: function () {
            return this.data.length;
        },

        toString: function () {
            var ret = [];
            for (var i = 0; i < this.data.length; i++) {
                ret.push(sprintf("[%s](%s)", this.data[i].elements, this.fitness));
            }
            return ret;
        }


    }
});

module.exports = exports = VRPSolution;