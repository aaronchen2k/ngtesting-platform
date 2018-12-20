package com.ngtesting.platform.action.client;

import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.action.BaseAction;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.TstProject;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.intf.ReportIssueService;
import com.ngtesting.platform.service.intf.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(Constant.API_PATH_CLIENT + "report_issue/")
public class ReportIssueAction extends BaseAction {

    @Autowired
    ReportIssueService reportIssueService;

    @Autowired
    ProjectService projectService;

    @PostMapping(value = "orgIssue")
    @ResponseBody
    public Map<String, Object> orgIssue(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();

        TstUser user = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_PROFILE);
        Integer orgId = user.getDefaultOrgId();
        Integer prjId = user.getDefaultPrjId();

        Integer projectId = json.getInteger("orgId");
        if (userNotInOrg(user.getId(), orgId)) {
            return authFail();
        }

        Map trendData =
                reportIssueService.chartIssueTrend(projectId, TstProject.ProjectType.org, 14);
        Map ageData =
                reportIssueService.chartIssueAge(projectId, TstProject.ProjectType.org, 14,
                        orgId, prjId);
        List distribDataByPriority =
                reportIssueService.chartIssueDistribByPriority(projectId, TstProject.ProjectType.org);
        List distribDataByStatus =
                reportIssueService.chartIssueDistribByStatus(projectId, TstProject.ProjectType.org);

        data.put("trend", trendData);
        data.put("age", ageData);
        data.put("distribByPriority", distribDataByPriority);
        data.put("distribByStatus", distribDataByStatus);

        ret.put("data", data);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "projectIssue")
    @ResponseBody
    public Map<String, Object> projectIssue(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();

        TstUser user = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_PROFILE);
        Integer orgId = user.getDefaultOrgId();
        Integer prjId = user.getDefaultPrjId();

        Integer projectId = json.getInteger("projectId");
        TstProject prj = projectService.get(projectId);

        Map trendData =
                reportIssueService.chartIssueTrend(projectId, prj.getType(), 14);
        Map ageData =
                reportIssueService.chartIssueAge(projectId, prj.getType(), 7, orgId, prjId);
        List distribDataByPriority =
                reportIssueService.chartIssueDistribByPriority(projectId, prj.getType());
        List distribDataByStatus =
                reportIssueService.chartIssueDistribByStatus(projectId, prj.getType());

        data.put("trend", trendData);
        data.put("age", ageData);
        data.put("distribByPriority", distribDataByPriority);
        data.put("distribByStatus", distribDataByStatus);

        ret.put("data", data);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

}