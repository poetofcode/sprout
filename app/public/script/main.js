$(function() {

});

function htmlDecode(input) {
  var doc = new DOMParser().parseFromString(input, "text/html");
  return doc.documentElement.textContent;
}

function codeEscape(buffer) {
	const res = buffer
		.replaceAll('\n', `\\n`)
		.replaceAll('\t', `\\t`)
		.replaceAll(`\r`, `\\r`)
		.replaceAll(`"`, `\\"`);
	return res;
}

function showToast(msg, isError) {
	Toastify({
	  text: !isError ? 'Ok' : msg,
	  duration: 3000,
	  // destination: "https://github.com/apvarun/toastify-js",
	  newWindow: true,
	  close: true,
	  gravity: "bottom", // `top` or `bottom`
	  position: "center", // `left`, `center` or `right`
	  stopOnFocus: true, // Prevents dismissing of toast on hover
	  style: {
	    background: isError ? "#e2512e" : "#00b09b",
	  },
	  onClick: function(){} // Callback after click
	}).showToast();
}

// function getCookie(name) {
//   const value = `; ${document.cookie}`;
//   const parts = value.split(`; ${name}=`);
//   if (parts.length === 2) return parts.pop().split(';').shift();
// }

String.prototype.escapeSpecialChars = function() {
    return this.replace(/\\n/g, "\\n")
               .replace(/\\'/g, "\\'")
               .replace(/\\"/g, '\\"')
               .replace(/\\&/g, "\\&")
               .replace(/\\r/g, "\\r")
               .replace(/\\t/g, "\\t")
               .replace(/\\b/g, "\\b")
               .replace(/\\f/g, "\\f");
};


//
// SRC: https://stackoverflow.com/a/24103596
//
function setCookie(name,value,days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    console.log(`GET_COOKIE: ${document.cookie}`);
    // console.log(ca);
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function eraseCookie(name) {   
    document.cookie = name +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}