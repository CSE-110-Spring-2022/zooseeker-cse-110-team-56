package edu.ucsd.cse110.team56.zooseeker;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExhibitsListTest {
    @Test
    public void emptyListCountTest() {
        ExhibitsList testList = new ExhibitsList();
        assertEquals(0, testList.getList().size());
    }

    @Test
    public void addItemsCountTest() {
        ExhibitsList testList = new ExhibitsList();
        testList.addItem("itemA");
        testList.addItem("itemB");
        assertEquals(2, testList.getList().size());
    }

    @Test
    public void addItemsValueTest() {
        ExhibitsList testList = new ExhibitsList();
        testList.addItem("itemA");
        testList.addItem("itemB");
        testList.addItem("itemC");
        assertEquals("itemB", testList.getList().get(1));
    }
}
