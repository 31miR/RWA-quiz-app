import { isLoggedIn, logoutAndRedirectToLanding, getQuizEventIdFromPin } from "./util/backendHelperFuncs.js";

document.addEventListener("DOMContentLoaded", async () => {
    const isLogedIn = await isLoggedIn();
    const loginFab = document.querySelector(".login-fab");
    loginFab.innerHTML = "";

    if (isLogedIn) {
        const logoutBtn = document.createElement("button");
        logoutBtn.className = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect";
        logoutBtn.textContent = "LOG OUT";
        logoutBtn.addEventListener("click", () => logoutAndRedirectToLanding()
        );

        const panelsBtn = document.createElement("button");
        panelsBtn.className = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect";
        panelsBtn.textContent = "PANELS";
        panelsBtn.addEventListener("click", () => {
            window.location.href = "admin/editorDashboard.html";
        });

        loginFab.appendChild(logoutBtn);
        loginFab.appendChild(panelsBtn);

    } else {
        const loginBtn = document.createElement("button");
        loginBtn.className = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect";
        loginBtn.textContent = "LOG IN";
        loginBtn.addEventListener("click", () => {
            window.location.href = "admin/login.html";
        });

        loginFab.appendChild(loginBtn);
    }

    const inputs = Array.from(document.querySelectorAll('.pin-box input'));
    const playBtn = document.getElementById('play-btn');

    inputs.forEach((input, index) => {
        input.addEventListener('input', (e) => {
            const value = input.value;
            input.value = value.replace(/[^0-9]/g, '');
            if (input.value.length === 1 && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
        });

        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && input.value === '' && index > 0) {
                inputs[index - 1].focus();
            }
        });
    });

    playBtn.addEventListener('click', async () => {
        const pin = inputs.map(i => i.value).join('');
        if (pin.length < inputs.length) {
            alert("Unesite cijeli pin!");
            return;
        }

        const {quizEventId} = await getQuizEventIdFromPin(pin);

        if (quizEventId == null) {
            alert("Nema kviza pod datim pinom!");
            return;
        }

        window.location.href = "/kviz/quizEventClient.html";
    });
});

