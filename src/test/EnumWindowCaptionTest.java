package test;

import TextEdit.EnumWindowCaption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumWindowCaptionTest {

    /**
     * assert caption attribute per enumeration
     * */
    @Test
    void test_EnumWindowCaption_ENUM_caption() {
        assertEquals("SMARTY", EnumWindowCaption.PRIMARY.caption);
    }
}
