document.addEventListener("DOMContentLoaded", function () {
    const inputs = document.querySelectorAll("form.otc input");
    const playBtn = document.getElementById("play-btn");

    // Fokus na prvo polje pri učitavanju
    if (inputs.length > 0) inputs[0].focus();

    // Automatski prelazak između input polja
    inputs.forEach((input, index) => {
        input.setAttribute("maxlength", 1);

        input.addEventListener("input", () => {
            const value = input.value;

            // Ako je unesena cifra i nije zadnje polje
            if (value && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
        });

        // Navigacija sa strelicama
        input.addEventListener("keydown", (e) => {
            if (e.key === "Backspace" && input.value === "" && index > 0) {
                inputs[index - 1].focus();
            }
            if (e.key === "ArrowLeft" && index > 0) {
                inputs[index - 1].focus();
            }
            if (e.key === "ArrowRight" && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
            if (e.key === "Enter") {
                playBtn.click();
            }
        });
    });

    // Klik na PLAY dugme
    playBtn.addEventListener("click", function (e) {
        e.preventDefault();

        // Spajanje unosa u PIN
        const pin = Array.from(inputs).map(input => input.value).join("");

        if (pin.length !== 6 || !/^\d{6}$/.test(pin)) {
            alert("Please enter all 6 digits of the PIN.");
            inputs[0].focus();
            return;
        }

        console.log("Entered PIN:", pin);

        // Ako želiš redirekciju sa PIN-om
        // window.location.href = `/startQuiz?pin=${pin}`;

        // Ili AJAX poziv (npr.)
        // $.post('/validatePin', { pin }, function(response) {
        //     if (response.valid) {
        //         window.location.href = `/quiz/${response.quizId}`;
        //     } else {
        //         alert("Invalid PIN");
        //     }
        // });
    });
});

