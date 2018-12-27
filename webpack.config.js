module.exports = {
  entry: [
    './src/main/javascript/index.js'//the root file (only files reachable from here will be bundled into bundle.js)
  ],
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader"
        }
      },
      {
        test: /\.css$/,
        loader: 'style-loader'
      }, {
        test: /\.css$/,
        loader: 'css-loader',
        query: {
          modules: true,
          localIdentName: '[name]__[local]___[hash:base64:5]'
        }
      }
    ]
  },
  output: {
    path: __dirname + '/src/main/resources/static/js',
    publicPath: '/js',//used by webpack-dev-server to intercept requests for /js/bundle.js
    filename: 'bundle.js',//the resulting javascript, all bundled into one file
    sourceMapFilename: './bundle.map'
  },
  devtool: '#source-map'
};