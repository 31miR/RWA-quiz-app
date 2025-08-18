package com.example.kviz.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import com.example.kviz.DTO.AnswerDTO;
import com.example.kviz.DTO.GetQuizDTO;
import com.example.kviz.DTO.QuestionDTO;
import com.example.kviz.model.QuizEvent;
import com.example.kviz.model.QuizPlayer;
import com.example.kviz.service.QuizCRUDService;
import com.example.kviz.service.QuizEventService;
import com.google.gson.Gson;


class MessageFromClient {
    String type;
    Long quizId;
    String username;
    List<Long> answers;
}


@ServerEndpoint("/quiz")
public class QuizWebsocket {
    Gson gson = new Gson();
    private final QuizEventService quizEventService = new QuizEventService();
    private final QuizCRUDService quizCRUDService = new QuizCRUDService();

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    private static final Map<Session, String> usernames = new ConcurrentHashMap<>();

    private static final Map<Session, Boolean> hasAnswered = new ConcurrentHashMap<>();

    private static List<QuestionDTO> questions;
    private static int currentQuestionIndex;

    private static volatile Session adminSession;
    private static volatile Long quizId;
    private static volatile Long quizEventId;

    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> runningTimer;
    private static int timerSecondsRemaining;

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        MessageFromClient msg = parse(message);

        switch (msg.type) {
            case "join" -> {
                String username = msg.username;
                usernames.put(session, username);
                quizEventService.createPlayerForQuizEvent(username, quizEventService.getEventById(quizEventId));
                hasAnswered.put(session, false);
                broadcast("{\"type\":\"player_count\",\"count\":" + usernames.size() + "}");
                sendTop10();
                System.out.println("AJDE BRE VISE RADI KOJI K TI JE");
            }

            case "admin_start" -> {
                adminSession = session;
                quizId = msg.quizId;
                currentQuestionIndex = -1;
                GetQuizDTO quiz = quizCRUDService.getQuizById(quizId);
                questions = quiz.questions;
                QuizEvent event = quizEventService.createEventForGivenQuiz(quizCRUDService.getQuizEntityById(quizId));
                quizEventId = event.getId();
                broadcast("{\"type\":\"quiz_started\",\"pin\":\"" + event.getPin() + "\"}");
            }

            case "admin_next_question" -> {
                currentQuestionIndex++;
                for (Map.Entry<Session, Boolean> entry : hasAnswered.entrySet()) {
                    hasAnswered.put(entry.getKey(), false);
                }

                String questionJson = gson.toJson(questions.get(currentQuestionIndex));
                broadcast("{\"type\":\"new_question\",\"question\":" + questionJson + "}");
                
                int duration = questions.get(currentQuestionIndex).timeInterval + 2;
                startTimer(duration);
            }

            case "answer" -> {
                respondToAnswerAttempt(msg, session);

                //if (allPlayersAnswered()) {
                //    stopTimer();
                //    sendTop10();
                //}
            }
        }
    }

    private void respondToAnswerAttempt(MessageFromClient msg, Session session) {
        hasAnswered.put(session, true);
        String username = usernames.get(session);
        QuizPlayer player = quizEventService.getPlayerByPlayerName(username);
        boolean isRight = false;
        List<Long> attemptedAnswers = msg.answers;

        Set<Long> attemptedAnswersSet = new HashSet<>(attemptedAnswers);
        Set<Long> actualAnswersSet = new HashSet<>();

        for (AnswerDTO a : questions.get(currentQuestionIndex).answers) {
            if (a.isRight) {
                actualAnswersSet.add(a.id);
            }
        }

        if (attemptedAnswersSet.equals(actualAnswersSet)) {
            isRight = true;
            quizEventService.updatePlayerScore(player.getId(), player.getScore() + questions.get(currentQuestionIndex).pointAmmount);
        }

        try {
            session.getAsyncRemote().sendText("{\"type\": \"answerResult\", \"isRight\": "
            +String.valueOf(isRight)
            +", \"answers\": "
            +gson.toJson(actualAnswersSet)
            +"}");
        }
        catch (Exception e) {}
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        usernames.remove(session);
        if (session.equals(adminSession)) {
            quizEventService.switchEventActiveStatus(quizEventId);
            broadcast("{\"type\":\"quiz_ended\"}");
            resetState();
        } else {
            broadcast("{\"type\":\"player_count\",\"count\":" + usernames.size() + "}");
        }
    }

    private void startTimer(int seconds) {
        stopTimer();
        timerSecondsRemaining = seconds;
        runningTimer = scheduler.scheduleAtFixedRate(() -> {
            timerSecondsRemaining--;
            if (timerSecondsRemaining <= 0) {
                stopTimer();
                sendTop10();
                System.out.println(String.valueOf(currentQuestionIndex));
                System.out.println(String.valueOf(questions.size() - 1));
                if (currentQuestionIndex == questions.size()-1) {
                    try {
                        broadcast("{\"type\":\"quiz_ended\"}");
                    } catch (Exception e) {}
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void stopTimer() {
        if (runningTimer != null && !runningTimer.isCancelled()) {
            runningTimer.cancel(false);
        }
    }

    private void sendTop10() {
        String resultsJson = gson.toJson(quizEventService.getTopTenPlayers(quizEventId));
        broadcast("{\"type\":\"question_ended\",\"results\":" + resultsJson + "}");
    }

    private void broadcast(String msg) {
        for (Session s : sessions) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(msg);
            }
        }
    }

    private void resetState() {
        adminSession = null;
        quizId = null;
        quizEventId = null;
        questions = null;
        currentQuestionIndex = -1;
        usernames.clear();
        hasAnswered.clear();
        stopTimer();
    }

    private MessageFromClient parse(String json) {
        return gson.fromJson(json, MessageFromClient.class);
    }

    //private boolean allPlayersAnswered() {
    //    for (Map.Entry<Session, Boolean> entry : hasAnswered.entrySet()) {
    //        Boolean value = entry.getValue();
    //        if (value == false) {
    //            return false;
    //        }
    //    }
    //    return true;
    //}
}
