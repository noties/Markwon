package io.noties.markwon.image.destination;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImageDestinationProcessorRelativeToAbsoluteTest {

    @Test
    public void malformed_base_do_not_process() {
        final ImageDestinationProcessorRelativeToAbsolute processor = new ImageDestinationProcessorRelativeToAbsolute("!@#$%^&*(");
        final String destination = "../hey.there.html";
        assertEquals(destination, processor.process(destination));
    }

    @Test
    public void access_root() {
        final ImageDestinationProcessorRelativeToAbsolute processor = new ImageDestinationProcessorRelativeToAbsolute("https://ro.ot/hello/");
        final String url = "/index.html";
        assertEquals("https://ro.ot/index.html", processor.process(url));
    }

    @Test
    public void access_same_directory() {
        final ImageDestinationProcessorRelativeToAbsolute processor = new ImageDestinationProcessorRelativeToAbsolute("https://ro.ot/hello/");
        final String url = "./.htaccess";
        assertEquals("https://ro.ot/hello/.htaccess", processor.process(url));
    }

    @Test
    public void asset_directory_up() {
        final ImageDestinationProcessorRelativeToAbsolute processor = new ImageDestinationProcessorRelativeToAbsolute("http://ro.ot/first/second/");
        final String url = "../cat.JPG";
        assertEquals("http://ro.ot/first/cat.JPG", processor.process(url));
    }

    @Test
    public void change_directory_inside_destination() {
        final ImageDestinationProcessorRelativeToAbsolute processor = new ImageDestinationProcessorRelativeToAbsolute("http://ro.ot/first/");
        final String url = "../first/../second/./thi.rd";
        assertEquals(
                "http://ro.ot/second/thi.rd",
                processor.process(url)
        );
    }

    @Test
    public void with_query_arguments() {
        final ImageDestinationProcessorRelativeToAbsolute processor = new ImageDestinationProcessorRelativeToAbsolute("http://ro.ot/first/");
        final String url = "../index.php?ROOT=1";
        assertEquals(
                "http://ro.ot/index.php?ROOT=1",
                processor.process(url)
        );
    }
}