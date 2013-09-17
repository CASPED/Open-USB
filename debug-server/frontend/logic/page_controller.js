define(['can/view/ejs', 'can/control'],
    function(can) {
// Using ECMAScript 5 strict mode during development By default r.js will ignore that.
"use strict";


var PageController = can.Control(
{
    STATES: {
        VIEW: 0,
        SELECT: 1
    }

},{
    state: null,

    init: function( el ) {
        this.state = PageController.STATES.VIEW;
        this.element.html( can.view('graphics/page.ejs', {}) );
    },

    //DOM EVENTS
    //'#dom mousedown': function( el, ev ) {}
});

return PageController;

});