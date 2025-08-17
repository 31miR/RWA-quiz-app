
import Quiz from "./classes/quiz.js";

// ===== State =====
let questions = []; // {id, text, points, seconds, answers:[{text, correct}]}
let editingId = null;
const urlParams = new URLSearchParams(window.location.search);
let quizId = urlParams.get('quizId'); //if this is not null, then this will be treated as an update, not create
let oldQuiz = null;

// ===== Elements =====
const quizForm = document.getElementById('quizForm');
const addQuestionBtn = document.getElementById('addQuestion');
const questionsList = document.getElementById('questionsList');
const cancelBtn = document.getElementById('cancelBtn');

// Image upload
const triggerUpload = document.getElementById('triggerUpload');
const fileInput = document.getElementById('quizImage');
const fileName = document.getElementById('fileName');
const imagePreview = document.getElementById('imagePreview');

// Modal
const modal = document.getElementById('questionModal');
const modalBackdrop = document.getElementById('modalBackdrop');
const closeModalBtn = document.getElementById('closeModal');
const modalForm = document.getElementById('modalForm');
const qText = document.getElementById('qText');
const qPoints = document.getElementById('qPoints');
const qSeconds = document.getElementById('qSeconds');

// Now we have to modify the page a bit if this is an update xd
if (quizId != null) {
    const pageTitle = document.getElementById("page-title");
    pageTitle.innerText = "Update Quiz";
    oldQuiz = new Quiz();
    oldQuiz.updateObjectFromBackend(quizId);
    imagePreview.style.backgroundImage = `/kviz/${oldQuiz.imageURI}`;
    fileName.innerText = "Do not change this if you wish to keep the old photo!";

    const title = document.getElementById('quizTitle');
    const description = document.getElementById('quizDescription');

    title.innerText = oldQuiz.title;
    description.innerText = oldQuiz.description;

    oldQuiz.questions.forEach(question => {
        questions.push({...question, editingId: question.id})
    });

    renderQuestions();
}

function openModal(prefill){
  if(prefill){
    qText.value = prefill.questionText || '';
    qPoints.value = prefill.pointAmmount ?? 0;
    qSeconds.value = prefill.timeInterval ?? 30;
    const rows = [...modal.querySelectorAll('.answer-row')];
    for(let i=0;i<4;i++){
      const a = prefill.answers?.[i] || {text:'', isRight:false};
      rows[i].querySelector('.aText').value = a.text || '';
      rows[i].querySelector('.aCorrect').checked = !!a.isRight;
    }
  } else {
    qText.value = '';
    qPoints.value = 0;
    qSeconds.value = 30;
    modal.querySelectorAll('.answer-row').forEach((row,idx)=>{
      row.querySelector('.aText').value = '';
      row.querySelector('.aCorrect').checked = false;
    });
  }
  modalBackdrop.hidden = false;
  modal.showModal();
}
function closeModal(){
  modal.close();
  modalBackdrop.hidden = true;
}
closeModalBtn.addEventListener('click', closeModal);
modalBackdrop.addEventListener('click', closeModal);

// Image upload behavior
triggerUpload.addEventListener('click', ()=> fileInput.click());
fileInput.addEventListener('change', ()=>{
  const file = fileInput.files?.[0];
  fileName.textContent = file? file.name : 'No file selected';
  if(file && file.type.startsWith('image/')){
    const reader = new FileReader();
    reader.onload = (e)=>{ imagePreview.style.backgroundImage = `url('${e.target.result}')`; };
    reader.readAsDataURL(file);
  } else {
    imagePreview.style.backgroundImage = '';
  }
});

// Add question -> opens empty modal
addQuestionBtn.addEventListener('click', ()=>{
  editingId = null;
  openModal(null);
});

// Save from modal
modalForm.addEventListener('submit', (e)=>{
  e.preventDefault();
  const answers = [...modal.querySelectorAll('.answer-row')].map(row=>({
    text: row.querySelector('.aText').value.trim(),
    isRight: row.querySelector('.aCorrect').checked
  }));
  const payload = {
    editingId: editingId ?? crypto.randomUUID(),
    questionText: qText.value.trim(),
    pointAmmount: parseInt(qPoints.value, 10) || 0,
    timeInterval: parseInt(qSeconds.value, 10) || 0,
    answers
  };
  if(!payload.questionText){ qText.focus(); return; }
  if(editingId){
    const idx = questions.findIndex(q=>q.editingId===editingId);
    if(idx>=0) questions[idx] = payload;
  } else {
    questions.push(payload);
  }
  renderQuestions();
  closeModal();
});

