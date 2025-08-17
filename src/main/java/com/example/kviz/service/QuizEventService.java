package com.example.kviz.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

import com.example.kviz.model.Quiz;
import com.example.kviz.model.QuizEvent;
import com.example.kviz.model.QuizPlayer;
import com.example.kviz.repository.QuizEventRepository;
import com.example.kviz.repository.QuizPlayerRepository;

public class QuizEventService {
    private final QuizEventRepository quizEventRepository = new QuizEventRepository();
    private final QuizPlayerRepository quizPlayerRepository = new QuizPlayerRepository();

    public void updatePlayerScore(Long id, int newScore) {
        quizPlayerRepository.updatePlayerScore(id, newScore);
    }

    public QuizPlayer getPlayerById(Long id) {
        return quizPlayerRepository.getPlayerById(id);
    }

    public QuizPlayer getPlayerByPlayerName(String playerName) {
        return quizPlayerRepository.findPlayerByPlayerName(playerName);
    }

    public int getPlayerRank(Long id) {
        QuizPlayer player = getPlayerById(id);
        QuizEvent event = player.getQuizEvent();
        List<QuizPlayer> eventPlayers = quizPlayerRepository.getAllPlayersForQuizEvent(event.getId());
        int rank = 1;
        for (QuizPlayer possiblePlayer : eventPlayers) {
            if (possiblePlayer.getId().equals(player.getId())) {
                return rank;
            }
            rank++;
        }
        return -1;
    }

    public List<QuizPlayer> getTopTenPlayers(Long quizId) {
        return quizPlayerRepository.getTop10PlayersForQuizEvent(quizId);
    }

    //TODO: generate XLS document that contains player rankings for given quiz and also the quiz data.

    public String generateFreePinForEvent() {
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            int randomNumber = random.nextInt(1000000);
            String randomPin = String.valueOf(randomNumber);
            int missingExtraZeroes = 6 - randomPin.length();
            randomPin = "".repeat(missingExtraZeroes) + randomPin;
            if (!isPINinUse(randomPin)) {
                return randomPin;
            }
        }
        for (int i = 0; i < 1000000; ++i) {
            //The ugly way of getting pin, if the random fails too many times
            String randomPin = String.valueOf(i);
            int missingExtraZeroes = 6 - randomPin.length();
            randomPin = "0".repeat(missingExtraZeroes) + randomPin;
            if (!isPINinUse(randomPin)) {
                return randomPin;
            }
        }
        return null;
    }

    public boolean isPINinUse(String pin) {
        return quizEventRepository.isPINinUse(pin);
    }

    public QuizEvent getEventById(Long id) {
        return quizEventRepository.findById(id);
    }

    public void switchEventActiveStatus(Long id) {
        quizEventRepository.swapEventActive(id);
    }

    public QuizEvent createEventForGivenQuiz(Quiz quiz) {
        QuizEvent quizEvent = new QuizEvent();
        quizEvent.setEventActive(true);
        quizEvent.setDateTimeCreated(OffsetDateTime.now());
        quizEvent.setQuiz(quiz);
        String pin = generateFreePinForEvent();
        if (pin == null) {
            return null;
        }
        quizEvent.setPin(pin);
        quizEventRepository.add(quizEvent);
        return quizEvent;
    }

    public QuizPlayer createPlayerForQuizEvent(String playerName, QuizEvent quizEvent) {
        QuizPlayer quizPlayer = new QuizPlayer();
        quizPlayer.setPlayerName(playerName);
        quizPlayer.setScore(0);
        quizPlayer.setQuizEvent(quizEvent);
        quizPlayerRepository.addPlayer(quizPlayer);
        return quizPlayer;
    }
}
