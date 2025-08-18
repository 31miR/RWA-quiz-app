document.addEventListener('DOMContentLoaded', () => {
    const readyButton = document.getElementById('readyButton');
    const nameElement = document.getElementById('name-element');
    const loadingElement = document.getElementById('loading');
    const questionContainer = document.getElementById("question-container");
    const numberOfPlayers = document.getElementById("number-of-players");
    const questionText = document.getElementById("question-text");
    const timeLeftElement = document.getElementById('time-left');
    const nextQuestionButton = document.getElementById("next-question");
    const tableContainer = document.getElementById("table-container");
    const tableBody = tableContainer.querySelector('tbody');
    const waitingForPlayers = document.getElementById("loading-next-question");
    const checkboxes = document.querySelectorAll('.answer-section input[type="checkbox"]');

    let websocket;
    let countdownInterval;

    function connect() {
        websocket = new WebSocket("ws://localhost:8080/kviz/quiz");

        websocket.onopen = () => console.log("Connected to WebSocket.");

        websocket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            console.log("Received:", data);

            switch (data.type) {
                case "player_count":
                    numberOfPlayers.textContent = "Players: " + data.count;
                    break;

                case "new_question":
                    loadingElement.style.display = 'none';
                    questionContainer.style.display = 'block';
                    tableContainer.style.display = 'none';
                    updateQuestion(data.question);
                    startCountdown(data.question.timeInterval);
                    break;

                case "answerResult":
                    showAnswerResult(data);
                    break;

                case "question_ended":
                    questionContainer.style.display = 'none';
                    tableContainer.style.display = 'block';
                    updateTopPlayers(data.results);
                    waitingForPlayers.style.display = 'none';
                    break;

                case "quiz_ended":
                    alert("Kviz je završen!");
                    window.location.href = "/kviz/";
                    break;
            }
        };

        websocket.onclose = () => console.log("WebSocket closed.");
        websocket.onerror = (err) => console.error("WebSocket error:", err);
    }

    readyButton.addEventListener('click', () => {
        const nameInput = document.getElementById('nameInput').value.trim();
        if (!nameInput) return alert("Unesite ime!");

        sessionStorage.setItem('playerName', nameInput);
        const joinMsg = JSON.stringify({ type: "join", username: nameInput });
        if (websocket && websocket.readyState === WebSocket.OPEN) {
            websocket.send(joinMsg);
            nameElement.style.display = 'none';
            loadingElement.style.display = 'block';
        }
    });

    function updateQuestion(question) {
        checkboxes.forEach(cb => {
            cb.checked = false;
            cb.disabled = false;
            cb.classList.remove('correct', 'wrong', 'missed-answer');
        });

        nextQuestionButton.disabled = false;
        nextQuestionButton.style.cursor = 'pointer';
        waitingForPlayers.style.display = 'none';

        console.log(question);

        questionText.textContent = question.questionText;
        question.answers.forEach((opt, i) => {
            const checkbox = document.getElementById(`option${i + 1}`);
            const label = document.querySelector(`label[for="option${i + 1}"]`);
            checkbox.value = opt.id;
            label.textContent = opt.text;
        });
    }

    nextQuestionButton.addEventListener('click', () => {
        const selected = Array.from(checkboxes).filter(cb => cb.checked).map(cb => parseInt(cb.value));
        if (selected.length === 0) return alert("Odaberite barem jedan odgovor!");

        const answerMsg = { type: "answer",  answers: selected };
        websocket.send(JSON.stringify(answerMsg));

        nextQuestionButton.disabled = true;
        nextQuestionButton.style.cursor = 'not-allowed';
        waitingForPlayers.style.display = 'block';
    });

    function showAnswerResult(data) {
        checkboxes.forEach((cb, i) => {
            cb.disabled = true;
            if (cb.checked && data.answers.includes(+cb.value)) cb.classList.add('correct');
            else if (cb.checked && !data.answers.includes(+cb.value)) cb.classList.add('wrong');
            else if (!cb.checked && data.answers.includes(+cb.value)) cb.classList.add('missed-answer');
        });
    }

    function startCountdown(seconds) {
        clearInterval(countdownInterval);
        let timeLeft = seconds;
        timeLeftElement.textContent = `Time left: ${timeLeft}s`;
        countdownInterval = setInterval(() => {
            timeLeft--;
            timeLeftElement.textContent = `Time left: ${timeLeft}s`;
            if (timeLeft <= 0) clearInterval(countdownInterval);
        }, 1000);
    }

    function updateTopPlayers(players) {
        tableBody.innerHTML = '';
        const sorted = players.sort((a,b) => b.score - a.score);
        sorted.forEach((p,i) => {
            const row = document.createElement('tr');
            row.innerHTML = `<td>${i+1}</td><td>${p.playerName}</td><td>${p.score}</td>`;
            tableBody.appendChild(row);
        });
    }

    connect();
});