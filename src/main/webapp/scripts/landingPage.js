import { isLoggedIn, logoutAndRedirectToLanding } from "./util/backendHelperFuncs.js";

document.addEventListener("DOMContentLoaded", async () => {
    const isLogedIn = await isLoggedIn();
    const loginFab = document.querySelector(".login-fab");
    loginFab.innerHTML = ""; // očisti dugmad

    if (isLogedIn) {
        // LOGOUT dugme
        const logoutBtn = document.createElement("button");
        logoutBtn.className = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect";
        logoutBtn.textContent = "LOG OUT";
        logoutBtn.addEventListener("click", () => logoutAndRedirectToLanding()
        );

        // PANELS dugme
        const panelsBtn = document.createElement("button");
        panelsBtn.className = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect";
        panelsBtn.textContent = "PANELS";
        panelsBtn.addEventListener("click", () => {
            window.location.href = "admin/editorDashboard.html";
        });

        loginFab.appendChild(logoutBtn);
        loginFab.appendChild(panelsBtn);

    } else {
        // LOG IN dugme
        const loginBtn = document.createElement("button");
        loginBtn.className = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect";
        loginBtn.textContent = "LOG IN";
        loginBtn.addEventListener("click", () => {
            window.location.href = "admin/login.html";
        });

        loginFab.appendChild(loginBtn);
    }
});

