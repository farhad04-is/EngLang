package com.example.EngLang.Service.YeniTelegram;

import com.example.EngLang.Entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component

public class TelegramBot extends TelegramLongPollingBot {

    private final String botName;
    private final String botToken;
    private final SearchService searchService;

    public TelegramBot(String botName, String botToken, SearchService searchService) {
        super(botToken);
        this.botName = botName;
        this.botToken = botToken;
        this.searchService = searchService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String email = update.getMessage().getText().trim();

            List<Word> words = searchService.getWordsByEmail(email);
            String message;

            if (words.isEmpty()) {
                message = "❗ Bu e-poçta uyğun söz tapılmadı.";
            } else {
                message = formatWordListAsTable(words);
            }

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setText(message);
            sendMessage.setParseMode("Markdown"); // monospace için Markdown kullan

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private String formatWordListAsTable(List<Word> words) {
        StringBuilder builder = new StringBuilder();
        builder.append("```\n")
                .append(String.format("%-20s %-20s %-10s\n", "Word", "Translation", "Level"))
                .append("-----------------------------------------------------------\n");

        for (Word word : words) {
            builder.append(String.format("%-20s %-20s %-10s\n",
                    sanitize(word.getWord()),
                    sanitize(word.getTranslation()),
                    sanitize(word.getEnglishLevel())));
        }

        builder.append("```");

        // Telegram karakter limiti kontrolü
        if (builder.length() > 4000) {
            builder.setLength(3990);
            builder.append("\n...devamı var```");
        }

        return builder.toString();
    }

    private String sanitize(String input) {
        if (input == null) return "";
        return input.replace("*", "")
                .replace("_", "")
                .replace("[", "")
                .replace("`", "")
                .replace("]", "")
                .replace("(", "")
                .replace(")", "");
    }
}
