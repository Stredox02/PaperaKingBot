package it.stredox02.duckbot.bot;

import lombok.RequiredArgsConstructor;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
public class DuckKingBot extends TelegramLongPollingBot {

    private final String token;
    private final String username;
    private final YamlFile groupCache;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || update.hasCallbackQuery()) {
            return;
        }
        Message message = update.getMessage();
        Long chatID = message.getChatId();
        if(message.getText().equalsIgnoreCase("/king")){
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatID.toString());
            sendPhoto.setPhoto(new InputFile(new File("zera.jpg")));
            sendPhoto.setCaption("\uD83C\uDF89 • <b>DUCK KING</b> • \uD83E\uDD86 \n\n\uD83D\uDC51 — <i>Zera</i> è un vero <b>duck lover</b> ❤️");
            sendPhoto.setParseMode("HTML");
            executeAsync(sendPhoto);
            return;
        }
        if (!message.getText().equalsIgnoreCase("papera")) {
            return;
        }
        User user = message.getFrom();
        ConfigurationSection section = groupCache.getConfigurationSection("chats");
        for(String key : section.getKeys(false)) {
            if(!key.equals(""+chatID)){
                continue;
            }
            if (section.getLong(key + ".id") == user.getId()) {
                SendMessage alreadyTakenMessage = new SendMessage();
                alreadyTakenMessage.setChatId(message.getChatId().toString());
                alreadyTakenMessage.enableHtml(true);
                alreadyTakenMessage.setText("<a href=\"https://i.imgur.com/oVj2mtR.jpeg\">&#8205</a> \uD83D\uDE2D || <b>HEY</b> • \uD83E\uDD86\n" +
                        "\n" +
                        "\uD83D\uDC51 — <b>" +
                        section.getString(key + ".username") + (section.getString(key + ".lastname") != null ? " " + section.getString(key + ".lastname") : "") +
                        "</b> sei gia' il paperone di oggi :)");

                try {
                    executeAsync(alreadyTakenMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (section.getString(key + ".id") != null) {
                SendMessage alreadyTakenMessage = new SendMessage();
                alreadyTakenMessage.setChatId(message.getChatId().toString());
                alreadyTakenMessage.enableHtml(true);
                alreadyTakenMessage.setText("<a href=\"https://i.imgur.com/oVj2mtR.jpeg\">&#8205</a> \uD83D\uDE2D || <b>Mi dispiace tanto</b> • \uD83E\uDD86\n" +
                        "\n" +
                        "\uD83D\uDC51 — Purtroppo <b>" +
                        section.getString(key + ".username") + (section.getString(key + ".lastname") != null ? " " + section.getString(key + ".lastname") : "") +
                        "</b> ha già preso il posto di Re Papera di oggi!");

                try {
                    executeAsync(alreadyTakenMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        SendMessage newDuckKing = new SendMessage();
        newDuckKing.setChatId(message.getChatId().toString());
        newDuckKing.enableHtml(true);
        newDuckKing.setText("<a href=\"https://i.imgur.com/2JTqaSI.jpeg\">&#8205</a> \uD83C\uDF89 || <b>Complimenti!</b> • \uD83E\uDD86 \n\n" +
                "\uD83D\uDC51 — <b>" + user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "") + "</b> sei il Re Papera di oggi!\n\n");
        try {
            executeAsync(newDuckKing);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        ZoneId zoneId = ZoneId.of("Europe/Rome");
        ZonedDateTime start = ZonedDateTime.ofInstant(Instant.now(), zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime tomorrow = start.plusDays(1);

        groupCache.set("chats." + chatID + ".id", user.getId());
        groupCache.set("chats." + chatID + ".username", user.getFirstName());
        groupCache.set("chats." + chatID + ".lastname", user.getLastName());
        groupCache.set("chats." + chatID + ".time", tomorrow.toEpochSecond());
        try {
            groupCache.save();
            groupCache.load();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
