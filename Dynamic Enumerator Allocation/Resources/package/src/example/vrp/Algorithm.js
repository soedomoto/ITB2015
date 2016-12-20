/**
 * Created by tnlam on 5/7/15.
 */

var util = require('util');
var assert = require('assert');
var defineClass = require('simple-cls').defineClass;
var Matrix = require('sylvester').Matrix;
var Vector = require('sylvester').Vector;

var VRPSolution = require('./Solution.js');

var TS = require('../../core/TS.js');

function neighbors (candidate) {
    assert(this.problem.valid(candidate));
    var self = this;
    var neighbors = [];
    var N = this.problem.dimension(); // number of city nodes
    var E = N; // number of travel paths
    // enumerate all possible pair of edges to remove
    var origin = candidate.data.elements.slice(0);
    console.log(origin);

    var unique_neighbors = [];

    assert(unique_neighbors.length > 0, "unique neighborhood set is empty!");
    return unique_neighbors;
}

function neighbor (candidate) {
    assert(this.problem.valid(candidate));
    var N = this.problem.dimension(); //number of city nodes
    var E = N; //number of travel paths
    var origin = candidate.data.elements.slice(0);
}