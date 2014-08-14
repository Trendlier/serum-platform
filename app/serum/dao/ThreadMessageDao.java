package serum.dao;

import java.util.*;

import javax.persistence.*;
import play.db.jpa.*;

import serum.model.*;

public class ThreadMessageDao
{
    public static ThreadMessage createThreadMessage(ThreadUser threadUser, String text)
    {
        ThreadMessage threadMessage = new ThreadMessage(threadUser, text);
        JPA.em().persist(threadMessage);
        return threadMessage;
    }

    public static void removeThreadMessage(ThreadMessage threadMessage)
    {
        threadMessage.isDeleted = true;
    }

    public static ThreadMessage getThreadMessageById(Long id)
    {
        return JPA.em().find(ThreadMessage.class, id);
    }

    public static void markThreadMessageAsRead(ThreadUser threadUser, ThreadMessage threadMessage)
    {
        ThreadUserMessageRead r = new ThreadUserMessageRead(threadUser, threadMessage);
        JPA.em().persist(r);
    }

    public static List<ThreadMessage> getUnreadMessages(ThreadUser threadUser)
    {
        Collection<ThreadMessage> threadMessages = threadUser.thread.getThreadMessages();
        if (threadMessages.isEmpty())
        {
            return new ArrayList<ThreadMessage>();
        }
        return JPA.em().createQuery(
                "select m from ThreadMessage m " +
                "where not exists ( " +
                  "select r from ThreadUserMessageRead r " +
                  "where r.threadUser = :threadUser " +
                  "and r.threadMessage = m " +
                ") " +
                "and m in :threadMessages ",
                ThreadMessage.class)
            .setParameter("threadUser", threadUser)
            .setParameter("threadMessages", threadMessages)
            .getResultList();
    }
}
