var mysql = require('mysql');
Q = require('q');
var pool;
pool = mysql.createPool({
    connectionLimit: 10,
    host: 'localhost',
    user: 'root',
    database: 'mo3',
    password: 'yb10dj1v',
    //charset			: 'latin1_swedish_ci',
    charset: 'utf8',
    timezone: 'Europe/Stockholm'
});

function getDetails(){
    return getData('select * from details')
}

function getPrints(){
    return getData('select * from prints')
}
function getBuilds(){
    return getData('select * from builds')
}
function getBuildParts(){
    return getData('select * from buildparts')
}

function getCompanies(){
    return getData('select * from companies')
}

function getDetailsById(detailsId){
    const query = 'select * from details where id=?'
    return getDataByParameters(query, [detailsId])
}
function getDetailsByCompanyId(companyId){
    const query = 'select * from details where companyId=?'
    return getDataByParameters(query, [companyId])
}
function getDetailsByOriginalFileName(fileName){
    const query = 'select * from details where originaleFileName=?'
    return getDataByParameters(query, [fileName])
}
function getDetailsByProjectId(projectId){
    const query = 'select * from details where projectId=?'
    return getDataByParameters(query, [projectId])
}
function getBuildById(buildId){
    const query = 'select * from builds where id=?'
    return getDataByParameters(query,[buildId])
}

function getPrintById(printId){
    const query = 'select * from prints where id=?'
    return getDataByParameters(query,[printId])
}
function getPrintByBuildId(buildId){
    const query = 'select * from prints where buildsId=?'
    return getDataByParameters(query,[buildId])
}
function getPrintByMachine(machine){
    const query = 'select * from prints where machine=?'
    return getDataByParameters(query,[machine])
}
function getPrintByOperator(operator){
    const query = 'select * from prints where operator=?'
    return getDataByParameters(query,[operator])
}

function getBuildPartsById(id){
    const query = 'select * from buildparts where id=?'
    return getDataByParameters(query, [id])
}
function getCompanyById(id){
    const query = 'select * from companies where id=?'
    return getDataByParameters(query, [id])
}
function getData(sqlQuery){
	var deferred = Q.defer();
	pool.getConnection(function (err, connection) {
		if (err) {
			console.log(err);
			var dbError = new Error('No db connection');
			console.log(dbError);
		}
		else {
			console.log(sqlQuery);
			connection.query(sqlQuery, function (err, rows) {
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


function getDataByParameters(sqlQuery, arrayOfParameters){ 
	var deferred = Q.defer();
	pool.getConnection(function (err, connection) {
		if (err) {
			console.log(err);
			var dbError = new Error('No db connection');
			console.log(dbError);
		}
		else {
			connection.query(sqlQuery, arrayOfParameters, function (err, rows) {
				if (err) {
					deferred.reject(err);
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

exports.getDetails = getDetails
exports.getPrints = getPrints
exports.getBuilds = getBuilds
exports.getBuildParts = getBuildParts
exports.getCompanies = getCompanies
exports.getDetailsById = getDetailsById
exports.getDetailsByCompanyId = getDetailsByCompanyId
exports.getDetailsByOriginalFileName = getDetailsByOriginalFileName
exports.getDetailsByProjectId = getDetailsByProjectId
exports.getBuildById = getBuildById
exports.getPrintById = getPrintById
exports.getPrintByBuildId = getPrintByBuildId
exports.getPrintByMachine = getPrintByMachine
exports.getPrintByOperator = getPrintByOperator
exports.getBuildPartsById = getBuildPartsById
exports.getCompanyById = getCompanyById