package serum.dao;

import java.util.*;

import javax.persistence.*;
import play.db.jpa.*;

import serum.model.*;

public class ThreadDao
{
    public static ThreadModel createThread(String title)
    {
        ThreadModel thread = new ThreadModel(title);
        JPA.em().persist(thread);
        return thread;
    }

    public static ThreadModel getThreadById(Long id)
    {
        return JPA.em().find(ThreadModel.class, id);
    }
}
