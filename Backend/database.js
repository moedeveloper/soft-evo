var mysql = require('mysql'),
Q = require('q'),
bcrypt = require('bcryptjs'),
uuid = require('node-uuid'),
pool,
dbPassword = '@MO3mott0yb10dj1v'; //process.argv[2];

pool = mysql.createPool({
    connectionLimit: 10,
    host: 'moemortada.com.mysql',
    user: 'moemortada_com',
    database: 'moemortada_com',
    password: dbPassword,
    //charset			: 'latin1_swedish_ci',
    charset: 'utf8',
    timezone: 'Europe/Stockholm'
});
pool.getConnection(function(err, connection) {
    if(err){
        console.log('Error while connecting to db.. '+ err.message);
    }
});

// if (dbPassword) {
    // code for conenctio here
// } else {
//     pool = mysql.createPool({
//         connectionLimit: 10,
//         host: 'moemortada.com.mysql',
//         user: 'moemortada_com',
//         database: 'moemortada_com'
//     });
// }

function getDetails(){
    var defferred = Q.defer();

    pool.getConnection(function(err, connection){
        if(err){
            console.log(err);
            var dbError = new Error('No db connection');
            console.log(dbError);
        }
        else {
            //console.log(sqlQuery);
            var queryString = 'SELECT * FROM details';
			connection.query(queryString,function (err, rows) {
				if (err) {
					console.log(err);
					deferred.reject(err);
				}
				else if (rows.length === 0) {
					var countError = new Error('no rows');
					deferred.reject(countError);
				}
				else {
					deferred.resolve(rows);
				}
				connection.release();
			});
		}
    });
    return deferred.promise;    
}

exports.getDetails= getDetails;