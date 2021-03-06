package base.results;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import base.Logger;
import base.TestCaseDocs;
import base.xml.XmlResult.Result;
import base.xml.XmlTestConfig;

public class Results {
	private Logger logger = Logger.getLogger();
	/**
	 * Set a TestInfo endDate and result and add it to testsResults list. 
	 * 
	 * @param testResult - current TestResult
	 * @param result - the test result
	 */
	public void addTestResult(
			List<TestResult> testResultInfo,
			TestResult testResult, 
			Result result, 
			Instant startTestTime){

		testResult.setElapsedTestTime( startTestTime );
		testResult.setResult( result );
		testResultInfo.add(testResult);
	}


	/**
	 * Set a TestCaseInfo endDate and result and add it to testInfo. 
	 * 
	 * @param testInfo - current TestResult
	 * @param result - the test result
	 * @param testCaseResult - current TestCaseResult
	 * @param startTestTime - test case tart time
	 */
	public void addTestCaseResult(
			TestResult testResult, 
			TestCaseResult testCaseResult, 
			Result result, 
			Instant startTestCaseTime){

		// set testCase results info
		testCaseResult.setElapsedTestTime( startTestCaseTime );
		testCaseResult.setResult( result );
		// add test case result to current test result
		testResult.addTestCaseInfo(testCaseResult.getId(), testCaseResult);
	}



	public String getDetailedResults(
			List<TestResult> testResultInfo,
			int totalTests,
			int succeededTests,
			int failedTests, 
			int crashedTests,
			String elapsedSuiteTime
			){
		StringBuilder resultsString = new StringBuilder();

		resultsString.append(getResults(
				totalTests, succeededTests, 
				failedTests, crashedTests,
				elapsedSuiteTime) + "\n")
		.append(logger.getSeparator()+"\n")
		.append(TestResult.getHeader(15)+"\n");

		for (TestResult testResult : testResultInfo){
			resultsString.append(testResult.getInfo(15)+"\n");
			Map<Integer, TestCaseResult> testCasesResults = testResult.getTestCasesResults();
			testCasesResults.values().forEach( 
					v -> resultsString.append(testResult.getId()+ "/" + v.getInfo(13)+"\n"));
		}
		resultsString.append(logger.getSeparator());
		return resultsString.toString();
	}



	private String createTableHeaderRow(List<String> columns, String className){
		StringBuilder row = new StringBuilder();
		row.append("<tr>\n");

		columns.forEach(c -> row.append("<th>\n")
				.append(c)
				.append("</th>\n"));

		row.append("</tr>");

		return row.toString();
	}



	private String createTableRow(List<String> columns, String className){
		StringBuilder row = new StringBuilder();
		row.append("<tr>\n");

		columns.forEach(c -> row.append("<td>\n")
				.append(c)
				.append("</td>\n"));

		row.append("</tr>");

		return row.toString();
	}



