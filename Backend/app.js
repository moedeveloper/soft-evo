var express = require('express');
var app = express();
var port = process.env.PORT || 3000;

var http = require('http');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var details = require('./routes/details.js');
var builds = require('./routes/build.js');
var prints = require('./routes/print.js');
var buildParts = require('./routes/buildParts.js');
var companies = require('./routes/companies.js');


app.use('/api', details);
app.use('/api', builds);
app.use('/api', prints);
app.use('/api', buildParts);
app.use('/api', companies);

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(function(req, res, next){
	res.setHeader('Access-Control-Allow-Origin', '*');
	res.setHeader('Access-Control-Allow-Methods', 'GET, POST');
    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type, Authorization');
    next();
});
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());

// app.get('/', function(req, res){
//     res.send('wel using gulp');
// });

app.use(function(err, req, res, next) {
	if (err) {
		res.status(err.status || 400).send(
		{
			message: err.message 
		});
	}
	//res.header("Content-Type", "application/json; charset=utf-8");
	next();
});

var server = http.createServer(app);

server.listen(port, function(){
    console.log("Running on port: " + port);
}); 

module.exports = app;
