import { logoutAndRedirectToLanding, isLoggedInSuperAdmin } from "./util/backendHelperFuncs.js";
import { convertRawListToQuizList } from "./util/quizRawToQuizObject.js";

document.addEventListener('DOMContentLoaded', async () => {
    const isSuperadmin = await isLoggedInSuperAdmin();
    const headerCenter = document.querySelector('.header-center');

    // Helper funkcija za kreiranje dugmeta
    function createNavButton(label, target) {
        const form = document.createElement('form');
        form.action = target;
        form.method = 'GET';
        const button = document.createElement('button');
        button.type = 'submit';
        button.className = 'mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect';
        button.textContent = label;
        form.appendChild(button);
        return form;
    }

    // Home uvijek
    headerCenter.appendChild(createNavButton('HOME', '../landingPage.html'));

    // Quizzes uvijek
    headerCenter.appendChild(createNavButton('QUIZZES', 'editorDashboard.html'));

    // Users samo za superadmina
    if (isSuperadmin) {
        headerCenter.appendChild(createNavButton('USERS', '../superadmin/manageUsers.html'));
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
        logoutBtn.addEventListener('click', () => logoutAndRedirectToLanding());
    }

    // --- PAGINACIJA ---
    const rowsPerPage = 10;
    let currentPage = 1;
    const tableBody = document.getElementById('quiz-table')?.getElementsByTagName('tbody')[0];
    const paginationContainer = document.getElementById('pagination');
    let quizList = []

    if (!tableBody || !paginationContainer) return;

    function fetchAndPopulateTable() {
        $.ajax({
            url: '/kviz/api/admin/quiz?offset=0&limit=1000',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                console.log(data);
                quizList = convertRawListToQuizList(data);
                displayTablePage(currentPage);
            },
            error: function(xhr, status, error) {
                console.error('Error fetching data:', status, error);
                quizList = [];
                displayTablePage(currentPage);
            }
        });
    }

    function displayTablePage(page) {
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        const tableBody = $('#quiz-table tbody');
        tableBody.empty();

        const quizzesToShow = quizList.slice(start, end);
        quizzesToShow.forEach(quiz => {
            let row = $('<tr></tr>');
            row.append('<td>' + quiz.id + '</td>');
            row.append('<td>' + `<img src="/kviz/${quiz.imageURI}" style="max-width:100px"` + '</td>');
            row.append('<td>' + quiz.title + '</td>');
            row.append('<td><button class="action-button edit-button" data-id="' + quiz.id + '">Edit</button> <button class="action-button delete-button" data-id="' + quiz.id + '">Delete</button></td>');
            tableBody.append(row);
        });

        $('.delete-button').click(function() {
            let quizId = $(this).data('id');
            quizList.filter(quiz => quiz.id == quizId).forEach(quiz => quiz.deleteOnBackend());
            fetchAndPopulateTable();
        });
        
        /*
        $('.edit-button').click(function() {
            let userId = $(this).data('id');
            window.location.href = 'addNewUser.html?userId=' + userId;
        });
        
        */
        createPagination(quizList.length);
    }

    function createPagination(totalRows) {
        const pageCount = Math.max(1, Math.ceil(totalRows / rowsPerPage));
        paginationContainer.innerHTML = '';

        for (let i = 1; i <= pageCount; i++) {
            const btn = document.createElement('button');
            btn.textContent = i;
            btn.classList.add('mdl-button', 'mdl-js-button', 'mdl-button--raised', 'mdl-js-ripple-effect');

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

    fetchAndPopulateTable();
    displayTablePage(currentPage);
});

