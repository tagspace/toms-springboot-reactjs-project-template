import React, { Component } from "react";
import ReactDOM from "react-dom";
import AjaxLoader from "./components/AjaxLoader";


ReactDOM.render(<div>
		<h1>This is home.js</h1>
		<AjaxLoader />
	</div>, 
	document.getElementById("root"));