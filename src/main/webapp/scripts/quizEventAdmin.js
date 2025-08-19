let socket;
let quizId;
let timerInterval;
let timeLeft = 0;

window.addEventListener("load", () => {
    const urlParams = new URLSearchParams(window.location.search);
    quizId = urlParams.get("quizId");

    if (!quizId) {
        alert("Nema quizId u URL-u!");
        return;
    }

    let loc = window.location;
    let protocol = (loc.protocol === "https:") ? "wss:" : "ws:";
    let socketUrl = protocol + "//" + loc.host + "/kviz/quiz";

    let socket = new WebSocket(socketUrl);

    socket.onopen = () => {
        const msg = { type: "admin_start", quizId };
        socket.send(JSON.stringify(msg));
    };

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);

        switch (data.type) {
            case "quiz_started":
                document.getElementById("quiz-pin").textContent = data.pin;
                break;
            case "player_count":
                document.getElementById("player-count").textContent = data.count;
                break;
            case "new_question":
                startTimer(data.question.timeInterval);
                document.getElementById("top10").style.display = "none";
                document.getElementById("start-btn").disabled = true;
                break;
            case "question_ended":
                showTop10(data.results);
                break;
            case "quiz_ended":
                // sakrij pin i broj igrača
                document.getElementById("quiz-info").style.display = "none";
                // sakrij timer i start dugme
                document.getElementById("timer").style.display = "none";
                document.getElementById("start-btn").style.display = "none";

                // prikaži top10 i button ispod tabele
                document.getElementById("top10").style.display = "block";
                document.getElementById("leave-btn").style.display = "block";
                document.getElementById("top10").appendChild(document.getElementById("leave-btn"));
                break;
        }
    };

    socket.onclose = () => {
        document.getElementById("controls").style.display = "none";
        document.getElementById("timer").style.display = "none";
        document.getElementById("leave-btn").style.display = "inline-block";
    };

    document.getElementById("start-btn").addEventListener("click", () => {
        socket.send(JSON.stringify({ type: "admin_next_question" }));
    });

    document.getElementById("leave-btn").addEventListener("click", () => {
        window.location.href = "/kviz/admin/editorDashboard.html";
    });
});

function startTimer(duration) {
    timeLeft = duration;
    document.getElementById("time-left").textContent = timeLeft;
    document.getElementById("timer").style.display = "block";
    clearInterval(timerInterval);
    timerInterval = setInterval(() => {
        timeLeft--;
        document.getElementById("time-left").textContent = timeLeft;
        if (timeLeft <= 0) clearInterval(timerInterval);
    }, 1000);
}

function showTop10(players) {
    const list = document.getElementById("top10-list");
    list.innerHTML = "";
    players.forEach((p, index) => {
        const li = document.createElement("li");
        li.style.setProperty('--i', index);
        li.textContent = `${p.playerName} - ${p.score}`;
        list.appendChild(li);
    });
    document.getElementById("top10").style.display = "block";
    document.getElementById("start-btn").disabled = false;
}
