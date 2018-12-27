import isoFetch from 'isomorphic-fetch'



export function get(path) {
	return isoFetch(path, {
        credentials: 'same-origin',/* force cookies to be sent */
        cache: 'no-store' /* needed for iOS to work */
    })
		.then(response => response.json());
}

export function post(path, payload = {}) {
	return fetch(path, {
        credentials: 'same-origin',/* force cookies to be sent */
        cache: 'no-store', /* needed for iOS to work */
        method: 'POST',
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            'x-csrf-token': csrfToken(),
            'x-requested-with': 'XMLHttpRequest'
        }
    })
    	.then(response => response.json());
}

export function csrfToken() {
	return document.head.querySelector('[name~=csrf-token]').content;
}

export function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email.trim())
}

export function mixpanel(event, payload = {}) {
    post('/home/log', {
        eventName: event,
        payload: payload
    })
}