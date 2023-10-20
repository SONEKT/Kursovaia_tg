package pro.sky.telegrambot.model;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.repository.NotificationTasksRepository;
import pro.sky.telegrambot.service.TelegramBotService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
@Service
public class NotificationScheduler {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTasksRepository repository;
    @Scheduled(cron="0 0/1 * * * *")
    public void notificationScheduler1(){
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<NotificationTask> tasks_now = repository.findByDateTime(dateTime);
        for(NotificationTask task:tasks_now){
            logger.info("sending task {}",task);
            var response = new SendMessage(task.getChatId(),task.getNotification());
            telegramBot.execute(response);
            task.setSent(true);
            repository.save(task);
        }
    }
}
