var express = require('express');
var details = express.Router();
var database = require('../database.js');


details.route('/details')
.get(function (req,res, next) {
    var promises = [];
    promises.push(database.getDetails().then(function (data) {
        return data;
    }));
    Promise.all(promises).then(function (values) {
        var json = JSON.stringify({
            detailsApi: values[0]
        });
        res.end(json);
    });
 
});

details.route('/details/:id').get(function(req, res, next){
    var detailsId = req.params.id
    database.getDetailsById(detailsId).then(function(detail){
        res.send(detail)
    }, next);
});

details.route('/details/companyId/:id').get(function(req, res, next){
    var companyId = req.params.id
    database.getDetailsByCompanyId(companyId).then(function(detail){
        res.send(detail)
    }, next);
});

details.route('/details/originalFileName/:filename').get(function(req, res, next){
    var filename = req.params.filename
    database.getDetailsByOriginalFileName(filename).then(function(detail){
        res.send(detail)
    }, next);
});

details.route('/details/projectId/:projectId').get(function(req, res, next){
    var projectId = req.params.projectId
    database.getDetailsByProjectId(projectId).then(function(detail){
        res.send(detail)
    }, next);
});

module.exports = details;