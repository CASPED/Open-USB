define([
    'can/view/ejs',
    'can/control',
    'can/observe',
    'can/util/library'
], function(can) {
// Using ECMAScript 5 strict mode during development By default r.js will ignore that.
"use strict";


var Visor = can.Control(
{ },{

    init: function(el, options) {},

    start: function() {
        this.element.html( can.view('graphics/visor.ejs', {}) );

        this.dom = {
            visor: this.element
        };

        G.socket.on('draw', $.proxy(this.onTrace, this));

        //this.element.addClass("fullscreen");
    },

    onTrace: function(data) {
        console.log("Drawing: ", data);

        var self = this;
        $.each(data, function(figure) {
            self.dom.out_text.append(
                can.view('graphics/drawing.ejs', {"shapes": data[figure], "figure": figure})
            );
        });
        
    },

    //DOM EVENTS
    //'input keydown': function( el, ev ) {
    //},

    //Private
    dom: null
});

return Visor;

});