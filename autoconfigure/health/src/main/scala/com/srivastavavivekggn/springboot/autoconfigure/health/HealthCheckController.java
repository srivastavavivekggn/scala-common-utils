package com.srivastavavivekggn.springboot.autoconfigure.health;


import com.srivastavavivekggn.platform.heathcheck.*;
import com.srivastavavivekggn.platform.heathcheck.HealthCheck.Level;
import j2html.tags.DomContent;
import j2html.tags.specialized.TdTag;
import j2html.tags.specialized.TrTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

@Controller
@RequestMapping(value = "/healthcheck")
public class HealthCheckController {

    private final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
    private final List<HealthCheck> healthChecks;
    private final GitInfo gitInfo;
    private final BuildInfo buildInfo;

    // OK response
    private final ResponseEntity<String> OK_RESPONSE =
            new ResponseEntity<String>("Ok", HttpStatus.OK);

    // failure response
    private final ResponseEntity<String> FAILED_RESPONSE =
            new ResponseEntity<String>("Failed", HttpStatus.SERVICE_UNAVAILABLE);


    // Spring will use this constructor if no health checks are found
    @SuppressWarnings("UnusedDeclaration")
    public HealthCheckController() {
        this(new ArrayList<>(), null);
    }

    @Autowired(required = false)
    public HealthCheckController(List<HealthCheck> healthChecks) {
        this(healthChecks, null);
    }

    @Autowired(required = false)
    public HealthCheckController(List<HealthCheck> healthChecks, GitInfo gitInfo) {
        this(healthChecks, gitInfo, null);
    }

    @Autowired(required = false)
    public HealthCheckController(List<HealthCheck> healthChecks, GitInfo gitInfo, BuildInfo buildInfo) {
        this.healthChecks = healthChecks;
        this.gitInfo = gitInfo;
        this.buildInfo = buildInfo;
    }

    /**
     * Core health check endpoint - only executes HIGH priority health checks
     *
     * @return an 'OK' response if all checks pass, or a 'Failed' response if any checks fail
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity<String> execute() {
        return executeForLevels(Level.HIGH);
    }


    /**
     * Core health check endpoint - only executes HIGH and MEDIUM priority health checks
     *
     * @return an 'OK' response if all checks pass, or a 'Failed' response if any checks fail
     */
    @RequestMapping(params = "warn")
    @ResponseBody
    public ResponseEntity<String> executeWithWarn() {
        return executeForLevels(Level.HIGH, Level.MEDIUM);
    }


    @RequestMapping(params = "details")
    @ResponseBody
    public String executeWithDetails(@RequestParam(required = false, defaultValue = "HIGH,MEDIUM") Level[] levels) {

        List<DomContent> rows = getHealthChecksByLevel(levels)
                .parallelStream()
                .map(this::tableRow)
                .collect(Collectors.toList());

        return html(
                head(
                        title("Health Check Details"),
                        style(rawHtml(STYLE))
                ),
                body(
                        div(
                                h1("Health Check Details"),
                                h2("Dependency Status"),
                                table(
                                        thead(
                                                tr(
                                                        th("Dependency Name"),
                                                        th("Level"),
                                                        th("Details")
                                                )
                                        ),
                                        tbody(
                                                tr(
                                                        each(rows, Function.identity())
                                                )
                                        )
                                ).withClass("table"),
                                h2("Build Info"),
                                buildInfo(),
                                h2("Git Info"),
                                gitInfo()

                        ).withClass("container smooth")
                )

        ).render();
    }

    @RequestMapping(params = "json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Map<String, String>> executeWithJson(@RequestParam(required = false, defaultValue = "HIGH,MEDIUM")
                                                                    Level[] levels) {


        Map<String, Map<String, String>> detailsMaps = new HashMap<>();

        getHealthChecksByLevel(levels)
                .parallelStream()
                .forEach(healthCheck -> {

                    HealthCheck hc = healthCheck;

                    try {
                        healthCheck.execute();
                    } catch (HealthCheckFailedException failed) {
                        hc = failed.getHealthCheck();
                        logger.error("HealthCheckFailure", failed);
                    } catch (HealthCheckWarnException warned) {
                        hc = warned.getHealthCheck();
                        logger.error("HealthCheckWarning", warned);
                    }

                    Map<String, String> healthCheckDetails = hc.getDetailsMap();
                    if (null != healthCheckDetails && !healthCheckDetails.isEmpty()) {
                        detailsMaps.put(hc.getName(), healthCheckDetails);
                    }
                });

        return detailsMaps;
    }


    /**
     * Get all health checks matching a particular level
     * @param levels the levels to check
     * @return the matching health checks
     */
    private List<HealthCheck> getHealthChecksByLevel(Level... levels) {
        if (levels == null || levels.length == 0) {
            return healthChecks;
        }

        List<Level> levelList = Arrays.asList(levels);

        return healthChecks.stream()
                .filter(hc -> levelList.contains(hc.getLevel()))
                .collect(Collectors.toList());
    }