	public String getDetailedResultsHtml(
			List<TestResult> testResultInfo,
			int totalTests,
			int succeededTests,
			int failedTests, 
			int crashedTests,
			String elapsedSuiteTime
			){
		String suiteResult = (totalTests==succeededTests) ? 
				"<font color=\"green\">Succeeded</font>" : 
					"<font color=\"red\">Failed</font>";
		StringBuilder resultsString = new StringBuilder();

		resultsString.append("<html>\n<head>\n"
				+ "<title>Details</title>\n"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"../../html/css/main.css\">\n"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"../../html/css/responsive.css\">\n"
				+ "<script src=\"http://ajax.aspnetcdn.com/ajax/jQuery/jquery-2.1.1.js\"></script>"
				+ "<script src=\"../../html/js/drsu_javascript.js\"></script>"
				+ "</head>\n"
				+ "<body onload=\"resultsPageSetup();\">\n");

		// Suite result
		resultsString.append("<div id=\"suiteResultsContainer\">");
		resultsString.append("<table class=\"suiteResults\">\n");
		resultsString.append(createTableHeaderRow(
				new ArrayList<String>(Arrays.asList(
						"TestSuite",
						"Result",
						"Elapsed",
						"TotalTests",
						"Succeeded",
						"Failed",
						"Crashed")),
				"suiteResults")
				);

		resultsString.append(createTableRow(
				new ArrayList<String>(Arrays.asList(
						XmlTestConfig.getSuiteName(),
						suiteResult,
						elapsedSuiteTime,
						Integer.toString(totalTests),
						Integer.toString(succeededTests), 
						Integer.toString(failedTests),
						Integer.toString(crashedTests)
						)),

				"suiteResults"));



		resultsString.append("</table>\n</div>\n<br/><br/>\n");

		// Tests/TestCaes details
		resultsString.append("<table class=\"results\">\n");

		String testHeader = "<span title=\"Expand/collapse test cases\">"
				+ "<svg class=\"svg24\" id=\"expandTestcases\" xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" >"
				+ "<path fill=\"#00FF00\" fill-rule=\"evenodd\" d=\"M16 11h-3V8c0-.6-.4-1-1-1s-1 .4-1 1v3H8c-.6 0-1 .4-1 1s.4 1 1 "
				+ "1h3v3c0 .6.4 1 1 1s1-.4 1-1v-3h3c.6 0 1-.4 1-1s-.4-1-1-1zM12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.6 "
				+ "0 12 0zm0 22C6.5 22 2 17.5 2 12S6.5 2 12 2s10 4.5 10 10-4.5 10-10 10z\" clip-rule=\"evenodd\"/>"
				+ "</svg></span>&nbsp;"
				+ "Test/Testcase";

		String details = "<span title=\"Show test cases info\">"
				+ "<svg class=\"svg24\" id=\"showDetails\" xmlns=\"http://www.w3.org/2000/svg\" width=\"115\" height=\"24\">"
				+ "<path fill=\"#00FF00\" fill-rule=\"evenodd\" d=\"M99 0c-6.6 0-12 5.4-12 12s5.4 12 12 12 12-5.4 12-12-5.4-12-12-12zm.4 "
				+ "6c.8 0 1.5.7 1.5 1.5S100.2 9 99.4 9s-1.5-.7-1.5-1.5.6-1.5 1.5-1.5zm1.6 10.3c-.7 1.1-1.5 1.6-2.7 1.6-1 0-1.9-1-1.7-1.6L98 "
				+ "12c0-.1 0-.3-.1-.3-.9.1-1.8.6-1.8.6 0-.2 0-.1 0-.3.7-1.1 2.2-2.1 3.4-2 .7.1.8.9.7 1.6L99 16c0 .3.2.5.5.5.5 0 1.5-1.1 1.5-1.1 "
				+ "0 .3 0 .8 0 .9z\" clip-rule=\"evenodd\"/>" 
				+ "</svg></span>"
				+ "Details";

		resultsString.append(createTableHeaderRow(
				new ArrayList<String>(Arrays.asList(
						testHeader,
						"Name",
						"Elapsed",
						"Result",
						details)),
				"results"));



		for (TestResult testResult : testResultInfo){
			String res = testResult.getResult().toString();
			Map<String, String> testAttr = new TreeMap<>();
			testAttr.putAll(testResult.getAttributes());
			testAttr.remove("name");

			// first add test result info
			resultsString.append(createTableRow(
					new ArrayList<String>(Arrays.asList(
							testResult.getId(),
							testResult.getAttributes().get("name"),
							testResult.getElapsedTime(),
							(res.contains("Pass")) ? 
									"<font color=\"green\">" + res + "</font>" : 
										"<font color=\"red\">" + res + "</font>",

										attrToHtml(testAttr)
							)),

					"details"));

			//resultsString.append("<td>" + testResult.getInfo(15)+"\n");

			// add test cases result info
			Map<Integer, TestCaseResult> testCasesResults = testResult.getTestCasesResults();
			testCasesResults.values().forEach( 
					v -> {	
						final String result = v.getResult().name();
						final Map<String, String> attr = new TreeMap<>();
						attr.putAll(v.getAttributes());
						attr.remove("name");
	


						resultsString.append("<tr style=\"display:none;\">\n"
								+ "<td>" 
								+ testResult.getId()
								+ "/"
								+ v.getId()
								+ "</td>\n" 

								// test case name column 
								+ "<td>" 
								+ v.getAttributes().get("name")
								+ "</td>\n"


								// test case elapsed column 
								+ "<td>" 
								+ v.getElapsedTime()
								+ "</td>\n"

								// test case result column 
								+ "<td>"
								+
								(result.contains("Pass") ? 
										"<font color=\"green\">" + result + "</font>" : 
											"<font color=\"red\">" + result + "</font>")
								+ "</td>\n"


								// test case attributes column
								+ "<td>"
								+ attrToHtml(v.getAttributes().get("name"), attr)
								+ "</td>\n"
								+ "</tr>\n");
					}
					);
		}
		resultsString.append("</table>\n"
				/*+ "<br>\n"

				+ "<footer>"
				+ "<img width=\"101\" alt=\"\" src=\"../images/azets.png\">"
				+ "</footer>"
				 */
				+"</body>\n"
				+ "</html>\n");

		return resultsString.toString();
	}




