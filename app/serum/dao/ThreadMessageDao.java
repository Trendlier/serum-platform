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
}
