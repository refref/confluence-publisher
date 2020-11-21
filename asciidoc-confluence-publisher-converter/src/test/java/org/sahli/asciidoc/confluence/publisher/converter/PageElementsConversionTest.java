package org.sahli.asciidoc.confluence.publisher.converter;

import com.google.common.io.Resources;
import org.hamcrest.Matchers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sahli.asciidoc.confluence.publisher.client.metadata.ConfluencePublisherMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;

public class PageElementsConversionTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    AsciidocConfluenceConverter converter = new AsciidocConfluenceConverter("~personalSpace", "1234");

    @Test
    public void renderingOfCollapsibleBlock() throws Exception {
        // arrange
        Path documentationRootFolder = Paths.get("src/test/resources/pageparts").toAbsolutePath();
        Path buildFolder = this.temporaryFolder.newFolder().toPath().toAbsolutePath();

        AsciidocPagesStructureProvider asciidocPagesStructureProvider = new FolderBasedAsciidocPagesStructureProvider(documentationRootFolder, UTF_8);

        // act
        ConfluencePublisherMetadata result = converter.convert(asciidocPagesStructureProvider, buildFolder, emptyMap());

        //verify
        Document resultingContents = parseDocument(fileContent(result.getPages().get(0).getContentFilePath()));
        Document expectedContents = parseDocument(testResource("pageparts/collapsible.html"));
        assertThat(resultingContents.body().toString(), Matchers.equalTo(expectedContents.body().toString()));
    }

    private Document parseDocument(String html) throws IOException {
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax( Document.OutputSettings.Syntax.xml);
        return document;
    }

    private String fileContent(String contentFilePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(contentFilePath)), UTF_8);
    }

    @SuppressWarnings("UnstableApiUsage")
    private String testResource(String location) throws IOException {
        return new String(Resources.toByteArray(Resources.getResource(location)), UTF_8);
    }

}
