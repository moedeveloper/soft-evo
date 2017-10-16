var express = require('express');
var detailsRouter = express.Router();


detailsRouter.route('/single')
.get(function (req,res) {
   res.send('details here ...'); 
});

module.exports = detailsRouter;