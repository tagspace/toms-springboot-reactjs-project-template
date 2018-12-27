import React, { Component } from "react";
import ReactDOM from "react-dom";
import base from '../css/base.css';

class AjaxLoader extends Component {
	render() {
		return (<div className={base.ajaxLoader}>
				<img src='/img/ajax-loader.gif'></img>
			</div>)
	}
}

export default AjaxLoader;