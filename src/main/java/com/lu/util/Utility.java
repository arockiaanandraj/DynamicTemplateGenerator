package com.lu.util;

import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Utility {

	public Boolean addGroupToJasperTemplate(String sourceTemplateFileName, String destTemplateFileName,
			String subDatasetName, String groupName, Integer replicationCount) {
		long startTime = System.currentTimeMillis();
		try {
			File sourceFile = new File(sourceTemplateFileName);
			if (!sourceFile.exists())
				return false;
			SAXReader reader = new SAXReader();
			Document document = reader.read(sourceFile);
			Element rootElement = document.getRootElement();

			for (int i = 1; i <= replicationCount; i++) {
				Node clonedSubDataset = (Node) document
						.selectSingleNode("/*[local-name() = 'jasperReport']/*[local-name() = 'subDataset'][@name = \""
								+ subDatasetName + "\"]")
						.clone();
				// System.out.println(clonedSubDataset);
				Element clonedSubDatasetElement = (Element) clonedSubDataset;
				clonedSubDatasetElement.addAttribute("name", subDatasetName + "_" + i);
				clonedSubDatasetElement.addAttribute("uuid", UUID.randomUUID().toString());
				Element parameterElement = (Element) clonedSubDatasetElement.elementIterator("parameter").next();
				parameterElement.addAttribute("name", parameterElement.attributeValue("name") + "_" + i);
				rootElement.add(clonedSubDataset);

				Node clonedGroupNode = (Node) document.selectSingleNode(
						"/*[local-name() = 'jasperReport']/*[local-name() = 'group'][@name = \"" + groupName + "\"]")
						.clone();
				// System.out.println(clonedGroupNode);
				Element element = (Element) clonedGroupNode;
				element.addAttribute("name", groupName + "_" + i);
				rootElement.add(clonedGroupNode);

				Node datasetRunNode = (Node) document
						.selectSingleNode("/*[local-name() = 'jasperReport']" + "/*[local-name() = 'group'][@name = \""
								+ groupName + "_" + i + "\"]" + "/*[local-name() = 'groupFooter']"
								+ "/*[local-name() = 'band']" + "/*[local-name() = 'componentElement']"
								+ "/*[local-name() ='table']" + "/*[local-name() = 'datasetRun']");

				// System.out.println(datasetRunNode);
				Element datasetRunElement = (Element) datasetRunNode;
				datasetRunElement.addAttribute("subDataset", subDatasetName + "_" + i);
				datasetRunElement.addAttribute("uuid", UUID.randomUUID().toString());

				Node dataSourceExpressionNode = (Node) document.selectSingleNode(
						"/*[local-name() = 'jasperReport']" + "/*[local-name() = 'group'][@name = \"" + groupName + "_"
								+ i + "\"]" + "/*[local-name() = 'groupFooter']" + "/*[local-name() = 'band']"
								+ "/*[local-name() = 'componentElement']" + "/*[local-name() ='table']"
								+ "/*[local-name() = 'datasetRun']" + "/*[local-name() = 'dataSourceExpression']");
				// System.out.println(dataSourceExpressionNode);
				Element dataSourceExpressionElement = (Element) dataSourceExpressionNode;
				dataSourceExpressionElement.clearContent();
				dataSourceExpressionElement.addCDATA("$P{tableFive_" + i + "}");

			}

			try (FileWriter fileWriter = new FileWriter(new File(destTemplateFileName))) {
				XMLWriter writer = new XMLWriter(fileWriter);
				writer.write(document);
				writer.close();
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(document);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			long endTime = System.currentTimeMillis();
			long timeElapsed = endTime - startTime;
			System.out.println("Execution time in milliseconds: " + timeElapsed);
		}
		return true;
	}

	public Document styleDocument(Document document, String stylesheet) throws Exception {

		// load the transformer using JAXP
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));

		// now lets style the given document
		DocumentSource source = new DocumentSource(document);
		DocumentResult result = new DocumentResult();
		transformer.transform(source, result);

		// return the transformed document
		Document transformedDoc = result.getDocument();
		return transformedDoc;
	}
}