// Render list
function renderQuestions(){
  questionsList.innerHTML = '';
  questions.forEach((q, index)=>{
    const card = document.createElement('div');
    card.className = 'card';
    card.draggable = true;
    card.dataset.editingId = q.editingId;

    const handle = document.createElement('div');
    handle.className = 'handle';
    handle.textContent = '↕';

    const body = document.createElement('div');
    body.className = 'q-body';
    body.innerHTML = `
      <div class="meta">#${index+1} • ${q.pointAmmount} pts • ${q.timeInterval}s</div>
      <div class="text">${escapeHTML(q.questionText) || '<em>(no text)</em>'}</div>
      <div class="meta">Answers: ${q.answers.map((a,i)=>`${i+1}. ${escapeHTML(a.text)}${a.isRight?' ✅':''}`).join(' • ')}</div>
    `;

    body.addEventListener('click', ()=>{
      editingId = q.editingId;
      openModal(q);
    });

    const del = document.createElement('button');
    del.className = 'icon-btn danger';
    del.setAttribute('title','Delete question');
    del.innerHTML = '🗑';
    del.addEventListener('click', ()=>{
      questions = questions.filter(x=>x.editingId!==q.editingId);
      renderQuestions();
    });

    card.append(handle, body, del);
    attachDnd(card);
    questionsList.append(card);
  });
}

// Simple HTML5 Drag & Drop
let dragEl = null;
function attachDnd(el){
  el.addEventListener('dragstart', (e)=>{
    dragEl = el; el.classList.add('drag-ghost');
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/plain', el.dataset.editingId);
  });
  el.addEventListener('dragend', ()=>{
    el.classList.remove('drag-ghost'); dragEl = null;
  });
  el.addEventListener('dragover', (e)=>{
    e.preventDefault(); // allow drop
    const target = el;
    const bounding = target.getBoundingClientRect();
    const offset = e.clientY - bounding.top;
    const shouldInsertBefore = offset < bounding.height/2;
    if(dragEl && dragEl!==target){
      questionsList.insertBefore(dragEl, shouldInsertBefore ? target : target.nextSibling);
    }
  });
  el.addEventListener('drop', ()=>{
    // rebuild order from DOM
    const ids = [...questionsList.children].map(c=>c.dataset.editingId);
    questions.sort((a,b)=> ids.indexOf(a.editingId) - ids.indexOf(b.editingId));
    renderQuestions();
  });
}

function escapeHTML(str){
  return (str||'').replace(/[&<>"]/g, s=>({"&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;"}[s]));
}

// Cancel (acts like text link)
cancelBtn.addEventListener('click', ()=>{
  if(confirm('Discard all changes?')){
    // Reset form and state
    quizForm.reset();
    imagePreview.style.backgroundImage = '';
    questions = [];
    renderQuestions();
    window.location.href = "/kviz/admin/editorDashboard.html";
  }
});

// Form submit: collect everything and pass to a single function you implement
quizForm.addEventListener('submit', (e)=>{
  e.preventDefault();
  collectFormDataAndSubmitQuiz();
});

async function collectFormDataAndSubmitQuiz(){
  const title = document.getElementById('quizTitle').value.trim();
  const description = document.getElementById('quizDescription').value.trim();
  const image = fileInput.files?.[0] || null; // you can pack this into FormData

  const newQuiz = oldQuiz == null ? (new Quiz()) : oldQuiz;

  newQuiz.title = title;
  newQuiz.description = description;
  newQuiz.isImageSent = true;
  if (image == null || image.size == 0) {
    newQuiz.isImageSent = false;
  }
  if (!newQuiz.isImageSent && (oldQuiz == null)) {
    alert("You have to upload an image when creating a quiz!");
    return;
  }
  newQuiz.questions = questions;

  for (let i = 0; i < newQuiz.questions.len; ++i) {
    newQuiz.questions[i].position = i;
  }

  if (oldQuiz == null) {
    await newQuiz.sendToBackendForCreate(image);
  } else {
    await newQuiz.sendToBackendForUpdate(image);
  }
  window.location.href = "/kviz/admin/editorDashboard.html";
}

// Initial render
renderQuestions();
