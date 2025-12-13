package com.example.projeto_tcc.service;

import org.springframework.stereotype.Service;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class XsltTransformationService {

    /**
     * "Guia" para o DTD.
     * Quando o parser XML procurar por "xacdml.dtd", esta classe irá encontrá-lo
     * dentro da pasta /resources/dtd do nosso projeto.
     */
    private static class ClasspathEntityResolver implements EntityResolver {
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (systemId != null && systemId.endsWith("xacdml.dtd")) {
                InputStream dtdStream = getClass().getResourceAsStream("/dtd/xacdml.dtd");
                if (dtdStream != null) {
                    return new InputSource(dtdStream);
                }
            }
            return null;
        }
    }

    public String transform(String xmlContent, String xsltResourcePath, String outputFilePath) throws Exception {
        return transform(xmlContent, xsltResourcePath, outputFilePath, null);
    }

    /**
     * Transforma um XML usando XSLT, permitindo a passagem de parâmetros.
     * * @param xmlContent O conteúdo XML de entrada (XACDML).
     * @param xsltResourcePath O caminho para o arquivo XSLT no classpath.
     * @param outputFilePath O caminho onde o código Java gerado será salvo no disco.
     * @param params Parâmetros para serem passados ao XSLT (Map<Nome, Valor>).
     * @return O conteúdo transformado (código Java).
     */
    public String transform(String xmlContent, String xsltResourcePath, String outputFilePath, Map<String, Object> params) throws Exception {
        try {
            // --- 1. Carregar o Stylesheet XSL ---
            InputStream xsltStream = XsltTransformationService.class.getResourceAsStream(xsltResourcePath);
            if (xsltStream == null) {
                throw new java.io.FileNotFoundException("Stylesheet XSL não encontrado no classpath: " + xsltResourcePath);
            }
            Source xslt = new StreamSource(xsltStream);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslt);

            // Passar os Parâmetros para o Transformer
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    transformer.setParameter(entry.getKey(), entry.getValue());
                }
            }

            // --- 2. Preparar o Parser de XML com o "guia" (EntityResolver) ---
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            XMLReader reader = spf.newSAXParser().getXMLReader();

            // Configuração do parser para usar o guia
            reader.setEntityResolver(new ClasspathEntityResolver());

            // --- 3. Criar a fonte de dados XML usando o parser configurado ---
            InputSource xmlInputSource = new InputSource(new StringReader(xmlContent));
            Source xmlSource = new SAXSource(reader, xmlInputSource);

            // --- 4. Preparar o resultado e executar a transformação ---
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(xmlSource, result);

            String generatedContent = writer.toString();

            // --- 5. Salvar o código Java gerado em disco ---
            if (outputFilePath != null && !outputFilePath.isBlank()) {
                Path outputPath = Paths.get(outputFilePath);
                Files.createDirectories(outputPath.getParent());
                Files.writeString(outputPath, generatedContent);
                System.out.println("Arquivo gerado com sucesso em: " + outputPath.toAbsolutePath());
            }

            return generatedContent;

        } catch (Exception e) {
            throw new Exception("Falha na transformação XSLT: " + e.getMessage(), e);
        }
    }
}