	/**
	 * Get final results separated by pipes.
	 * 
	 * @param total - total number of executed tests
	 * @param succeeded - number of succeeded tests
	 * @param failed - number of failed tests
	 * @param crashed - number of crashed tests
	 */
	public String getResults(int total, int succeeded, 
			int failed, int crashed, String elapsedTime){

		String colWidth = "15";
		String format = "%-"+ colWidth + "s";


		return new StringBuilder()
				.append(Logger.getLogger().getSeparator() + "\n")
				// header
				.append( String.format(format, "Total" ) + " | ")
				.append(String.format(format.replace(colWidth, "20"), "Elapsed" ) + " | ")
				.append(String.format(format.replace(colWidth, "30"), "Succeeded" ) + " | ")
				.append(String.format(format.replace(colWidth, "15"), "Failed" ) + " | ")
				.append(String.format(format, "Crashed" ) + " | \n")
				// result 
				.append(String.format(format, total ) + " | ")
				.append(String.format(format.replace(colWidth, "20"), elapsedTime ) + " | ")
				.append( String.format(format.replace(colWidth, "30"), succeeded ) + " | ")
				.append(String.format(format.replace(colWidth, "15"), failed ) + " | ")
				.append(String.format(format, crashed ) + " |")
				.toString();
	}



	public String getUniqueResultFileName(String hostname, Path resultFilePath){
		return hostname.replace('.', '_') 
				+ "_" 
				+ resultFilePath
				.toString()
				.replace(System.getProperty("user.dir"), "")
				.replace('\\', '_')

				.replace("_logs_", "")
				.replace("log_", "");
	}



	public void log(List<TestResult> testResultInfo,
			int totalTests,
			int succeededTests,
			int failedTests,
			int crashedTests,
			String elapsedSuiteTime
			){

		logger.logLines(getDetailedResults(
				testResultInfo, 
				totalTests, 
				succeededTests, failedTests, crashedTests, 
				elapsedSuiteTime));
	}



	public String mapToHtmlColumns(Map<String, String> map){
		StringBuilder retHtmlString = new StringBuilder();
		map.values().forEach(
				v -> retHtmlString.append("<td>")
				.append(v)
				.append("</td>\n")
				);
		return retHtmlString.toString();
	}


	private String attrToHtml(String testcaseName, Map<String, String> attr) {
		String htmlAttr = new String();
		String failure = attr.remove("failure");
		String note = attr.remove("note");
		String save = attr.remove("save");
		String saveResult = attr.remove("saveResult");



		String js_errors = attr.remove("js_errors");
		String testDocs = TestCaseDocs.getTestScenario(
				XmlTestConfig.getSuitePackages(), 
				testcaseName
				).substring(1).replace("\n","<br>");


		for (String attribute : attr.keySet()){
			htmlAttr += attribute + "=\"" + attr.get(attribute) + "\"<br/>";
		}

		if ( save != null ){
			htmlAttr += "<br>saved:" + save;
		}

		if ( saveResult != null ){
			htmlAttr += "<br>savedResults:{" + saveResult + "}";
		}

		if (failure!=null){
			htmlAttr += "<div class=\"failure\">failure: " 
					+ failure.replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;")
					.replaceAll("Cause:", "<br>Cause:")
					+ "</div>";
		}

		if (js_errors!=null){
			htmlAttr += "<div class=\"js_errors\">JS_ERRORS: " + js_errors + "</div>";
		}

		if(note!=null){
			htmlAttr += "<div class=\"note\">Note:<br> " + note.replace("\\n", "<br>") + "</div>";
		}

		// add hidden testCaseScenario information, available on click
		htmlAttr += "<div class=\"testCaseDocs\" title=\"Test case info\">"
				+ testDocs 
				+ "</div>";

		return htmlAttr;
	}

	private String attrToHtml(Map<String, String> attr) {
		String htmlAttr = new String();
		String failure = attr.remove("failure");
		String js_errors = attr.remove("js_errors");

		
		//htmlAttr = attr.toString().replace("{", "").replace("}", "").replace("save=", "saved:");
		for (String attribute : attr.keySet()){
			htmlAttr = htmlAttr + (attribute + "=\"" + attr.get(attribute) + "\"</br>").replace("save=", "saved:");;
		}
		

		if (failure!=null){
			htmlAttr += "<br><span class=\"failure\">failure: " + failure + "</span>";
		}

		if (js_errors!=null){
			htmlAttr += "<br><div class=\"js_errors\">JS_ERRORS: " + js_errors + "</div>";
		}

		return htmlAttr;
	}
}
