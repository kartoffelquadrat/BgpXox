function verifyGameExists() {

    fetch('/Xox/api/games/' + getGameId())
        .then(result => result.json())
        .then(json => {
            if (json.error) // assumes that the server is nice enough to send an error message
                throw Error(json.error);
            else
                // ARL-long poll on "board"-resource that causes re-rendering whenever the board changed.
                observeResource('/Xox/api/games/' + getGameId() + '/board?hash=', renderBoard, markOffline, "");
        })
        .catch(error => {
            // Seems like the game does not exist:
            document.getElementById('xox-container').remove();
            alert('No such game: ' + getGameId());
        });
}

/**
 * Extracts the game-ID from the URL path.
 */
function getGameId() {
    return window.location.pathname.split('/')[4];
}

/**
 * Helper method to mark that connectino to servier is lost
 */
function markOffline() {
    alert('connection to server lost');
}

function xoxBoardUpdate(board)
{
    // update board
    renderBoard(board);

    // update available handlers / visuals

    // update status string
}

/**
 * Iterates over received board information and draws the x/o symbol to the grid.
 * This function is called by the resource observer.
 */
function renderBoard(board) {
    console.log('Rendering board...');

    // for all cells, if content told by API is not whitespace: set corresponding class (x/o)
    if (/\S/.test(board.cells[0][0]))
        document.getElementById('00').classList.add(board.cells[0][0]);
    if (/\S/.test(board.cells[1][0]))
        document.getElementById('10').classList.add(board.cells[1][0]);
    if (/\S/.test(board.cells[2][0]))
        document.getElementById('20').classList.add(board.cells[2][0]);
    if (/\S/.test(board.cells[0][1]))
        document.getElementById('01').classList.add(board.cells[0][1]);
    if (/\S/.test(board.cells[1][1]))
        document.getElementById('11').classList.add(board.cells[1][1]);
    if (/\S/.test(board.cells[2][1]))
        document.getElementById('21').classList.add(board.cells[2][1]);
    if (/\S/.test(board.cells[0][2]))
        document.getElementById('02').classList.add(board.cells[0][2]);
    if (/\S/.test(board.cells[1][2]))
        document.getElementById('12').classList.add(board.cells[1][2]);
    if (/\S/.test(board.cells[2][2]))
        document.getElementById('22').classList.add(board.cells[2][2]);
}