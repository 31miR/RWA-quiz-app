document.addEventListener('DOMContentLoaded', () => {
    const rowsPerPage = 10;
    let currentPage = 1;
    const tableBody = document.getElementById('quiz-table').getElementsByTagName('tbody')[0];
    const paginationContainer = document.getElementById('pagination');

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
        const pageCount = Math.max(1, Math.ceil(totalRows / rowsPerPage)); // uvijek bar 1 stranica
        paginationContainer.innerHTML = '';

        for (let i = 1; i <= pageCount; i++) {
            const btn = document.createElement('button');
            btn.textContent = i;

            // Stil kao ostali MDL buttoni
            btn.classList.add('mdl-button', 'mdl-js-button', 'mdl-button--raised', 'mdl-js-ripple-effect');

            if (i === currentPage) {
                btn.style.background = 'linear-gradient(to bottom, #ffd6e8, #fff0f5)';
            }

            btn.addEventListener('click', () => {
                currentPage = i;
                displayTablePage(currentPage);
            });

            paginationContainer.appendChild(btn);
        }
    }

    // Inicijalno
    displayTablePage(currentPage);
});

