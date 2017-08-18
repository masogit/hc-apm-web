var path = require('path')
var assign = require('es6-object-assign').assign

var env = process.env.NODE_ENV === 'development' ?
           require('./dev') : require('./prod')

var base = {
  distDir: path.resolve(__dirname, '../build'),
  targetDir: path.resolve(__dirname, '../../../src/main/webapp/react'),
}

module.exports = assign(base, env)
