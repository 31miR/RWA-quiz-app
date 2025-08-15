document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('quizForm');
    const addQuestionBtn = document.getElementById('addQuestionButton');
    const questionsContainer = document.getElementById('questionsContainer');
    const imageUpload = document.getElementById('imageUpload');
    const imagePreview = document.getElementById('imagePreview');
    const cancelBtn = document.getElementById('cancel-btn');

    let questionCount = 1;

    // Cancel dugme
    cancelBtn.addEventListener('click', () => {
        window.location.href = 'editorDashboard.html';
    });

    // Prikaz slike
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

    // Dodavanje pitanja
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
                ${[1,2,3,4].map(i => `
                    <div class="option-group">
                        <label>Option ${i}</label>
                        <input type="text" name="option${i}[]" class="option-input" ${i < 3 ? 'required' : ''}>
                    </div>
                `).join('')}
            </div>
            <div class="input-container">
                <label>Correct Answer</label>
                <div class="chips-container">
                    ${[1,2,3,4].map(i => `
                        <span class="mdl-chip correct-chip" data-value="option${i}">
                            <span class="mdl-chip__text">Option ${i}</span>
                        </span>
                    `).join('')}
                </div>
                <input type="hidden" name="correctAnswer[]" required>
            </div>
            <div class="input-container">
                <label>Time Length (in seconds): <span class="sliderValue">0</span>s</label>
                <input class="mdl-slider mdl-js-slider" type="range" name="timeLength[]" min="0" max="60" value="0" step="1" tabindex="0">
            </div>
        `;
        questionsContainer.appendChild(newSection);
        componentHandler.upgradeDom();
        initSlider(newSection);
        initChips(newSection);
    });

    // Slider za prvu sekciju
    const slider = document.querySelector('input[type="range"]');
    const sliderValue = document.querySelector('.sliderValue');
    slider.addEventListener('input', () => {
        sliderValue.textContent = slider.value;
    });

    // Chips za prvu sekciju
    const chips = document.querySelectorAll('.correct-chip');
    const hiddenInput = document.querySelector('input[type="hidden"][name="correctAnswer[]"]');
    chips.forEach(chip => {
        chip.addEventListener('click', () => {
            chips.forEach(c => c.classList.remove('selected'));
            chip.classList.add('selected');
            hiddenInput.value = chip.dataset.value;
        });
    });

    function initSlider(section) {
        const slider = section.querySelector('input[type="range"]');
        const valueSpan = section.querySelector('.sliderValue');
        slider.addEventListener('input', () => { valueSpan.textContent = slider.value; });
        valueSpan.textContent = slider.value;
    }

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

    // Validacija forme
    form.addEventListener('submit', function(e) {
        const title = form.querySelector('#quizTitle').value.trim();
        if (!title) {
            alert("Please enter the quiz title.");
            e.preventDefault();
            return;
        }

        const questions = form.querySelectorAll('.question-section');
        for (let i = 0; i < questions.length; i++) {
            const questionText = questions[i].querySelector('input[name="questionText[]"]').value.trim();
            if (!questionText) {
                alert(`Please enter text for question ${i+1}`);
                e.preventDefault();
                return;
            }

            const options = [1,2].map(n => questions[i].querySelector(`input[name="option${n}[]"]`).value.trim());
            if (options.some(opt => !opt)) {
                alert(`Please fill in required options (1 and 2) for question ${i+1}`);
                e.preventDefault();
                return;
            }

            const correct = questions[i].querySelector('input[type="hidden"][name="correctAnswer[]"]').value;
            if (!correct) {
                alert(`Please select a correct answer for question ${i+1}`);
                e.preventDefault();
                return;
            }
        }
    });
});

