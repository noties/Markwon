package io.noties.markwon.image.destination;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static io.noties.markwon.image.destination.ImageDestinationProcessorAssets.BASE;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImageDestinationProcessorAssetsTest {

    private ImageDestinationProcessorAssets processor;

    @Before
    public void before() {
        processor = new ImageDestinationProcessorAssets();
    }

    @Test
    public void access_root() {
        final String path = "/whoam.i";
        assertEquals(
                BASE.substring(0, BASE.length() - 1) + path,
                processor.process(path)
        );
    }

    @Test
    public void access_folder_without_modifier() {
        final String path = "first/second/thi.rd";
        assertEquals(
                BASE + path,
                processor.process(path)
        );
    }

    @Test
    public void change_directory_inside_path() {
        final String path = "first/../second/./thi.rd";
        assertEquals(
                BASE + "second/thi.rd",
                processor.process(path)
        );
    }
}