    /**
     * Execute health checks for the specified Level(s)
     *
     * @param levels the levels
     * @return the OK response if all succeed, or the failure response if any one fails
     */
    private ResponseEntity<String> executeForLevels(Level... levels) {

        boolean isFailed = getHealthChecksByLevel(levels)
                .parallelStream()
                .anyMatch(hc -> {
                    try {
                        hc.execute();
                        return false;
                    } catch (HealthCheckFailedException failedException) {
                        logger.error("HealthCheckFailure", failedException);
                        return true;
                    } catch (HealthCheckWarnException warnException) {
                        logger.error("HealthCheckWarning", warnException);
                        return true;
                    }
                });

        return isFailed ? FAILED_RESPONSE : OK_RESPONSE;
    }

    private DomContent buildInfo() {
        if (buildInfo == null) return div("No build info");
        return ul(
                li("description: " + buildInfo.getDescription()),
                li("version: " + buildInfo.getVersion())
        );
    }

    private DomContent gitInfo() {
        if (gitInfo == null) return div("No git info");
        return ul(
                li("branch: " + gitInfo.getBranch()),
                li("commit: " + gitInfo.getCommit()),
                li("committer: " + gitInfo.getCommiter()),
                li("message: " + gitInfo.getCommiteMessage()),
                li("date: " + gitInfo.getDate())
        );
    }

    private DomContent tableRow(HealthCheck healthCheck) {
        try {
            healthCheck.execute();
            return getTr(healthCheck, "Succeeded", "healthy");
        } catch (HealthCheckFailedException e) {
            logger.error("HealthCheckFailure", e);
            return getTr(e.getHealthCheck(), "Failed", "failure");
        } catch (HealthCheckWarnException w) {
            logger.error("HealthCheckWarning", w);
            return getTr(w.getHealthCheck(), "Warned", "warning");
        }
    }

    private TrTag getTr(HealthCheck healthCheck, String status, String statusCls) {
        return tr(
                td(healthCheck.getName()),
                td(healthCheck.getLevel().name()),
                getDetailsTd(healthCheck, status, statusCls)
        );
    }

    private TdTag getDetailsTd(HealthCheck healthCheck, String status, String statusCls) {
        return td(
                div(status + ": " + healthCheck.getDetails()),
                div(
                        table(
                                each(healthCheck.getDetailsMap().entrySet(), this::getDetailRow)
                        )
                ).withClasses("details")
        ).withClass(statusCls);
    }

    private TrTag getDetailRow(Map.Entry<String, String> entry) {
        return tr(
                td(entry.getKey() + ": "),
                td(entry.getValue())
        );
    }

    private static final String STYLE =
            "                       body, textarea, input, select {\n" +
                    "                    background: 0;\n" +
                    "                    border-radius: 0;\n" +
                    "                    font: 16px sans-serif;\n" +
                    "                    margin: 0\n" +
                    "                }\n" +
                    "\n" +
                    "                .addon, .btn-sm, .nav, textarea, input, select {\n" +
                    "                    outline: 0;\n" +
                    "                    font-size: 14px\n" +
                    "                }\n" +
                    "\n" +
                    "                .smooth {\n" +
                    "                    transition: all .2s\n" +
                    "                }\n" +
                    "\n" +
                    "                .btn, .nav a {\n" +
                    "                    text-decoration: none\n" +
                    "                }\n" +
                    "\n" +
                    "                .container {\n" +
                    "                    margin: auto;\n" +
                    "                    width: 960px\n" +
                    "                }\n" +
                    "\n" +
                    "                .btn, h2 {\n" +
                    "                    font-size: 2em\n" +
                    "                }\n" +
                    "\n" +
                    "                h1 {\n" +
                    "                    font-size: 3em\n" +
                    "                }\n" +
                    "\n" +
                    "                .table { \n" +
                    "                   border-collapse: collapse;\n " +
                    "                 }\n" +
                    "\n" +
                    "                .table th, .table td {\n" +
                    "                    padding: .5em;\n" +
                    "                    text-align: left;\n" +
                    "                    vertical-align: top;\n" +
                    "                    border: 1px solid #999;\n" +
                    "                }\n" +
                    "\n" +
                    "                .table > tbody > tr:nth-child(2n-2) {\n" +
                    "                    background: #ddd\n" +
                    "                }\n" +
                    "\n" +
                    "                .healthy, .healthy table tr td {\n" +
                    "                    color: #009900;\n" +
                    "                }\n" +
                    "\n" +
                    "                .warning, .warning table tr td {\n" +
                    "                    color: #debd06;\n" +
                    "                }\n" +
                    "\n" +
                    "                .failure, .failure table tr td {\n" +
                    "                   color: #d61d1d;\n" +
                    "                }\n" +
                    "\n" +
                    "                div.details { \n" +
                    "                   font-size: .8em;\n" +
                    "                   border-left: 1px solid;\n" +
                    "                   margin-top: 5px;\n" +
                    "                } \n" +
                    "\n" +
                    "                div.details table { \n" +
                    "                   font-size: inherit;\n" +
                    "                }\n" +
                    "\n" +
                    "                div.details td { \n" +
                    "                   border: 0px;\n" +
                    "                }\n" +
                    "\n";


    public static void main(String[] args) {
        System.out.println(STYLE);
    }
}
