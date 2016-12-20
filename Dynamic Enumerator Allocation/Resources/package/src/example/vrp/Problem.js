/**
 * Created by tnlam on 5/7/15.
 */
var util = require('util');
var assert = require('assert');
var defineClass = require('simple-cls').defineClass;
var Class = require('simple-cls').Class;
var Matrix = require('sylvester').Matrix;
var Vector = require('sylvester').Vector;
var xml2js = require('xml2js');
var fisher_yates_permute = require('../../util/permute.js');

var OProblem = require('../../core/OProblem.js');
var VRPSolution = require('./Solution.js');

var VRP = defineClass({
    name: 'VRP',
    extend: OProblem,

    construct: function (nodes, data) {
        if (data instanceof Array) {
            data = $M(data);
        }
        if (nodes instanceof Array) {
            nodes = $V(nodes);
        }
        OProblem.call(this, nodes, data, true);
    },

    methods: {
        valid: function (candidate) {
            assert(Class.isInstanceOfClass(candidate, 'VRPSolution'));

            var val = {}; // index from node index to occurance count
            for (var i = 1; i <= candidate.data.elements.length; i++) {
                var idx = candidate.data.e(i);
                if (!val[idx]) val[idx] = 1;
                else val[idx] += 1;
            }
            // once and only once
            var nodeCount = this.data.elements.length;
            for (var i = 1; i <= nodeCount; i++)
                if (val[i] != 1) return false;
            return true;
        },

        fitness: function (candidate) {
            //var total_dist = 0;
            //var pre = candidate.data.e(1), cur;
            //for (var i = 2; i <= candidate.data.elements.length; i++) {
            //    cur = candidate.data.e(i); //console.log("%d => %d (+%d)",pre, cur, this.instance.e(pre,cur));
            //    total_dist += this.data.e(pre, cur);
            //    pre = cur;
            //}
            //total_dist += this.data.e(cur, candidate.data.e(1)); //final stop to start
            //return total_dist;

            var total_dist = 0;
            var pre = candidate.data.e(1), cur;
            console.log(pre);
            for (var i = 2; i <= candidate.data.element.length; i++) {
                cur = candidate.data.e(1);
                total_dist += this.data.e(pre, cur);
                pre = cur;
            }
            total_dist += this.data.e(cur, candidate.data.e(1));
            return total_dist;
        },

        randsol: function () {
            //produce a random solution
            var sol = [];
            for (var i = 1; i <= this.dimension(); i++) sol.push(i);
            fisher_yates_permute(sol);
            var ret = new VRPSolution($V(sol));
            ret.fitness = this.fitness(ret);
            return ret;
        },

        initSolution: function (sol) {
            var ret = new VRPSolution($V($V(sol)));
            ret.fitness = this.fitness(ret);
        },

        dimension: function () {
            return this.nodes.length; // an array
        }
    },

    statics : {
        parseData: function (raw) {


            return $M(data);
        },

        validData: function (data) {


            return true;
        }

    }
});

module.exports = exports = VRP;