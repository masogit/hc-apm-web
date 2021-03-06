const HtmlWebpackPlugin = require('html-webpack-plugin'); //installed via npm

module.exports = {
  context: __dirname,
  entry: {
    js: "./src/js/index.js"
  },

  output: {
    path: __dirname + "/dist",
    filename: "index.js"
  },

  devServer: {
    // contentBase: path.join(__dirname, "dist"),
    compress: true,
    port: 9001,
    hot: true,
    host: "0.0.0.0"
  },

  module: {
    // preLoaders: [
    //     //Eslint loader
    //   { test: /\.js$/, exclude: /node_modules/, loader: "eslint-loader"}
    // ],
    rules: [
      // { test: /\.html$/, use: '' },
      { test: /\.(css|scss)$/, use: ['style-loader', 'css-loader', 'sass-loader'] },
      { test: /\.(js|jsx)$/, use: 'babel-loader', exclude: [/node_modules/] },
      { test: /\.json$/, use: 'json-loader' }
    ]
  },

  plugins: [
    new HtmlWebpackPlugin({template: './src/index.html'})
  ]
};
