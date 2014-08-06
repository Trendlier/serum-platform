package serum.util;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class IdHashUtilTest
{
    @Test
    public void testEncryptDecryptIdHash()
    throws Exception
    {
        Long[] ids =
            new Long[] {
                0L,
                1L,
                50L,
                64L,
                1000L,
                123456L,
                839832011L
            };
        for (Long id: ids)
        {
            assertFalse(id.equals(IdHashUtil.encrypt(id)));
            assertEquals(id, IdHashUtil.decrypt(IdHashUtil.encrypt(id)));
        }
    }
}
