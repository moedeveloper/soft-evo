var express = require('express');
var companyRouter = express.Router();
var database = require('../database.js');

companyRouter.route('/companies')
.get(function (req,res, next) {
    var promises = [];
    promises.push(database.getCompanies().then(function (data) {
        return data;
    }));
    Promise.all(promises).then(function (values) {
        var json = JSON.stringify({
            companiesApi: values[0]
        });
        res.end(json);
    });
 
});

companyRouter.route('/companies/:name')
.post(function (req,res, next) {
    var promises = [];
    promises.push(database.saveNewCompany({name: req.params.name}));
});

companyRouter.route('/company/:id').get(function(req, res, next){
    var companyId = req.params.companyId
    database.getCompanyById(companyId).then(function(result){
        res.send(result)
    }, next);
});


module.exports = companyRouter;