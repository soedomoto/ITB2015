var util = require('util');
var assert = require('assert');
var defineClass = require('simple-cls').defineClass;

var OSolution = require('./OSolution.js');

var OProblem = defineClass({
	name : "OProblem",
	/**
	 initialize with problem instance data
	**/
	construct: function (nodes, data, minimization) {
		this.nodes = nodes;
		this.data = data;	
		this.minimization = minimization;
	},
	
	variables: {
		// node list
		nodes: null,

		// cost matrix
		data: null,

		// minimization
		minimization: false
	},
	
	statics: {
		/**
		consumes a raw content and parse the problem instance data 
		subclass will implement this parsing function and return a data object that can be used by the problem instance
		@return data - the problem instance data 
		**/
		parseData : function(raw){assert.ok(null,"not implemented"); },
		
		/**
		 check if a given data object is valid 
		**/
		validData : function(data){assert.ok(null,"not implemented");}
	},
	
	methods : {
		
		/**
		 check if a given solution to the problem satisfies all constraints 
		**/
		valid : function(solution){assert.ok(null,"not implemented"); }, 
		
		/**
		 definition of the objective function
		 @return the solution quality value for the solution
		**/
		fitness : function(solution){assert.ok(null,"not implemented");}, 
		
		/**
		 utility function for generating a random solution 
		 @return a solution instance
		**/
		randsol : function () {
			assert.ok(null,"not implemented");
		}
	}
});

module.exports = exports = OProblem;