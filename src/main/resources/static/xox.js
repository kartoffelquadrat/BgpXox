let actions;

function verifyGameExists() {

    fetch('/Xox/api/games/' + getGameId())
        .then(result => result.json())
        .then(json => {
            if (json.error) // assumes that the server is nice enough to send an error message
                throw Error(json.error);
            else
                // ARL-long poll on "board"-resource that causes re-rendering whenever the board changed.
                observeResource('/Xox/api/games/' + getGameId() + '/board?hash=', xoxBoardUpdate, markOffline, "");
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
 * Helper method to mark that connection to server is lost
 */
function markOffline() {
    alert('connection to server lost');
}

function xoxBoardUpdate(board) {
    // update board
    renderBoard(board);

    // retrieve updated set of actions
    actions = associateActions();

    // update status string
    updateStatusBar();
}

function updateStatusBar()
{
    let statusbar = document.getElementById('status-bar');
    statusbar.innerHTML= "THIS IS NEW!";

    // find out if the game is already finished
    // => access stats object: http://127.0.0.1:4244/Xox/api/games/12345
    // => gives something like: {"gameOver":false,"playersDescending":[{"name":"maex","preferredColour":"#CAFFEE"},{"name":"joerg","preferredColour":"#1C373A"}],"scoresDescending":[0,0]}
    fetch('/Xox/api/games/' + getGameId())
        .then(result => result.json())
        .then(json => {
            if (json.error) // assumes that the server is nice enough to send an error message
                throw Error(json.error);
            else
                console.log("Game stats are: " + json);
        })
        .catch(error => {
            // Seems like the game does not exist:
            alert('Error while getting stats: '+error);
        });

    // Game finished: Display winner

    // Game running: Display whos turn it is
}

/**
 * Retrieves a map of actions. Keys are the MD5 values of their serialized string representation
 * Assigns the action-hashes to the corresponding cells.
 */
function associateActions() {

    // unregister all actions registered so far. (for clicks on all cells)
    $(".game-cell").each(function () {
        $(this).off();
    });

    fetch('/Xox/api/games/' + getGameId() + '/players/' + 'maex' + '/actions?access_token=' + 'foo')
        .then(result => result.json())
        .then(json => {
            if (json.error) // assumes that the server is nice enough to send an error message
                throw Error(json.error);
            else {
                // associate new click handler to every referenced cell (entries in json that encodes action map)
                $.each(json, function (actionhash, action) {
                    bindActionHashToCellClick(action.y, action.x, action.player.name, actionhash);
                })
            }
        })
        .catch(error => {
            alert('Error: ' + error.toString());
        });
}

function bindActionHashToCellClick(x, y, player, hash) {
    // resolve action position to corresponding html element
    $('#' + x + y).click(function () {
        playAction(player, hash)
    });
}

/**
 * Sends a post request to the API to play a specific action
 * @param player as the player who plays the action
 * @param hash as the id of the action
 */
function playAction(player, hash) {

    // Send a post request to /Xox/api/games/{gameid}/players/{player}/actions/{action}
    // Add access_token as request parameter to authenticate
    fetch('/Xox/api/games/' + getGameId() + '/players/' + player + '/actions/' + hash + '?access_token=' + 'foo', {
        method: 'post'
    })
        .then(reply => {
            if (reply.status == 401)
                alert('Token invalid. Logout required');
            //logout(); // ToDo: First try to renew token, call logout() if that failed.
            if (reply.status != 200)
                console.log("Failed to start session. Server replied: " + reply.status);
        });
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