window.cyto = function (selector, elements) {
    AJS.$(selector).cytoscape({
        layout: {
            name: 'breadthfirst',
            padding: 30,
            spacingFactor: 1,
            maximalAdjustments: 3,
            directed: true
        },

        style: cytoscape.stylesheet()
            .selector("[nodeType = 'task']")
            .css({
                'shape': 'rectangle',
                'width': '200',
                'content': 'data(label)',
                'text-valign': 'center',
                'text-outline-width': 2,
                'text-outline-color': '#6FB1FC',
                'background-color': '#6FB1FC',
                'color': '#fff'
            })
            .selector("[nodeType = 'event']")
            .css({
                'shape': 'circle',
                'width': '50',
                'content': 'data(label)',
                'text-valign': 'center',
                'text-outline-width': 2,
                'text-outline-color': '#333',
                'background-color': '#999',
                'color': '#fff'
            })
            .selector(':selected')
            .css({
                'border-width': 3,
                'border-color': '#333'
            })
            .selector('edge')
            .css({
                'curve-style': 'bezier',
                'opacity': 0.666,
                'width': '3',
                'target-arrow-shape': 'triangle',
                'line-color': '#333',
                'source-arrow-color': '#6FB1FC',
                'target-arrow-color': '#6FB1FC'
            })
            .selector('edge.questionable')
            .css({
                'line-style': 'dotted',
                'target-arrow-shape': 'diamond'
            })
            .selector('.faded')
            .css({
                'opacity': 0.25,
                'text-opacity': 0
            }),

        elements: elements,

        ready: function () {


            // giddy up
        }
    });
};
