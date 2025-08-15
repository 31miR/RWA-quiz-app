document.addEventListener('DOMContentLoaded', () => {

    // --- USERS dugme za superadmin ---
    const role = document.body.getAttribute('data-role');
    if (role === 'superadmin') {
        const headerCenter = document.querySelector('.header-center');
        const form = document.createElement('form');
        form.action = 'manageUsers.html';
        form.method = 'GET';
        const button = document.createElement('button');
        button.type = 'submit';
        button.className = 'mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect';
        button.textContent = 'USERS';
        form.appendChild(button);
        headerCenter.appendChild(form);
    }

    // --- Add New Quiz dugme ---
    const addQuizBtn = document.getElementById('add-new-quiz-btn');
    if (addQuizBtn) {
        addQuizBtn.addEventListener('click', () => {
            window.location.href = 'addNewQuiz.html';
        });
    }

    // --- Logout dugme ---
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            window.location.href = 'login.html';
        });
    }

    // --- PAGINACIJA ---
    const rowsPerPage = 10;
    let currentPage = 1;
    const tableBody = document.getElementById('quiz-table')?.getElementsByTagName('tbody')[0];
    const paginationContainer = document.getElementById('pagination');

    if (!tableBody || !paginationContainer) return;

    function displayTablePage(page) {
        const rows = Array.from(tableBody.getElementsByTagName('tr'));
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;

        rows.forEach((row, index) => {
            row.style.display = (index >= start && index < end) ? '' : 'none';
        });

        createPagination(rows.length);
    }

    function createPagination(totalRows) {
        const pageCount = Math.max(1, Math.ceil(totalRows / rowsPerPage));
        paginationContainer.innerHTML = '';

        for (let i = 1; i <= pageCount; i++) {
            const btn = document.createElement('button');
            btn.textContent = i;
            btn.classList.add('mdl-button', 'mdl-js-button', 'mdl-button--raised', 'mdl-js-ripple-effect');

            // Trenutna stranica
            if (i === currentPage) {
                btn.style.background = 'linear-gradient(to bottom, #ffd6e8, #fff0f5)';
            }

            btn.addEventListener('click', () => {
                currentPage = i;
                displayTablePage(currentPage);
            });

            btn.addEventListener('mouseover', () => {
                btn.style.background = 'linear-gradient(to bottom, #fff0f5, #ffd6e8)';
            });
            btn.addEventListener('mouseout', () => {
                btn.style.background = (i === currentPage)
                    ? 'linear-gradient(to bottom, #ffd6e8, #fff0f5)'
                    : '';
            });

            paginationContainer.appendChild(btn);
        }
    }

    displayTablePage(currentPage);
});

