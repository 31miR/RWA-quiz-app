package com.example.kviz.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

import com.example.kviz.DTO.AnswerDTO;
import com.example.kviz.DTO.QuestionDTO;
import com.example.kviz.model.QuizPlayer;
import com.example.kviz.service.QuizCRUDService;
import com.example.kviz.service.QuizEventService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
        Map<String, Object> msg = parse(message);

        switch ((String) msg.get("type")) {
            case "join" -> {
                String username = (String) msg.get("username");
                usernames.put(session, username);
                broadcast("{\"type\":\"player_count\",\"count\":" + usernames.size() + "}");
            }

            case "admin_start" -> {
                adminSession = session;
                quizId = ((Number) msg.get("quizId")).longValue();
                currentQuestionIndex = -1;
                questions = quizCRUDService.getQuizById(quizId).questions;
                broadcast("{\"type\":\"quiz_started\"}");
            }

            case "admin_next_question" -> {
                currentQuestionIndex++;
                String questionJson = gson.toJson(questions.get(currentQuestionIndex));
                broadcast("{\"type\":\"new_question\",\"question\":" + questionJson + "}");

                int duration = questions.get(currentQuestionIndex).timeInterval + 2;
                startTimer(duration);
            }

            case "answer" -> {
                respondToAnswerAttempt(msg, session);

                if (allPlayersAnswered()) {
                    stopTimer();
                    sendTop10();
                }
            }
        }
    }

    private void respondToAnswerAttempt(Map<String, Object> msg, Session session) {
        String username = usernames.get(session);
        QuizPlayer player = quizEventService.getPlayerByPlayerName(username);
        boolean isRight = false;
        List<Long> attemptedAnswers = (List<Long>) msg.get("answers");

        Set<Long> attemptedAnswersSet = new HashSet<>(attemptedAnswers);
        Set<Long> actualAnswersSet = new HashSet<>();

        for (AnswerDTO a : questions.get(currentQuestionIndex).answers) {
            if (a.isRight) {
                actualAnswersSet.add(a.id);
            }
        }

        if (attemptedAnswersSet.equals(actualAnswersSet)) {
            isRight = true;
            quizEventService.updatePlayerScore(quizId, player.getScore() + questions.get(currentQuestionIndex).pointAmmount);
        }

        try {
            session.getBasicRemote().sendText("{type: 'answerResult', isRight: "
            +String.valueOf(isRight)
            +", answers: "
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
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void stopTimer() {
        if (runningTimer != null && !runningTimer.isCancelled()) {
            runningTimer.cancel(true);
        }
    }

    private void sendTop10() {
        String resultsJson = gson.toJson(quizEventService.getTopTenPlayers(quizId));
        broadcast("{\"type\":\"question_ended\",\"results\":" + resultsJson + "}");
    }

    private void broadcast(String msg) {
        for (Session s : sessions) {
            try { s.getBasicRemote().sendText(msg); }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void resetState() {
        adminSession = null;
        quizId = null;
        questions = null;
        stopTimer();
    }

    private Map<String, Object> parse(String json) {
        Type typeOfHashMap = new TypeToken<HashMap<String, Object>>(){}.getType();
        return gson.fromJson(json, typeOfHashMap);
    }

    private boolean allPlayersAnswered() {
        for (Map.Entry<Session, Boolean> entry : hasAnswered.entrySet()) {
            Boolean value = entry.getValue();
            if (value == false) {
                return false;
            }
        }
        return true;
    }
}
