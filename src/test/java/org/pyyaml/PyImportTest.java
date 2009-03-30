/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.pyyaml;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Util;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.reader.UnicodeReader;

public abstract class PyImportTest extends TestCase {
    public static final String PATH = "pyyaml";

    protected Object load(String data) {
        Yaml yaml = new Yaml();
        Object obj = yaml.load(data);
        return obj;
    }

    protected Object load(Loader loader, String data) {
        Yaml yaml = new Yaml(loader);
        Object obj = yaml.load(data);
        return obj;
    }

    protected Iterable<Object> loadAll(InputStream data) {
        Yaml yaml = new Yaml();
        return yaml.loadAll(data);
    }

    protected Iterable<Object> loadAll(String data) {
        Yaml yaml = new Yaml();
        return yaml.loadAll(data);
    }

    protected Iterable<Object> loadAll(Loader loader, String data) {
        Yaml yaml = new Yaml(loader);
        return yaml.loadAll(data);
    }

    protected String getResource(String theName) {
        try {
            String content;
            content = Util.getLocalResource(PATH + File.separator + theName);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File[] getStreamsByExtension(String extention) {
        return getStreamsByExtension(extention, false);
    }

    protected File[] getStreamsByExtension(String extention, boolean onlyIfCanonicalPresent) {
        File file = new File("src/test/resources/pyyaml");
        assertTrue("Folder not found: " + file.getAbsolutePath(), file.exists());
        assertTrue(file.isDirectory());
        File[] files = file.listFiles(new PyFilenameFilter(extention, onlyIfCanonicalPresent));
        return files;
    }

    protected File getFileByName(String name) {
        File file = new File("src/test/resources/pyyaml/" + name);
        assertTrue("Folder not found: " + file.getAbsolutePath(), file.exists());
        assertTrue(file.isFile());
        return file;
    }

    protected List<Event> canonicalParse(InputStream input2) throws IOException {
        Reader reader = new Reader(new UnicodeReader(input2));
        StringBuffer buffer = new StringBuffer();
        while (reader.peek() != '\0') {
            buffer.append(reader.peek());
            reader.forward();
        }
        CanonicalParser parser = new CanonicalParser(buffer.toString());
        List<Event> result = new LinkedList<Event>();
        while (parser.peekEvent() != null) {
            result.add(parser.getEvent());
        }
        return result;
    }

    protected List<Event> parse(InputStream input) throws IOException {
        Reader reader = new Reader(new UnicodeReader(input));
        Parser parser = new ParserImpl(reader);
        List<Event> result = new LinkedList<Event>();
        while (parser.peekEvent() != null) {
            result.add(parser.getEvent());
        }
        return result;
    }

    private class PyFilenameFilter implements FilenameFilter {
        private String extension;
        private boolean onlyIfCanonicalPresent;

        public PyFilenameFilter(String extension, boolean onlyIfCanonicalPresent) {
            this.extension = extension;
            this.onlyIfCanonicalPresent = onlyIfCanonicalPresent;
        }

        public boolean accept(File dir, String name) {
            int position = name.lastIndexOf('.');
            String canonicalFileName = name.substring(0, position) + ".canonical";
            File canonicalFile = new File(dir, canonicalFileName);
            if (onlyIfCanonicalPresent && !canonicalFile.exists()) {
                return false;
            } else {
                return name.endsWith(extension);
            }
        }
    }
}