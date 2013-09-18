require([
    'socketio',
    'page_controller',
    'libs/amd/can/construct/super'
],
    function(io, PageController) {
// Using ECMAScript 5 strict mode during development By default r.js will ignore that.
"use strict";

G = {
    socket: io.connect(),
    pageController: new PageController( '#page' )
};

G.pageController.start();

/*require([
    'scenes/scene_0',
    'scenes/scene_1'
]);*/


});