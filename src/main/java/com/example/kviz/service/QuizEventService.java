package com.example.kviz.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.kviz.DTO.GetQuizEventDTO;
import com.example.kviz.DTO.QuizPlayerDTO;
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

    public List<QuizPlayer> getPlayerByPlayerName(String playerName) {
        return quizPlayerRepository.findPlayersByPlayerName(playerName);
    }

    public int getPlayerRank(Long id) {
        QuizPlayer player = getPlayerById(id);
        QuizEvent event = player.getQuizEvent();
        List<QuizPlayerDTO> eventPlayers = quizPlayerRepository.getAllPlayersForQuizEvent(event.getId());
        int rank = 1;
        for (QuizPlayerDTO possiblePlayer : eventPlayers) {
            if (possiblePlayer.id.equals(player.getId())) {
                return rank;
            }
            rank++;
        }
        return -1;
    }

    public List<QuizPlayerDTO> getTopTenPlayers(Long quizEventId) {
        return quizPlayerRepository.getTop10PlayersForQuizEvent(quizEventId);
    }

    public List<QuizPlayerDTO> getAllPlayersForQuizEvent(Long quizEventId) {
        return quizPlayerRepository.getAllPlayersForQuizEvent(quizEventId);
    }

    public List<QuizEvent> findQuizEventsWithPagination(int offset, int limit) {
        return quizEventRepository.findWithPagination(offset, limit);
    }

    public Workbook generateXLSWorkbookFromGetQuizEventDAO(GetQuizEventDTO quiz) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("id");
        headerRow.createCell(1).setCellValue("date/time created");
        headerRow.createCell(2).setCellValue("quiz id");
        headerRow.createCell(3).setCellValue("quiz name");

        Row mainValuesRow = sheet.createRow(1);
        mainValuesRow.createCell(0).setCellValue(quiz.id);
        mainValuesRow.createCell(1).setCellValue(quiz.dateTimeCreated);
        mainValuesRow.createCell(2).setCellValue(quiz.quizId);
        mainValuesRow.createCell(3).setCellValue(quiz.quizName);

        //just some space lmao
        sheet.createRow(2);
        
        Row playersHeaderRow = sheet.createRow(3);
        playersHeaderRow.createCell(0).setCellValue("id");
        playersHeaderRow.createCell(1).setCellValue("player name");
        playersHeaderRow.createCell(2).setCellValue("score");

        int rowCount = 4;
        for (QuizPlayerDTO player : quiz.players) {
            Row playerRow = sheet.createRow(rowCount);
            playerRow.createCell(0).setCellValue(player.id);
            playerRow.createCell(1).setCellValue(player.playerName);
            playerRow.createCell(2).setCellValue(player.score);
            rowCount++;
        }

        return workbook;
    }

    public String generateFreePinForEvent() {
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            int randomNumber = random.nextInt(1000000);
            String randomPin = String.valueOf(randomNumber);
            int missingExtraZeroes = 6 - randomPin.length();
            randomPin = "0".repeat(missingExtraZeroes) + randomPin;
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

    public QuizEvent getActiveQuizByPIN(String pin) {
        return quizEventRepository.getActiveQuizByPIN(pin);
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
