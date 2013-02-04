var board = null;

function createBoard(width, height) {
    /** Creates a new empty board. */
    board = {
        width: width,
        height: height,
        cells: ""
    };

    for (var i=0; i<width * height; i++) {
        board.cells += "0"
    }

    return board;
}

function replaceAt(string, index, character) {
    res = string.substring(0, index) + character + string.substring(index+1);
    return res
}

function setCoordinate(x, y, value) {
    pos = y * board.width + x;
    board.cells = replaceAt(board.cells, pos, value);
}

function rPentomino(xoffset, yoffset) {
    /** A simple structure that quickly becomes complicated. */

    setCoordinate(xoffset + 1, yoffset - 1, '1');
    setCoordinate(xoffset, yoffset - 1, '1');
    setCoordinate(xoffset, yoffset, '1');
    setCoordinate(xoffset, yoffset + 1, '1');
    setCoordinate(xoffset - 1, yoffset, '1');
}

function square(xoffset, yoffset) {
    /** A simple, static structure. Useful for debugging. */

    setCoordinate(xoffset, yoffset, '1');
    setCoordinate(xoffset, yoffset + 1, '1');
    setCoordinate(xoffset + 1, yoffset, '1');
    setCoordinate(xoffset + 1, yoffset + 1, '1');
}

function wait_and_iterate() {
    window.setTimeout(iterate, 100);
}

function isAlive(x, y) {
    return board.cells[y * board.width + x] == '1';
}

function chooseClass(x, y) {
    if (isAlive(x, y)) {
        return "alive";
    } else {
        return "dead";
    }
}

function draw() {
    var table = $("#wrapper");
    table.empty();
    for (var y = 0; y < board.height; y++) {
        var row = $('<tr></tr>');
        for (var x = 0; x < board.width; x++) {
            row.append($('<td></td>').addClass(chooseClass(x, y)));
        }
        table.append(row);
    }
}

function iterate() {
    /** Fetch the next iteration from the service and draw it. */

    function callback(response) {
        board = response;
        draw();
        wait_and_iterate();
    }
    $.post("/board", board, callback);
}

$(document).ready(function() {
    board = createBoard(180, 90);
    rPentomino(board.width / 2, board.height / 2);
    draw();
    wait_and_iterate();
});
