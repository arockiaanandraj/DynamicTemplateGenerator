package com.lu.dynamicTemplateGenerator;

import com.lu.util.Utility;

public class App {
	public static void main(String[] args) {
		String sourceTemplateFileName = "Template.xml";
		String destTemplateFileName = "ModifiedTemplate.xml";
		String subDatasetName = "ParameterFive";
		String groupName = "Parameter5";
		Integer replicationCount = 3;
		Utility utility = new Utility();
		Boolean result = utility.addGroupToJasperTemplate(sourceTemplateFileName, destTemplateFileName, subDatasetName,
				groupName, replicationCount);
		System.out.println(result ? "Success" : "Failure");
	}
}
