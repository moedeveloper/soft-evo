var express = require('express');

var app = express();
var port = process.env.PORT || 3000;
var router = require('./routes/details.js');

app.use('/api', router);



app.get('/', function(req, res){
    res.send('wel using gulp');
});

app.listen(port, function(){
    console.log("Running on port: " + port);
}); 


// details, prints, build, build parts.


//get details by details ID
// details by companyId
// details by original filename
// details by projectID

// build by Id

// print get print by Id
// by build Id return list of printIDs
// by machine & opeeratorname

