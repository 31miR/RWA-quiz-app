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

    socket = new WebSocket("ws://localhost:8080/kviz/quiz");

    socket.onopen = () => {
        console.log("WebSocket connected (admin)");

        const msg = {
            type: "admin_start",
            quizId: quizId
        };
        socket.send(JSON.stringify(msg));
    };

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log("Primljeno:", data);

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
                document.getElementById("timer").style.display = "none";
                document.getElementById("start-btn").style.display = "none";
                document.getElementById("leave-btn").style.display = "inline-block";
        }
    };

    socket.onclose = () => {
        console.log("WebSocket closed (admin)");
        document.getElementById("controls").style.display = "none";
        document.getElementById("timer").style.display = "none";
        document.getElementById("leave-btn").style.display = "inline-block";
    };

    document.getElementById("start-btn").addEventListener("click", () => {
        const msg = {
            type: "admin_next_question"
        };
        socket.send(JSON.stringify(msg));
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
        if (timeLeft <= 0) {
            clearInterval(timerInterval);
        }
    }, 1000);
}

function showTop10(players) {
    const list = document.getElementById("top10-list");
    list.style.display = "block";
    list.innerHTML = "";
    players.forEach(p => {
        const li = document.createElement("li");
        li.textContent = `${p.playerName} - ${p.score}`;
        list.appendChild(li);
    });

    document.getElementById("top10").style.display = "block";
    document.getElementById("start-btn").disabled = false;
}
