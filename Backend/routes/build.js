var express = require('express');
var buildsRouter = express.Router();
var database = require('../database.js');

buildsRouter.route('/builds')
.get(function (req,res, next) {
    var promises = [];
    promises.push(database.getBuilds().then(function (data) {
        return data;
    }));
    Promise.all(promises).then(function (values) {
        var json = JSON.stringify({
            buildsApi: values[0]
        });
        res.end(json);
    });
 
});

buildsRouter.route('/build/id/:id').get(function(req, res, next){
    var id = req.params.id
    database.getBuildById(id).then(function(build){
        res.send(build)
    }, next);
});

module.exports = buildsRouter;