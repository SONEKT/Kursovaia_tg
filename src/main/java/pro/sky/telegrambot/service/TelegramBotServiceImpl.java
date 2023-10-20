package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTasksRepository;
import pro.sky.telegrambot.tools.Parser;
@Service
public class TelegramBotServiceImpl implements TelegramBotService{

    private final TelegramBot telegramBot;
    private final NotificationTasksRepository taskRepository;

    private Logger logger = LoggerFactory.getLogger(TelegramBotServiceImpl.class);

    public TelegramBotServiceImpl(TelegramBot telegramBot, NotificationTasksRepository taskRepository) {
        this.telegramBot = telegramBot;
        this.taskRepository = taskRepository;
    }

    @Override
    public void addTask(Message message) {
        NotificationTask task;
        long chatId = message.chat().id();
        SendMessage result;
        try {
            task = Parser.createTask(message.text());
            task.setChatId(chatId);
        } catch (Exception ex) {
            result = new SendMessage(chatId, "incorrect data format");
            telegramBot.execute(result);
            logger.warn("from chat with id {} was sent incorrect request {}, notification not created",message.chat().id(),message.text());
            return;
        }
        taskRepository.save(task);
        result = new SendMessage(chatId, "notification added");
        telegramBot.execute(result);
        logger.info("for chat with id {} was created notification {}",message.chat().id(),message.text());
    }
}
