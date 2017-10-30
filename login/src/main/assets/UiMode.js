JavaScript:(function() {
    var parent = document.getElementsByTagName('head').item(0);
    var style = document.createElement('style');
    style.type = 'text/css';
    style.innerHTML = window.atob('%1$s');
    parent.appendChild(style)
  })();