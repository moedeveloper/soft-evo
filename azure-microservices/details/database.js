const mysql = require('mysql2');

var config = 
{
    host: 'moedemo.database.windows.net',
    user: 'mo3',
    password: 'Yb10dj1v',
    database: 'demo',
    port: 3306,
    ssl: true
};

const conn = new mysql.createConnection(config);

conn.connect(
    function (err) { 
    if (err) { 
        console.log("!!! Cannot connect !!! Error:");
        throw err;
    }
    else
    {
       console.log("Connection established.");
           queryDatabase();
    }   
});

function queryDatabase(){
    conn.query('INSERT INTO Builds (Name, CompanyId, OriginalFileName, ProjectId, CreatedDate, Comment) VALUES (?,?,?,?,?,?);', ['orange', 2, 'filename',3, '2017-10-17', 'text here' ], 
            function (err, results, fields) {
                if (err) throw err;
            console.log('Inserted ' + results.affectedRows + ' row(s).');
        })
};

module.exports = function (context, req) {
    context.log('JavaScript HTTP trigger function processed a request.');

    if (req.query.name || (req.body && req.body.name)) {
        context.res = {
            // status: 200, /* Defaults to 200 */
            body: "Hello " + (req.query.name || req.body.name)
        };
    }
    else {
        context.res = {
            status: 400,
            body: "Please pass a name on the query string or in the request body"
        };
    }
    context.done();
};