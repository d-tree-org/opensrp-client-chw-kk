package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.Utils;

import static org.smartregister.chw.util.Utils.formatDateForVisual;
import static org.smartregister.chw.util.Utils.getClientName;
import static org.smartregister.chw.util.Utils.getFormattedDateFromTimeStamp;

public class UtilsTest extends BaseUnitTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void firstCharacterUppercase_empty() {
        Assert.assertEquals("", Utils.firstCharacterUppercase(""));
    }

    @Test
    public void firstCharacterUppercase_with_one_character() {
        Assert.assertEquals("A", Utils.firstCharacterUppercase("a"));
    }

    @Test
    public void firstCharacterUppercase_with_two_word() {
        Assert.assertEquals("A b", Utils.firstCharacterUppercase("a b"));
    }

    @Test
    public void testConvertDpToPixel() {
        Assert.assertEquals(20.0, Utils.convertDpToPixel(20f, RuntimeEnvironment.application), 0);
    }

    @Test
    public void testTableColConcatEmpty() {
        Assert.assertEquals("", ChildDBConstants.tableColConcat("", ""));
    }

    @Test
    public void testTableColConcatValidInput() {
        Assert.assertEquals("table.col", ChildDBConstants.tableColConcat("table", "col"));
    }

    @Test
    public void testFormatDateForVisual() {
        String date = "2020-06-23";
        String inputFormat = "yyyy-MM-dd";
        String formattedDate = formatDateForVisual(date, inputFormat);
        Assert.assertEquals(formattedDate, "23 Jun 2020");
    }

    @Test
    public void testGetClientName() {
        String name = getClientName("first_name", "middle_name", "last_name");
        if (ChwApplication.getApplicationFlavor().hasSurname())
            Assert.assertEquals("first_name middle_name last_name", name);
        else
            Assert.assertEquals("first_name middle_name", name);

    }
    @Test
    public void testGetDateTimeFromTimeStamp() {
        String mDateFormat = "dd MMM yyyy";
        Long time = Long.valueOf("1606780800000");
        String formattedDateFromTimeStamp = getFormattedDateFromTimeStamp(time, mDateFormat);
        Assert.assertEquals("01 Dec 2020",formattedDateFromTimeStamp);

    }
}
