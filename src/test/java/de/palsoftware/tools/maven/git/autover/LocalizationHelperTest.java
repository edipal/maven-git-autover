package de.palsoftware.tools.maven.git.autover;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: comment
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class LocalizationHelperTest {

    @Test
    public void getMessage() {
        final LocalizationHelper localizationHelper = new LocalizationHelper();
        String message = localizationHelper.getMessage(LocalizationHelper.ERR_EMPTY_VERSION_PATCH_DETECTED, "AABBCC");
        Assert.assertTrue("LocalizationHelper problem!", ((message != null) && (message.length() > 0)));
        message = localizationHelper.getMessage(LocalizationHelper.ERR_NO_GROUP_DEFINED_FOR_VERSION_PATCH, "AABBCC");
        Assert.assertTrue("LocalizationHelper problem!", ((message != null) && (message.length() > 0)));
        message = localizationHelper.getMessage(LocalizationHelper.ERR_INVALID_RB_KEY, "AABBCC");
        Assert.assertTrue("LocalizationHelper problem!", ((message != null) && (message.length() > 0)));
        message = localizationHelper.getMessage(LocalizationHelper.ERR_INVALID_VERSION_PATCH_DETECTED, "1a", "AABBCC");
        Assert.assertTrue("LocalizationHelper problem!", ((message != null) && (message.length() > 0)));

        try {
            localizationHelper.getMessage("DUMMY", "1a", "AABBCC");
            Assert.fail("LocalizationHelper problem!");
        } catch (final IllegalArgumentException e) {
            //on
        }
    }
}