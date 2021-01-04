function verifyGameExists() {

    fetch('/Xox/api/games/' + getGameId())
        .then(result => result.json())
        .then(json => {
            if (json.error) // assumes that the server is nice enough to send an error message
                throw Error(json.error);
            else
                // ARL-long poll on "board"-resource that causes re-rendering whenever the board changed.
                observeResource('/Xox/api/games/'+getGameId()+'/board?hash=', renderBoard, markOffline, "");
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

/**
 * Iterates over received board information and draws the x/o symbol to the grid.
 * This function is called by the resource observer.
 */
function renderBoard(board)
{
    console.log('Rendering board...');
}