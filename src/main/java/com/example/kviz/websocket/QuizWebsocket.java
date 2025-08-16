package com.example.kviz.websocket;

import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

enum MessageType {
    START_NEW_QUESTION,
    GET_GAME_STATE,
    UPDATE_PLAYER_COUNT,
    TIMER_ENDED,
    GAME_STATE,
    SERVER_ERROR,
    SEND_ATTEMPTED_ANSWER,
    UPDATE_SCORE,
    ATTEMPTED_ANSWER_RESULT,
}

enum GameStateType {
    GAME_END,
    WAITING_ON_ADMIN,
    QUESTION_ACTIVE
}

class AbstractMessage {
    public String type;
}

class AbstractValue {
    public String type;
}

class AbstractMessageWithAbstractValue {
    public String type;
    public AbstractValue value;
}

@ServerEndpoint("/quizSocket")
public class QuizWebsocket {
    Gson gson = new Gson();

    public int counter = 0;
    Timer timer = new Timer();

    @OnOpen
    public void onOpen(Session session) {
        //TODO
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        //TODO
    }

    @OnClose
    public void onClose(Session session) {
        //TODO
    }

    private MessageType parseMessageType(String message) {
        AbstractMessage msg = gson.fromJson(message, AbstractMessage.class);
        switch (msg.type) {
            case "startNewQuestion":
                return MessageType.START_NEW_QUESTION;
            case "getGameState":
                return MessageType.GET_GAME_STATE;
            case "updatePlayerCount":
                return MessageType.UPDATE_PLAYER_COUNT;
            case "timerEnded":
                return MessageType.TIMER_ENDED;
            case "gameState":
                return MessageType.GAME_STATE;
            case "serverError":
                return MessageType.SERVER_ERROR;
            case "sendAttemptedAnswer":
                return MessageType.SEND_ATTEMPTED_ANSWER;
            case "updateScore":
                return MessageType.UPDATE_SCORE;
            case "attemptedAnswerResult":
                return MessageType.ATTEMPTED_ANSWER_RESULT;
        }
        return null;
    }
    private GameStateType parseGameStateInMessage(String message) {
        try {
            AbstractMessageWithAbstractValue msg = gson.fromJson(message, AbstractMessageWithAbstractValue.class);
            switch (msg.value.type) {
                case "GAME_END":
                    return GameStateType.GAME_END;
                case "WAITING_ON_ADMIN":
                    return GameStateType.WAITING_ON_ADMIN;
                case "QUESTION_ACTIVE":
                    return GameStateType.QUESTION_ACTIVE;
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }
}
