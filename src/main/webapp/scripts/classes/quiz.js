import { deleteQuizById, createNewQuiz, updateExistingQuiz } from "../util/backendHelperFuncs.js";

export default class Quiz {
    id;
    title;
    description;
    imageURI;
    isImageSent;
    adminId;
    questions = [];

    copyDataFromQuizRaw(quizRaw) {
        this.id = quizRaw.id;
        this.title = quizRaw.title;
        this.description = quizRaw.description;
        this.imageURI = quizRaw.imageURI;
        this.adminId = quizRaw.adminId;
        this.questions = quizRaw.questions;
    }

    generateDataForBackend() {
        return {
            id: this.id,
            title: this.title,
            description: this.description,
            imageURI: this.imageURI,
            adminId: this.adminId,
            questions: this.questions
        }
    }

    deleteOnBackend() {
        deleteQuizById(this.id);
        this.id = null;
        this.title = null;
        this.description = null;
        this.imageURI = null;
        this.adminId = null;
        this.questions = [];
    }

    sendToBackendForUpdate(isImageUpdated) {
        this.isImageSent = isImageUpdated;
        updateExistingQuiz(this.generateDataForBackend());
    }

    sendToBackendForCreate() {
        this.isImageSent = true;
        createNewQuiz(this.generateDataForBackend());
    }
}