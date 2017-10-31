var express = require('express');
var buildPartsRouter = express.Router();
var database = require('../database.js');

buildPartsRouter.route('/buildParts')
.get(function (req,res, next) {
    var promises = [];
    promises.push(database.getBuildParts().then(function (data) {
        return data;
    }));
    Promise.all(promises).then(function (values) {
        var json = JSON.stringify({
            buildPartsApi: values[0]
        });
        res.end(json);
    });
 
});

buildPartsRouter.route('/buildpart/:id').get(function(req, res, next){
    var id = req.params.id
    database.getbuild(id).then(function(result){
        res.send(result)
    }, next);
});

module.exports = buildPartsRouter;