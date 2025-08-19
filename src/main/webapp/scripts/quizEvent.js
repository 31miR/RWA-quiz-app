import { logoutAndRedirectToLanding } from "./util/backendHelperFuncs.js";

document.addEventListener('DOMContentLoaded', () => {
    
   
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => logoutAndRedirectToLanding());
    }

    
    const rowsPerPage = 10;
    let currentPage = 1;
    let allQuizzes= [];

    function fetchAndPopulateTable() {
        $.ajax({
            url: '/kviz/api/quizEventRaw?offset=0&limit=1000',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                allQuizzes = data;
                displayTablePage(currentPage);
            },
            error: function(xhr, status, error) {
                console.error('Error fetching data:', status, error);
                allQuizzes = [];
                displayTablePage(currentPage);
            }
        });
    }

    function displayTablePage(page) {
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        const tableBody = $('#quiz-table tbody');
        tableBody.empty();

        const QuizzesToShow = allQuizzes.slice(start, end);
        QuizzesToShow.forEach(quiz => {
            let row = $('<tr></tr>');
            row.append('<td>' + quiz.quizName + '</td>');
            row.append('<td>' + quiz.dateTimeCreated + '</td>');  
            row.append('<td>' + (quiz.eventActive ? "Yes" : "No") + '</td>'); 
            row.append('<td>' + (quiz.eventActive ? "" : `<a href = "/kviz/api/quizEventRaw?id=${quiz.id}&getXls=true"><button id="download-${quiz.id}">Download</button></a>`) + '</td>');  
            tableBody.append(row);
            
        });


        createPagination(allQuizzes.length);
    }

    function createPagination(totalRows) {
        const pageCount = Math.ceil(Math.max(totalRows, 1) / rowsPerPage);
        const paginationContainer = $('#pagination');
        paginationContainer.empty();

        for (let i = 1; i <= pageCount; i++) {
            const btn = $('<button></button>').text(i);
            btn.addClass('mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect');

            if (i === currentPage) btn.addClass('active');

            btn.click(() => {
                currentPage = i;
                displayTablePage(currentPage);
            });

            paginationContainer.append(btn);
        }

        componentHandler.upgradeDom();
    }

  

    fetchAndPopulateTable();
});

