module.exports = {
  entry: {
    //list of entry points ("As a rule of thumb: for each HTML document use exactly one entry point.")
    home_bundle: './src/main/javascript/home.js'//the entry point (only files reachable from here will be bundled into home.js)
  },
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
    publicPath: '/js'//used by webpack-dev-server to intercept requests for /js/bundle.js
  },
  devtool: '#source-map'
};