require([
    'socketio',
    'page_controller',
    'libs/amd/can/construct/super'
],
    function(io, PageController) {
// Using ECMAScript 5 strict mode during development By default r.js will ignore that.
"use strict";


var socket = io.connect();

socket.on('news', function (data) {
    console.log(data);
    socket.emit('my other event', { my: 'data' });
});


G = {
    pageController: new PageController( '#page' )
};

/*require([
    'scenes/scene_0',
    'scenes/scene_1'
]);*/


});