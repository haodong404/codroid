package org.codroid.interfaces;

import org.codroid.interfaces.utils.PathUtils;
import org.junit.Test;

import org.junit.Assert;

public class PathUtilsTest {

    @Test
    public void StringAndString() {
        String first = "\\mnt\\sdcard";
        String second = "test.txt";
        Assert.assertEquals( first + "\\" + second, PathUtils.splice(first, second).toString());

        Assert.assertEquals("\\mnt\\d\\sdcard\\Downloads",
                PathUtils.splice("\\mnt\\", "\\d", "\\sdcard\\Downloads").toString());
    }
}
