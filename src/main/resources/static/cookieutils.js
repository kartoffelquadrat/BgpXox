/**
 * Verifies whether the current session cookie contains username, acces-token and renewal-token
 */
function isLoginOk() {
    let username = readCookie('user-name');
    let access_token = readCookie('access-token');
    let refresh_token = readCookie('refresh-token');

    // evaluates to true if all strings properly set in cookie
    return username && access_token && refresh_token;
}

/**
 * Redirects all non-authenticated users to the login page.
 */
function anonymousIntercept() {
    if (!isLoginOk())
        window.location.replace("/");
}

function getUserName() {
    return readCookie('user-name');
}

function getAccessToken() {
    return readCookie('access-token');
}

function getRefreshToken() {
    return readCookie('refresh-token');
}

/**
 * Deletes the tokens from the cookie, keeps the username. Then reloads the page (forces redirect to login if protected page.).
 */
function logout() {
    document.cookie = "access-token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    document.cookie = "refresh-token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    location.reload();
}