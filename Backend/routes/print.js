var express = require('express');
var printsRouter = express.Router();
var database = require('../database.js');

printsRouter.route('/prints')
.get(function (req,res, next) {
    var promises = [];
    promises.push(database.getPrints().then(function (data) {
        return data;
    }));
    Promise.all(promises).then(function (values) {
        var json = JSON.stringify({
            printsApi: values[0]
        });
        res.end(json);
    });
 
});

printsRouter.route('/print/:id').get(function(req, res, next){
    var id = req.params.id
    database.getPrintById(id).then(function(print){
        res.send(print)
    }, next);
});

printsRouter.route('/print/build/:id').get(function(req, res, next){
    var id = req.params.id
    database.getPrintByBuildId(id).then(function(result){
        res.send(result)
    }, next);
});
printsRouter.route('/print/machine/:id').get(function(req, res, next){
    var id = req.params.id
    database.getPrintByMachine(id).then(function(result){
        res.send(result)
    }, next);
});
printsRouter.route('/print/operator/:id').get(function(req, res, next){
    var operator = req.params.operator
    database.getPrintByOperator(operator).then(function(result){
        res.send(result)
    }, next);
});

module.exports = printsRouter;