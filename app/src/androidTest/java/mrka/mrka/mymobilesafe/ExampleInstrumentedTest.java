package mrka.mrka.mymobilesafe;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context context = InstrumentationRegistry.getTargetContext();

    /*@Test
    public void TaskMangerEngineTest() {
        TaskManagerEngine.getAllRunningTaskInfo(InstrumentationRegistry.getTargetContext());
        TaskManagerEngine.getAvailMemSize(InstrumentationRegistry.getTargetContext());
        TaskManagerEngine.getTotalMemSize();
        File file = new File(context.getFilesDir(), "WOQU.TXT");
        String packageName = context.getPackageName();
        System.out.println("filepath:" + file.getAbsolutePath() + "packname:" + packageName);
    }*/

    /*@Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.mrka.mymobilesafe", appContext.getPackageName());
    }*/
}
