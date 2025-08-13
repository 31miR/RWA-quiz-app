document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('quizForm');
    const addQuestionBtn = document.getElementById('addQuestionButton');
    const questionsContainer = document.getElementById('questionsContainer');
    const imageUpload = document.getElementById('imageUpload');
    const imagePreview = document.getElementById('imagePreview');

    let questionCount = 1;

    // === Prikaz slike ===
    imageUpload.addEventListener('change', function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                imagePreview.innerHTML = `<img src="${e.target.result}" style="max-width: 200px;">`;
            };
            reader.readAsDataURL(file);
        }
    });

    // === Dodavanje novog pitanja ===
    addQuestionBtn.addEventListener('click', () => {
        questionCount++;

        const newSection = document.createElement('div');
        newSection.className = 'question-section';
        newSection.innerHTML = `
            <h3>Question ${questionCount}</h3>

            <div class="input-container">
                <label>Question Text</label>
                <input type="text" name="questionText[]" placeholder="Question text" required>
            </div>

            <div class="options-container">
                ${[1, 2, 3, 4].map(i => `
                    <div class="option-group">
                        <label>Option ${i}</label>
                        <input type="text" name="option${i}[]" class="option-input" ${i < 3 ? 'required' : ''}>
                    </div>
                `).join('')}
            </div>

            <div class="input-container">
                <label>Correct Answer</label>
                <div class="chips-container">
                    ${[1, 2, 3, 4].map(i => `
                        <span class="mdl-chip correct-chip" data-value="option${i}">
                            <span class="mdl-chip__text">Option ${i}</span>
                        </span>
                    `).join('')}
                </div>
                <input type="hidden" name="correctAnswer[]" required>
            </div>

            <div class="input-container">
                <label>Time Length (in seconds): <span class="sliderValue">0</span>s</label>
                <input class="mdl-slider mdl-js-slider"
                    type="range"
                    name="timeLength[]"
                    min="0" max="60" value="0" step="1" tabindex="0">
            </div>
        `;

        questionsContainer.appendChild(newSection);
        componentHandler.upgradeDom();

        initSlider(newSection);
        initChips(newSection);
    });

    // === Tvoj slider kod za prvu sekciju ===
    const slider = document.getElementById('timeLengthSlider');
    const sliderValue = document.getElementById('sliderValue');
    slider.addEventListener('input', () => {
        sliderValue.textContent = slider.value;
    });

    // === Tvoj chip kod za prvu sekciju ===
    const chips = document.querySelectorAll('.correct-chip');
    const hiddenInput = document.getElementById('correctAnswerHidden');

    chips.forEach(chip => {
        chip.addEventListener('click', () => {
            chips.forEach(c => c.classList.remove('selected'));
            chip.classList.add('selected');
            hiddenInput.value = chip.dataset.value;
        });
    });

    // === Funkcija za nove slidere ===
    function initSlider(section) {
        const slider = section.querySelector('input[type="range"]');
        const valueSpan = section.querySelector('.sliderValue');
        slider.addEventListener('input', () => {
            valueSpan.textContent = slider.value;
        });
        valueSpan.textContent = slider.value;
    }

    // === Funkcija za nove chipove ===
    function initChips(section) {
        const chips = section.querySelectorAll('.correct-chip');
        const hidden = section.querySelector('input[type="hidden"][name="correctAnswer[]"]');
        chips.forEach(chip => {
            chip.addEventListener('click', () => {
                chips.forEach(c => c.classList.remove('selected'));
                chip.classList.add('selected');
                hidden.value = chip.dataset.value;
            });
        });
    }

    // === Validacija forme ===
    form.addEventListener('submit', function (e) {
        const allHidden = form.querySelectorAll('input[name="correctAnswer[]"]');
        let valid = true;

        allHidden.forEach((input, index) => {
            if (!input.value) {
                alert(`Please select a correct answer for question ${index + 1}`);
                valid = false;
            }
        });

        if (!valid) {
            e.preventDefault();
        }
    });
});

