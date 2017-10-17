var express = require('express');
var details = express.Router();
var database = require('../database.js');



details.route('/details')
.get(function (req,res, next) {
   //res.send('details here ...'); 
   var promises = [];
   
       promises.push(database.getDetails().then(function (data) {
           return data;
       }));
    // database.getDetails().then(function (data) {
	// 	res.send({ birds: data });
	// }, next);   
    
       Promise.all(promises).then(function (values) {
           var json = JSON.stringify({
               tags: values[0],
               species: values[1]
           });
           res.end(json);
       });
});

module.exports = details;