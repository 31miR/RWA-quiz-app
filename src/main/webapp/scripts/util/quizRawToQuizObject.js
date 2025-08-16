import Quiz from "../classes/quiz.js";

export function convertRawToQuiz(raw) {
    const ret = new Quiz();
    ret.copyDataFromQuizRaw(raw);
    return ret;
}

export function convertRawListToQuizList(rawList) {
    return rawList.map(raw => {
        const quiz = new Quiz;
        quiz.copyDataFromQuizRaw(raw);
        return quiz;
    });
}